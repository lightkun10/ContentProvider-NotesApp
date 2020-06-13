package com.example.android.mynotesapp.db

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.provider.BaseColumns._ID
import com.example.android.mynotesapp.db.DatabaseContract.NoteColumns.Companion.TABLE_NAME
import java.sql.SQLException

/* TODO: DML (Data Manipulation Language) is used to access, modify or retrieve the data from the database.
 *
 * Tugas utama dari kelas di atas adalah melakukan proses manipulasi data
 * yang berada di dalam tabel seperti query untuk pembacaan data yang diurutkan
 * secara ascending, penyediaan fungsi pencarian catatan berdasarkan judul,
 * pembaruan catatan, dan penghapusan catatan.*/


// Kelas di bawah menggunakan sebuah pattern yg bernama Singleton Pattern.
// Dengan singleton sebuah objek hanya bisa memiliki sebuah instance,
// sehingga tidak terjadi duplikasi instance.
// Lebih lengkap: https://en.wikipedia.org/wiki/Singleton_pattern
class NoteHelper(context: Context) {
    private var databaseHelper: DatabaseHelper = DatabaseHelper(context)
    private lateinit var database: SQLiteDatabase

    companion object {
        private const val DATABASE_TABLE = TABLE_NAME
        private var INSTANCE: NoteHelper? = null

        /* Metode yg nantinya akan digunakan untuk menginisiasi database */
        fun getInstance(context: Context): NoteHelper =
                INSTANCE ?: synchronized(this) {     // left if elvis op(?:) not null, right otherwise
                    INSTANCE ?: NoteHelper(context)  // same as above
            /*
            Synchronized di sini dipakai untuk menghindari duplikasi instance
            di semua Thread, karena bisa saja kita membuat instance di Thread yg berbeda.
            */
        }
    }

    /* Metode untuk membuka koneksi ke database */
    fun open() {
        database = databaseHelper.writableDatabase
    }

    /* Metode menutup koneksi ke database */
    @Throws(SQLException::class)
    fun close() {
        databaseHelper.close()

        if (database.isOpen) {
            database.close()
        }
    }


    /**
     * Ambil data dari semua note yang ada di dalam database
     *
     * @return cursor hasil queryAll
     */
    fun queryAll(): Cursor {
        return database.query(
            DATABASE_TABLE,
            null,
            null,
            null,
            null,
            null,
            "$_ID ASC")
    }

    /**
     * Ambil data dari note berdasarakan parameter id
     *
     * @param id id note yang dicari
     * @return cursor hasil queryAll
     */
    fun queryById(id: String): Cursor {
        return database.query(
            DATABASE_TABLE,
            null,
            "$_ID = ?",
            arrayOf(id),
            null,
            null,
            null,
            null)
    }

    /**
     * Simpan data ke dalam database
     *
     * @param values nilai data yang akan di simpan
     * @return long id dari data yang baru saja di masukkan
     */
    fun insert(values: ContentValues?): Long {
        return database.insert(DATABASE_TABLE, null, values)
    }

    /**
     * Update/pembaharuan data dalam database
     *
     * @param id     data dengan id berapa yang akan di update
     * @param values nilai data baru
     * @return int jumlah data yang ter-update
     */
    fun update(id: String, values: ContentValues?): Int {
        return database.update(DATABASE_TABLE, values, "$_ID = ?", arrayOf(id))
    }

    /**
     * Delete data dalam database
     *
     * @param id data dengan id berapa yang akan di delete
     * @return int jumlah data yang ter-delete
     */
    fun deleteById(id: String): Int {
        return database.delete(DATABASE_TABLE, "$_ID = '$id'", null)
    }
}