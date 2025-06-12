package com.example.course_app;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;


public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 101;

    private TextView textViewSensor;
    private TextView textViewLocation;
    private Button buttonNavigate;
    private Button buttonAddNote;

    private SensorManager sensorManager;
    private Sensor lightSensor;
    private FusedLocationProviderClient fusedLocationClient;
    private Location lastKnownLocation;
    private LocationCallback locationCallback;
    private Button buttonGoToNotes;
    private TextView textViewDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textViewSensor = findViewById(R.id.textViewSensor);
        textViewLocation = findViewById(R.id.textViewLocation);
        buttonNavigate = findViewById(R.id.buttonNavigate);
        buttonGoToNotes = findViewById(R.id.buttonGoToNotes);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        if (lightSensor == null) {
            textViewSensor.setText("Sensor cahaya tidak tersedia.");
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // --- Perubahan Logika Lokasi ada di sini ---
        createLocationCallback();
        checkLocationPermission();
        // -------------------------------------------

        textViewDate = findViewById(R.id.textViewDate);
        setCurrentDate();
        buttonGoToNotes.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CatatanActivity.class);
            startActivity(intent);
        });
        buttonNavigate.setOnClickListener(v -> startNavigation());
    }

    /**
     * Method ini dipanggil untuk membuat dan menampilkan menu di app bar.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    /**
     * Method ini dipanggil setiap kali sebuah item menu diklik.
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Cek apakah item yang diklik adalah item logout
        if (item.getItemId() == R.id.action_logout) {
            performLogout(); // Panggil fungsi logout
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Method untuk menjalankan proses logout.
     */
    private void performLogout() {
        // Buat Intent untuk kembali ke LoginActivity
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);

        // **PENTING**: Tambahkan flag ini untuk membersihkan semua histori activity sebelumnya.
        // Ini memastikan pengguna tidak bisa menekan tombol "Back" untuk kembali ke MainActivity setelah logout.
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        startActivity(intent);

        // Tampilkan pesan konfirmasi
        Toast.makeText(this, "Anda telah berhasil logout.", Toast.LENGTH_SHORT).show();

        // Tutup MainActivity
        finish();
    }

    private void setCurrentDate() {
        // Format: Nama Hari, Tanggal Bulan Tahun (misal: Jumat, 13 Juni 2025)
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, dd MMMM yyyy", new Locale("id", "ID"));
        String todayDate = dateFormat.format(new Date());
        textViewDate.setText(todayDate);
    }

    private void checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            // Langsung minta update lokasi baru
            startLocationUpdates();
        }
    }

    // FUNGSI BARU untuk meminta update lokasi
    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationRequest locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000) // 10 detik
                    .setMinUpdateIntervalMillis(5000) // 5 detik
                    .build();

            fusedLocationClient.requestLocationUpdates(locationRequest,
                    locationCallback,
                    Looper.getMainLooper());
        }
    }

    // FUNGSI BARU untuk membuat callback
    private void createLocationCallback() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        lastKnownLocation = location;
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();
                        textViewLocation.setText("Lat: " + latitude + "\nLon: " + longitude);

                        // Setelah dapat lokasi, hentikan update untuk hemat baterai
                        stopLocationUpdates();
                        break; // Keluar dari loop setelah dapat lokasi pertama
                    }
                }
            }
        };
    }

    // FUNGSI BARU untuk menghentikan update lokasi
    private void stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationUpdates();
            } else {
                textViewLocation.setText("Izin lokasi ditolak.");
                Toast.makeText(this, "Aplikasi butuh izin lokasi untuk berfungsi.", Toast.LENGTH_LONG).show();
            }
        }
    }

    // ... (Fungsi lainnya tetap sama) ...
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
            float lightValue = event.values[0];
            textViewSensor.setText("Tingkat Cahaya: " + lightValue + " lx");
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) { }

    private void startNavigation() {
        if (lastKnownLocation != null) {
            double lat = lastKnownLocation.getLatitude();
            double lon = lastKnownLocation.getLongitude();

            // Format geo URI untuk menampilkan pin di peta dengan label
            String geoUri = "geo:" + lat + "," + lon + "?q=" + lat + "," + lon + "(Lokasi Anda Saat Ini)";
            Uri gmmIntentUri = Uri.parse(geoUri);

            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");

            if (mapIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(mapIntent);
            } else {
                Toast.makeText(this, "Google Maps tidak terinstall.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Lokasi belum ditemukan untuk ditampilkan di peta.", Toast.LENGTH_SHORT).show();
        }
    }

    private void addSampleNote() {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_TITLE, "Catatan Baru");
        values.put(DatabaseHelper.COLUMN_CONTENT, "Ini adalah isi dari catatan yang dibuat pada " + System.currentTimeMillis());
        long newRowId = db.insert(DatabaseHelper.TABLE_NOTES, null, values);
        db.close();
        if (newRowId != -1) {
            Toast.makeText(this, "Catatan berhasil ditambahkan!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Gagal menambahkan catatan.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (lightSensor != null) {
            sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
        // Jika perlu, Anda bisa memulai update lokasi lagi saat aplikasi kembali dibuka
        // checkLocationPermission();
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
        // Selalu hentikan update lokasi saat aplikasi tidak aktif
        stopLocationUpdates();
    }
}