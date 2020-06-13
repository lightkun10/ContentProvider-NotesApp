package com.example.android.mynotesapp

import android.content.Intent
import android.database.ContentObserver
import android.os.*
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.android.mynotesapp.adapter.NoteAdapter
import com.example.android.mynotesapp.db.DatabaseContract.NoteColumns.Companion.CONTENT_URI
import com.example.android.mynotesapp.db.NoteHelper
import com.example.android.mynotesapp.entity.Note
import com.example.android.mynotesapp.helper.MappingHelper
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

/* TODO Tugas utama MainActivity ada dua,
   1. Menampilkan data dari database pada tabel Note secara ascending,
   2. menerima nilai balik dari setiap aksi dan proses yang dilakukan
      di NoteAddUpdateActivity.

   DATABASE - NOTE(table)   --->   MainActivity   <---   NoteAddUpdateActivity
*/

class MainActivity : AppCompatActivity() {
    private lateinit var adapter: NoteAdapter

    companion object {
        private const val EXTRA_STATE = "EXTRA_STATE"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.title = "Notes"

        // Setting RecyclerView dan Adapter
        rv_notes.layoutManager = LinearLayoutManager(this)
        rv_notes.setHasFixedSize(true)
        adapter = NoteAdapter(this)
        rv_notes.adapter = adapter

        fab_add.setOnClickListener {
            val intent = Intent(this@MainActivity, NoteAddUpdateActivity::class.java)
            startActivityForResult(intent, NoteAddUpdateActivity.REQUEST_ADD)
        }

        /************************* Menangkap pesan dari contentResolver. *************************/

        /* Menciptakan thread baru untuk melihat perubahan (observe)
           supaya tidak mengganggu kinerja thread utama. */
        val handlerThread = HandlerThread("DataObserver")  // Inisiasi thread baru
        handlerThread.start()
        val handler = Handler(handlerThread.looper)

        // Fungsi turunan ContentObserver supaya bisa melakukan fungsi observe.
        val myObserver = object : ContentObserver(handler) {
            override fun onChange(self: Boolean) {
                loadNotesAsync()
            }
        }

        /*
        Mendaftarkan observer(myObserver), agar ketika terjadi perubahan data,
        kelas onChange akan terpanggil dan melakukan aksi tertentu
        */
        contentResolver.registerContentObserver(CONTENT_URI, true, myObserver)

        /*
        Cek jika savedInstaceState null, maka akan melakukan proses asynctask-nya.
        Jika tidak,akan mengambil arraylist nya dari yang sudah di simpan
        */
        if (savedInstanceState == null) {
            // proses ambil data
            loadNotesAsync()
        } else {
            val list = savedInstanceState.getParcelableArrayList<Note>(EXTRA_STATE)
            if (list != null) {
                adapter.listNotes = list
            }
        }
    }

    /* Fungsi ini digunakan utk load data dari tabel dan dan kemudian menampilkannya
       ke dlm list secara asynchronous dgn menggunakan Background process.
    */
    private fun loadNotesAsync() {
        /* Mulai mengambil data dari database dgn menggunakan background thread.  */
        GlobalScope.launch(Dispatchers.Main) {
            progressBar.visibility = View.VISIBLE

            // Menggunakan fungsi async() krn user ingin nilai kembalian dari fungsi yg dipanggil.
            val deferredNotes = async(Dispatchers.IO) {
                // Menggunakan ContentResolver pada background process.
                val cursor = contentResolver?.query(CONTENT_URI, null, null, null, null)
                /* Content resolver akan meneruskan obyek Uri ke content provider dlm metode query.
                * Obyek Uri dgn nilai CONTENT_URI berarti akan memanggil query select semua data. */

                MappingHelper.mapCursorToArrayList(cursor)  /* Mengubah data menjadi ArrayList agar
                                                               bisa ditampilkan dalam adapter. */
            }
            progressBar.visibility = View.INVISIBLE
            val notes = deferredNotes.await()           /* Mendapatkan nilai kembalian dari fungsi async,
                                                           dgn menggunakan fungsi await(). */
            if(notes.size > 0) {
                adapter.listNotes = notes
            } else {
                adapter.listNotes = ArrayList()
                showSnackbarMessage("Tidak ada data saat ini")
            }
        }
    }

    /*
    Melakukan aksi setelah menerima nilai balik dari semua
    aksi yang dilakukan di NoteAddUpdateActivity.
    */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        /* Metode onActivityResult() akan melakukan penerimaan data dari intent
           yg dikirimkan & diseleksi berdasarkan jenis requestCode & resultCode-nya.
        */

        if (data != null) {
            when (requestCode) {
                // Akan dipanggil jika request codenya ADD,
                // ketika terjadi penambahan data pada NoteAddUpdateActivity.
                NoteAddUpdateActivity.REQUEST_ADD -> if (resultCode == NoteAddUpdateActivity.RESULT_ADD) {
                    val note = data.getParcelableExtra<Note>(NoteAddUpdateActivity.EXTRA_NOTE)
                    /* Ketika metode ini dijalankan maka kita akan membuat objek note baru
                       dan inisiasikan dengan getParcelableExtra */

                    adapter.addItem(note)       // Panggil metode addItem yg berada di adapter
                                                // dengan memasukan objek note sebagai argumen.
                                                // Lalu Metode ini akan menjalankan notifyItemInserted
                                                // dan penambahan arraylist-nya.

                    rv_notes.smoothScrollToPosition(adapter.itemCount - 1)  // Melakukan smoothscrolling
                                                                            // pada objek rvNotes

                    showSnackbarMessage("Satu item berhasil ditambahkan") // Terakhir, muncul notifikasi pesan
                                                                          // dengan Snackbar.
                }
                // Update dan Delete memiliki request code sama akan tetapi result codenya berbeda
                NoteAddUpdateActivity.REQUEST_UPDATE ->
                    when (resultCode) {
                        /* Akan dipanggil jika result codenya UPDATE, dijalankan
                        ketika terjadi perubahan data pada NoteAddUpdateActivity.
                        Semua data di load kembali dari awal.
                        */
                        NoteAddUpdateActivity.RESULT_UPDATE -> {
                            val note = data.getParcelableExtra<Note>(NoteAddUpdateActivity.EXTRA_NOTE) // Data diambil dari
                                                                                                       // NoteAddUpdateActivity,
                                                                                                       // lwt Key "EXTRA_NOTE"
                            val position = data.getIntExtra(NoteAddUpdateActivity.EXTRA_POSITION, 0) // Data diambil dari
                                                                                                     // NoteAddUpdateActivity,
                                                                                                     // lwt Key "EXTRA_POSITION"
                            adapter.updateItem(position, note)
                            rv_notes.smoothScrollToPosition(position)

                            showSnackbarMessage("Satu item berhasil diubah")
                        }
                        /*
                        Akan dipanggil jika result codenya DELETE
                        Delete akan menghapus data dari list berdasarkan dari position
                        */
                        NoteAddUpdateActivity.RESULT_DELETE -> {
                            val position = data.getIntExtra(NoteAddUpdateActivity.EXTRA_POSITION, 0)  // Data diambil dari
                                                                                                      // NoteAddUpdateActivity,
                                                                                                      // lwt Key "EXTRA_POSITION"

                            adapter.removeItem(position)

                            showSnackbarMessage("Satu item berhasil dihapus")
                        }
                    }
            }
        }
    }

    /*
    Metode ini akan menyimpan arraylist, jadi pada saat rotasi,
    layar berubah dan aplikasi tidak memanggil ulang proses mengambil data dari database.
    onCreate akan mengambil data dari sini.
    */
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelableArrayList(EXTRA_STATE, adapter.listNotes)
    }

    /**
     * Tampilkan snackbar
     * @param message inputan message
    */
    private fun showSnackbarMessage(message: String) {
        Snackbar.make(rv_notes, message, Snackbar.LENGTH_SHORT).show()
    }
}