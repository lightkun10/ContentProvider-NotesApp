package com.example.android.consumerapp.helper

import android.database.Cursor
import com.example.android.consumerapp.db.DatabaseContract
import com.example.android.consumerapp.entity.Note

object MappingHelper {

    fun mapCursorToArrayList(notesCursor: Cursor?): ArrayList<Note> {
        val notesList = ArrayList<Note>()

        notesCursor?.apply {            // Fungsi apply untuk menyederhanakan kode yg berulang.
            while (moveToNext()) {      // MoveToNext untuk memindahkan cursor ke baris selanjutnya
                val id = getInt(getColumnIndexOrThrow(DatabaseContract.NoteColumns._ID))
                val title = getString(getColumnIndexOrThrow(DatabaseContract.NoteColumns.TITLE))
                val description = getString(getColumnIndexOrThrow(DatabaseContract.NoteColumns.DESCRIPTION))
                val date = getString(getColumnIndexOrThrow(DatabaseContract.NoteColumns.DATE))

                notesList.add(Note(id, title, description, date))
                /* Di sini data diambil satu per satu & dimasukkan ke dlm ArrayList. */
            }
        }

        return notesList
    }

    /*
    fungsi tambahan untuk konversi dari cursor menjadi object yang
    akan digunakan di kelas NoteAddUpdateActivity nanti.
    */
    fun mapCursorToObject(notesCursor: Cursor?): Note {
        var note = Note()

        notesCursor?.apply {
            moveToFirst()
            val id = getInt(getColumnIndexOrThrow(DatabaseContract.NoteColumns._ID))
            val title = getString(getColumnIndexOrThrow(DatabaseContract.NoteColumns.TITLE))
            val description = getString(getColumnIndexOrThrow(DatabaseContract.NoteColumns.DESCRIPTION))
            val date = getString(getColumnIndexOrThrow(DatabaseContract.NoteColumns.DATE))
            note = Note(id, title, description, date)
        }

        return note
    }
}