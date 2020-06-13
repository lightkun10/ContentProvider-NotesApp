package com.example.android.consumerapp.entity

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Note(
    var id: Int = 0,
    var title: String? = null,
    var description: String? = null,
    var date: String? = null
) : Parcelable

/*
    Kelas model di atas merepresentasikan data yang tersimpan
    dan memberi kemudahan menulis kode. Lebih simpel dibandingkan
    dengan ketika Anda harus mengolah data dalam bentuk objek Cursor.
    Selain itu dengan menjadikan objek ini sebagai objek Parcelable
    (dalam bentuk paket) memudahkan pengiriman data dari satu Activity ke Activity lain.
*/