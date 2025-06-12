// File: com/example/course_app/CatatanActivity.java

package com.example.course_app;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class CatatanActivity extends AppCompatActivity {

    private EditText editTextNoteTitle, editTextNoteContent;
    private Button buttonSaveNote, buttonDeleteNote, buttonClearInput;
    private ListView listViewNotes;

    private DatabaseHelper dbHelper;
    private ArrayAdapter<String> notesAdapter;
    private List<String> noteDisplayList; // List untuk ditampilkan di ListView
    private List<Long> noteIdList;       // List untuk menyimpan ID asli dari database

    private Long selectedNoteId = -1L; // Menyimpan ID catatan yang sedang dipilih

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catatan);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // Inisialisasi Database Helper
        dbHelper = new DatabaseHelper(this);

        // Hubungkan komponen UI dengan variabel
        editTextNoteTitle = findViewById(R.id.editTextNoteTitle);
        editTextNoteContent = findViewById(R.id.editTextNoteContent);
        buttonSaveNote = findViewById(R.id.buttonSaveNote);
        buttonDeleteNote = findViewById(R.id.buttonDeleteNote);
        buttonClearInput = findViewById(R.id.buttonClearInput);
        listViewNotes = findViewById(R.id.listViewNotes);

        // Setup ListView
        noteDisplayList = new ArrayList<>();
        noteIdList = new ArrayList<>();
        notesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, noteDisplayList);
        listViewNotes.setAdapter(notesAdapter);

        // Muat data dari database ke ListView saat pertama kali dibuka
        loadNotesIntoListView();

        // Atur listener untuk tombol-tombol
        buttonSaveNote.setOnClickListener(v -> saveOrUpdateNote());
        buttonDeleteNote.setOnClickListener(v -> deleteSelectedNote());
        buttonClearInput.setOnClickListener(v -> clearInputFields());

        // Atur listener ketika item di ListView diklik
        listViewNotes.setOnItemClickListener((parent, view, position, id) -> {
            // Dapatkan ID catatan yang sebenarnya dari list
            selectedNoteId = noteIdList.get(position);
            // Ambil data catatan dari DB berdasarkan ID
            loadNoteDataIntoFields(selectedNoteId);
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish(); // Menutup activity ini dan kembali ke activity sebelumnya (MainActivity)
        return true;
    }

    private void loadNotesIntoListView() {
        // Bersihkan list sebelum memuat data baru
        noteDisplayList.clear();
        noteIdList.clear();

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelper.TABLE_NOTES, null, null, null, null, null, null);

        while (cursor.moveToNext()) {
            long id = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID));
            String title = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TITLE));

            noteIdList.add(id);
            noteDisplayList.add(title); // Hanya tampilkan judul di list
        }
        cursor.close();
        db.close();

        // Beri tahu adapter bahwa data telah berubah
        notesAdapter.notifyDataSetChanged();
    }

    private void loadNoteDataIntoFields(long noteId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelper.TABLE_NOTES,
                new String[]{DatabaseHelper.COLUMN_TITLE, DatabaseHelper.COLUMN_CONTENT},
                DatabaseHelper.COLUMN_ID + "=?",
                new String[]{String.valueOf(noteId)},
                null, null, null);

        if (cursor.moveToFirst()) {
            String title = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TITLE));
            String content = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CONTENT));
            editTextNoteTitle.setText(title);
            editTextNoteContent.setText(content);
        }
        cursor.close();
        db.close();
    }

    private void saveOrUpdateNote() {
        String title = editTextNoteTitle.getText().toString().trim();
        String content = editTextNoteContent.getText().toString().trim();

        if (title.isEmpty() || content.isEmpty()) {
            Toast.makeText(this, "Judul dan Isi tidak boleh kosong", Toast.LENGTH_SHORT).show();
            return;
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_TITLE, title);
        values.put(DatabaseHelper.COLUMN_CONTENT, content);

        if (selectedNoteId == -1L) {
            // CREATE: Tidak ada catatan yang dipilih, buat baru
            long newRowId = db.insert(DatabaseHelper.TABLE_NOTES, null, values);
            if (newRowId != -1) {
                Toast.makeText(this, "Catatan berhasil disimpan!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Gagal menyimpan catatan.", Toast.LENGTH_SHORT).show();
            }
        } else {
            // UPDATE: Ada catatan yang dipilih, perbarui
            int rowsAffected = db.update(DatabaseHelper.TABLE_NOTES, values, DatabaseHelper.COLUMN_ID + "=?", new String[]{String.valueOf(selectedNoteId)});
            if (rowsAffected > 0) {
                Toast.makeText(this, "Catatan berhasil diperbarui!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Gagal memperbarui catatan.", Toast.LENGTH_SHORT).show();
            }
        }
        db.close();

        // Muat ulang data ke list dan bersihkan input
        loadNotesIntoListView();
        clearInputFields();
    }

    private void deleteSelectedNote() {
        if (selectedNoteId == -1L) {
            Toast.makeText(this, "Pilih catatan yang ingin dihapus", Toast.LENGTH_SHORT).show();
            return;
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rowsDeleted = db.delete(DatabaseHelper.TABLE_NOTES, DatabaseHelper.COLUMN_ID + "=?", new String[]{String.valueOf(selectedNoteId)});
        db.close();

        if (rowsDeleted > 0) {
            Toast.makeText(this, "Catatan berhasil dihapus!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Gagal menghapus catatan.", Toast.LENGTH_SHORT).show();
        }

        // Muat ulang data ke list dan bersihkan input
        loadNotesIntoListView();
        clearInputFields();
    }

    private void clearInputFields() {
        selectedNoteId = -1L; // Reset pilihan
        editTextNoteTitle.setText("");
        editTextNoteContent.setText("");
        editTextNoteTitle.requestFocus(); // Fokuskan kursor ke judul
    }
}