package com.example.android.consumerapp.adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.android.consumerapp.CustomOnItemClickListener
import com.example.android.consumerapp.NoteAddUpdateActivity
import com.example.android.consumerapp.R
import com.example.android.consumerapp.entity.Note
import kotlinx.android.synthetic.main.item_note.view.*

/*
Kelas adapter ini berfungsi untuk menampilkan data per baris di komponen viewgroup
seperti RecyclerView dengan data yang berasal dari objek linkedlist bernama listNotes.
User melakukan proses inflate layout yang dibuat sebelumnya untuk menjadi tampilan
per baris di RecyclerView. Termasuk juga di dalamnya implementasi dari CustomOnItemClickListener
yang membuat objek CardViewNote bisa diklik untuk mengarahkan ke halaman NoteAddUpdateActivity.
Tujuannya, untuk melakukan perubahan data oleh pengguna.
*/

class NoteAdapter(private val activity: Activity) : RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {
    /*
       Generate getter untuk arraylist-nya dan juga constructor untuk context-nya.
       Context di sini dibutuhkan karena user akan memanggil fungsi
       startActivityForResultketika item diklik.
    */
    var listNotes = ArrayList<Note>()
        set(listNotes) {
            if (listNotes.size > 0) {
                this.listNotes.clear()
            }
            this.listNotes.addAll(listNotes)

            notifyDataSetChanged()
        }

    /* Menambah Item di RecyclerView. */
    fun addItem(note: Note) {
        this.listNotes.add(note)
        notifyItemInserted(this.listNotes.size - 1)
    }

    /* Memperbaharui Item di RecyclerView. */
    fun updateItem(position: Int, note: Note) {
        this.listNotes[position] = note
        notifyItemChanged(position, note)
    }

    /* Menghapus Item di RecyclerView. */
    fun removeItem(position: Int) {
        this.listNotes.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, this.listNotes.size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_note, parent, false)
        return NoteViewHolder(view)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        holder.bind(listNotes[position])
    }

    override fun getItemCount(): Int = this.listNotes.size

    inner class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(note: Note) {
            with(itemView) {
                tv_item_title.text = note.title
                tv_item_date.text = note.date
                tv_item_description.text = note.description
                cv_item_note.setOnClickListener(CustomOnItemClickListener(adapterPosition, object : CustomOnItemClickListener.OnItemClickCallback {
                    override fun onItemClicked(view: View, position: Int) {
                        val intent = Intent(activity, NoteAddUpdateActivity::class.java)
                        intent.putExtra(NoteAddUpdateActivity.EXTRA_POSITION, position)  // Used in
                                                                                    // NoteAddUpdateActivity
                        intent.putExtra(NoteAddUpdateActivity.EXTRA_NOTE, note)     // Also, Used in
                                                                                    // NoteAddUpdateActivity
                        activity.startActivityForResult(intent, NoteAddUpdateActivity.REQUEST_UPDATE)
                    }
                }))
            }
        }
    }
}