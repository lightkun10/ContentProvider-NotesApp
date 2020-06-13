package com.example.android.mynotesapp.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.android.mynotesapp.db.DatabaseContract.NoteColumns.Companion.TABLE_NAME


/* TODO NOTE:
    DDL (Data Definition Language) is used to Specify the database schema database structure.

Kelas ini mengakomodasi kebutuhan DDL.
Tanggung jawab utama dari kelas di atas adalah menciptakan database
dengan tabel yg dibutuhkan dan handle ketika terjadi perubahan
skema pada tabel (terjadi pada metode onUpgrade()).
*/

internal class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        private const val DATABASE_NAME = "dbnoteapp"

        private const val DATABASE_VERSION = 1

        /* Constructor */
        private val SQL_CREATE_TABLE_NOTE = "CREATE TABLE $TABLE_NAME" +
                " (${DatabaseContract.NoteColumns._ID} INTEGER PRIMARY KEY AUTOINCREMENT," +
                " ${DatabaseContract.NoteColumns.TITLE} TEXT NOT NULL," +
                " ${DatabaseContract.NoteColumns.DESCRIPTION} TEXT NOT NULL," +
                " ${DatabaseContract.NoteColumns.DATE} TEXT NOT NULL)"
        /*
        Kelas ini menggunakan variabel yang ada pada DatabaseContract untuk mengisi
        kolom nama tabel. Begitu juga dengan kelas-kelas lainnya nanti. Dengan
        memanfaatkan kelas contract, maka akses nama tabel dan nama kolom tabel menjadi lebih mudah.
        */
    }

    /* Constructor and method implementation for SQLiteOpenHelper */
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_TABLE_NOTE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }
}