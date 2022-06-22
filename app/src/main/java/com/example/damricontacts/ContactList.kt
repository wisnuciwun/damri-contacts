package com.example.damricontacts

import com.google.gson.annotations.SerializedName
import java.util.*

data class ContactList (
    @SerializedName("data")
    val data: List<Contact>,
    @SerializedName("status")
    val status: String,
    @SerializedName("kode")
    val kode: Int,
)