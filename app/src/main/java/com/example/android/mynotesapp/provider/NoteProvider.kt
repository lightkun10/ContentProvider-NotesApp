package com.example.android.mynotesapp.provider

import android.content.ContentProvider
import android.content.ContentValues
import android.content.Context
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri
import com.example.android.mynotesapp.db.DatabaseContract.AUTHORITY
import com.example.android.mynotesapp.db.DatabaseContract.NoteColumns.Companion.CONTENT_URI
import com.example.android.mynotesapp.db.DatabaseContract.NoteColumns.Companion.TABLE_NAME
import com.example.android.mynotesapp.db.NoteHelper

class NoteProvider : ContentProvider() {

    // Deklarasi variabel
    companion object {

        /* Integer digunakan sebagai identifier antara select all
           atau select by id */
        private const val NOTE = 1
        private const val NOTE_ID = 2
        private lateinit var noteHelper: NoteHelper

        private val sUriMatcher = UriMatcher(UriMatcher.NO_MATCH)   // utk membandingkan uri dgn nilai integer tertentu

        /*
        TODO UriMatcher untuk mempermudah identifier dengan menggunakan integer,
        dibanding dengan string yg lebih kompleks.
        */

        init {
            // content://com.example.android.mynotesapp/note
            sUriMatcher.addURI(AUTHORITY, TABLE_NAME, NOTE)     // uri com.example.android.mynotesapp/note
                                                                // dicocokan dengan integer 1

            // content://com.example.android.mynotesapp/note/id
            sUriMatcher.addURI(AUTHORITY, "$TABLE_NAME/#", NOTE_ID)    // uri com.example.android.mynotesapp/note/#
                                                                       // dicocokan dengan integer 2.
                                                                       // Tanda "#" akan diganti dgn id tertentu
        }
    }


    override fun onCreate(): Boolean {
        // "Implement this to initialize your content provider on startup."
        noteHelper = NoteHelper.getInstance(context as Context)
        noteHelper.open()
        return true
    }

    /*
    Method queryAll digunakan ketika ingin menjalankan queryAll Select
    Return cursor
    */
    override fun query(
        uri: Uri, projection: Array<String>?, selection: String?,
        selectionArgs: Array<String>?, sortOrder: String?
    ): Cursor? {
        // "Implement this to handle query requests from clients."
        // Setiap proses query, insert, update, dan delete kita wajib membuka noteHelper-nya.
        val cursor: Cursor?
        when (sUriMatcher.match(uri)) {
            NOTE -> cursor = noteHelper.queryAll()
            NOTE_ID -> cursor = noteHelper.queryById(uri.lastPathSegment.toString())
            else -> cursor = null
        }
        /* Di sinilah proses identifikasi obyek Uri berlangsung. Dengan menggunakan switch,
           user bisa mendptkan data mana yg sebenarnya akan diakses oleh obyek Uri.
           Begitu juga dengan metode Insert, Update, dan Delete, semuanya menggunakan proses identifikasi obyek Uri.  */

        return cursor
    }

    override fun getType(uri: Uri): String? {
        // "Implement this to handle requests for the MIME type of the data at the given URI."
        return null
    }

    override fun insert(uri: Uri, contentValues: ContentValues?): Uri? {
        // Implement this to handle requests to insert a new row.
        val added: Long = when (NOTE) {
            sUriMatcher.match(uri) -> noteHelper.insert(contentValues) // Lalukan operasi, yg datanya diambil dari
                                                                       // request contentResolver pd NoteAddUpdateActivity.
                                                                       // Operasi lalu "diteruskan" ke noteHelper.
            else -> 0

            /* Jadi, content provider hanya sebagai jembatan sebelum mengakses noteHelper. Namun kelebihan
           dari penggunaan Content Provider yaitu datanya bisa diakses dari aplikasi lain.*/
        }

        context?.contentResolver?.notifyChange(CONTENT_URI, null)   // memberitahu kalau ada perubahan data.
        // Fungsi ini akan mengirim pesan kepada semua aplikasi yang mengakses data dari content provider ini.

        return Uri.parse("$CONTENT_URI/$added")
    }

    override fun update(
        uri: Uri, contentValues: ContentValues?, selection: String?,
        selectionArgs: Array<String>?
    ): Int {
        // Implement this to handle requests to update one or more rows.
        val updated: Int = when (NOTE_ID) {
            sUriMatcher.match(uri) -> noteHelper.update(uri.lastPathSegment.toString(), contentValues)
            else -> 0
        }

        // Let the "other" apps request the change
        context?.contentResolver?.notifyChange(CONTENT_URI, null)

        return updated
    }

    override fun delete(uri: Uri, s: String?, strings: Array<String>?): Int {
        // Implement this to handle requests to delete one or more rows
        val deleted = when (NOTE_ID) {
            sUriMatcher.match(uri) -> noteHelper.deleteById(uri.lastPathSegment.toString())
            else -> 0
        }

        // Let the "other" apps request the change
        context?.contentResolver?.notifyChange(CONTENT_URI, null)

        return deleted
    }
}
