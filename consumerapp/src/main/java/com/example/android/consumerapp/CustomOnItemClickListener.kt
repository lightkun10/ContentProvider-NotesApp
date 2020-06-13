package com.example.android.consumerapp

import android.view.View
import javax.security.auth.callback.Callback

class CustomOnItemClickListener(private val position: Int,
                                private val onItemClickCallback: OnItemClickCallback) : View.OnClickListener {

    override fun onClick(view: View) {
        onItemClickCallback.onItemClicked(view, position)
    }

    interface OnItemClickCallback {
        fun onItemClicked(view: View, position: Int)
    }
}

/* TODO NOTE:
Kelas di atas bertugas membuat item seperti CardView bisa diklik di dalam adapter.
Caranya lakukan penyesuaian pada kelas event OnClickListener. Alhasil kita bisa
mengimplementasikan interface listener yang baru bernama OnItemClickCallback.
Kelas tersebut dibuat untuk menghindari nilai final dari posisi yang
tentunya sangat tidak direkomendasikan.
*/
