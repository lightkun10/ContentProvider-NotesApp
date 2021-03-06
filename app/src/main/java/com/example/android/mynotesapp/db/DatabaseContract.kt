package com.example.android.mynotesapp.db

import android.net.Uri
import android.provider.BaseColumns

/*
Kelas contract ini akan digunakan untuk mempermudah
akses nama tabel dan nama kolom di dalam database.
*/

object DatabaseContract {

    // Authority yang digunakan
    const val AUTHORITY = "com.example.android.mynotesapp"  // Variabel AUTHORITY merupakan base authority yg
    const val SCHEME = "content"                            // akan digunakan untuk mengidentifikasi bahwa
                                                            // provider NoteProvider milik MyNotesApp yg akan diakses.

    // TODO: Pastikan authorities pada provider di file manifest sama dengan isi variabel AUTHORITY
    // agar akses bisa terjadi.

    /*
    Penggunaan Base Columns akan memudahkan dalam penggunaan suatu table
    Untuk id yang autoincrement sudah default ada di dalam kelas BaseColumns
    dengan nama field _ID
    */
    class NoteColumns : BaseColumns {

        companion object {
            const val TABLE_NAME = "note"           // variabel2 ini akan mempermudah dalam memanggil
            const val _ID = "_id"                   // nama tabel dan nama kolom tabel. Dengan hanya
            const val TITLE = "title"               // membuat 1 kelas berisikan variable tersebut maka
            const val DESCRIPTION = "description"   // dapat di jaga konsistensi dalam penamaan ketika
            const val DATE = "date"                 // aplikasi ingin mengakses tabel tersebut.

            // Id tidak perlu didefinisikan di sini krn kolom _ID sdh ada secara default di dlm kelas BaseColumns.
            // Sudah ada suatu aturan lumrah untuk menggunakan _ID untuk komponen lainnya.

            // Base content yang digunakan untuk akses content provider
            val CONTENT_URI: Uri = Uri.Builder().scheme(SCHEME)   // Di sini user menggabungkan base authority dgn
                .authority(AUTHORITY)                             // scheme dan nama tabel, nanti string yg tercipta
                .appendPath(TABLE_NAME)                           // adlh "content://com.dicoding.mynotesapp/note".
                .build()
            // Artinya dari string "content://com.dicoding.mynotesapp/note" berarti user akan
            // mencoba untuk akses data tabel Note dari provider NoteProvider.
        }

    }
}