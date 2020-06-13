package com.example.android.consumerapp

import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.android.consumerapp.db.DatabaseContract
import com.example.android.consumerapp.db.DatabaseContract.NoteColumns.Companion.CONTENT_URI
import com.example.android.consumerapp.db.DatabaseContract.NoteColumns.Companion.DATE
import com.example.android.consumerapp.entity.Note
import com.example.android.consumerapp.helper.MappingHelper
import kotlinx.android.synthetic.main.activity_note_add_update.*
import java.text.SimpleDateFormat
import java.util.*

/* TODO Tanggung jawab utama NoteAddUpdateActivity adalah:
-  Menyediakan form untuk melakukan proses input data,
-  Menyediakan form untuk melakukan proses pembaruan data,
-  Jika pengguna berada pada proses pembaruan data maka setiap
   kolom pada form sudah terisi otomatis dan ikon untuk hapus yang
   berada pada sudut kanan atas ActionBar ditampilkan dan berfungsi
   untuk menghapus data.
-  Sebelum proses penghapusan data, dialog konfirmasi akan tampil.
   Pengguna akan ditanya terkait penghapusan yang akan dilakukan.
-  Jika pengguna menekan tombol back (kembali) baik pada ActionBar
   maupun peranti, maka akan tampil dialog konfirmasi sebelum menutup halaman.
-  Masih ingat materi di mana sebuah Activity menjalankan Activity lain dan
   menerima nilai balik pada metode onActivityResult()? Tepatnya di Activity
   yang dijalankan dan ditutup dengan menggunakan parameter REQUEST dan RESULTCODE.
   Jika lupa, sila kunjungi laman ini: https://www.dicoding.com/academies/14/tutorials/135
*/
/* TODO Secara fungsionalitas masih sama dengan sebelumnya, tetapi
    tidak menggunakan obyek Parcelable untuk ditampilkan di dalam
    NoteAddUpdateActivity, melainkan menggunakan Uri untuk ambil data
    kembali dari ContentProvider. Fungsi NoteHelper diubah menggunakan ContentProvider. */

class NoteAddUpdateActivity : AppCompatActivity(), View.OnClickListener {
    private var isEdit = false
    private var note: Note? = null
    private var position: Int = 0
    private lateinit var uriWithId: Uri

    companion object {
        const val EXTRA_NOTE = "extra_note"
        const val EXTRA_POSITION = "extra_position"
        const val REQUEST_ADD = 100
        const val RESULT_ADD = 101
        const val REQUEST_UPDATE = 200
        const val RESULT_UPDATE = 201
        const val RESULT_DELETE = 301
        const val ALERT_DIALOG_CLOSE = 10
        const val ALERT_DIALOG_DELETE = 20
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note_add_update)
        

        note = intent.getParcelableExtra(EXTRA_NOTE)            // Menerapkan data hasil dari intent.
                                                                // Dalam skenario ini data intent
                                                                // diambil dari NoteAdapter
        if (note != null) {
            position = intent.getIntExtra(EXTRA_POSITION, 0)    // Sama dengan intent EXTRA_NOTE diatas
            isEdit = true
        } else {
            note = Note()
        }

        val actionBarTitle: String
        val btnTitle: String

        if (isEdit) {
            // Uri yang di dapatkan disini akan digunakan untuk ambil data dari provider
            // content://com.example.android.mynotesapp/note/id
            uriWithId = Uri.parse(CONTENT_URI.toString() + "/" + note?.id)  // Uri yang dibuat adalah parsing tambah
                                                                            // string CONTENT_URI dgn string /id
            // TODO: menggunakan Uri untuk ambil data kembali dari ContentProvider
            val cursor = contentResolver.query(uriWithId, null, null, null, null)

            /* Content resolver akan meneruskan obyek Uri ke content provider dan
               akan masuk ke dalam metode query. uriWithId berarti memanggil query
               select dengan id tertentu. Lalu data akan diubah menjadi object
               agar bisa ditampilkan di dalam teks. */

            if (cursor != null) {
                MappingHelper.mapCursorToObject(cursor)     // Merubah data menjadi object
                cursor.close()
            }

            actionBarTitle = "Ubah"
            btnTitle = "Update"

            note?.let {
                edt_title.setText(it.title)
                edt_description.setText(it.description)
            }
        } else {
            actionBarTitle = "Tambah"
            btnTitle = "Simpan"
        }

        supportActionBar?.title = actionBarTitle
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        btn_submit.text = btnTitle
        btn_submit.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        if (view.id == R.id.btn_submit) {
            val title = edt_title.text.toString().trim()
            val description = edt_description.text.toString().trim()

            // Check if user doesn't input anything in title input section
            if (title.isEmpty()) {
                edt_title.error = "Field cannot be blank"
                return
            }

            // Change note title & description passed by adapter with value from title input
            note?.title = title
            note?.description = description

            val intent = Intent() // Ready intent
            intent.putExtra(EXTRA_NOTE, note)
            intent.putExtra(EXTRA_POSITION, position)

            val values = ContentValues()
            values.put(DatabaseContract.NoteColumns.TITLE, title)
            values.put(DatabaseContract.NoteColumns.DESCRIPTION, description)

            if (isEdit) {
                // Gunakan uriWithId untuk update
                // content://com.example.android.mynotesapp/note/id
                contentResolver.update(uriWithId, values, null,null)
                Toast.makeText(this, "Satu item berhasil diedit", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                values.put(DATE, getCurrentDate())
                // Gunakan content uri untuk insert
                // content://com.example.android.mynotesapp/note/note
                contentResolver.insert(CONTENT_URI, values)     /* metode insert dgn menggunakan
                                                                   getContentResolver dgn masukan
                                                                   CONTENT_URI & values yg berisi data.
                                                                   Kemudian fungsi ini akan memanggil
                                                                   metode insert di kelas NoteProvider. */

                Toast.makeText(this, "Satu item berhasil disimpan", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    // Untuk memanggil menu_form.xml
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        if (isEdit) {
            menuInflater.inflate(R.menu.menu_form, menu)
        }
        return super.onCreateOptionsMenu(menu)
    }

    // Untuk memberikan fungsi ketika menu diklik.
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_delete -> showAlertDialog(ALERT_DIALOG_DELETE)
            android.R.id.home -> showAlertDialog(ALERT_DIALOG_CLOSE)
        }

        return super.onOptionsItemSelected(item)
    }

    // Untuk mengambil tanggal dan jam.
    private fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault())
        val date = Date()

        return dateFormat.format(date)
    }

    // Pada saat menekan tombol back (kembali), memunculkan AlertDialog.
    override fun onBackPressed() {
        showAlertDialog(ALERT_DIALOG_CLOSE)
    }

    // Metode untuk memunculkan dialog ketika onBackPressed
    private fun showAlertDialog(type: Int) {
        val isDialogClose = type == ALERT_DIALOG_CLOSE  // Returns "true" if parameter
                                                        // is ALERT_DIALOG_CLOSE, "false" otherwise
        val dialogTitle: String
        val dialogMessage: String

        if (isDialogClose) {
            dialogTitle = "Batal"
            dialogMessage = "Apakah anda ingin membatalkan perubahan pada form?"
        } else {
            dialogMessage = "Apakah anda yakin ingin menghapus item ini?"
            dialogTitle = "Hapus Note"
        }

        /* Pada proses penghapusan data, dialog konfirmasi tampil.
           Ia pun muncul ketika pengguna menekan tombol back baik pada
           ActionBar atau peranti. Dialog konfirmasi tersebut muncul
           sebelum menutup halaman. Untuk itu, gunakan fasilitas
           AlertDialoguntuk menampilkan dialog. */
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setTitle(dialogTitle)
        alertDialogBuilder
            .setMessage(dialogMessage)
            .setCancelable(false)
            .setPositiveButton("Ya") { dialog, id ->
                if (isDialogClose) {
                    finish()
                } else {
                    // Gunakan uriWithId dari intent activity ini
                    // content://com.example.android.mynotesapp/note/id
                    contentResolver.delete(uriWithId, null, null)
                    Toast.makeText(this, "Satu item berhasil dihapus", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
            .setNegativeButton("Tidak") { dialog, _ -> dialog.cancel() }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }
}