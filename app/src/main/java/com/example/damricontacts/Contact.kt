package com.example.damricontacts

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Contact (
    @SerializedName("id")
    var id: String,

    @SerializedName("nama")
    var nama: String,

    @SerializedName("nomor")
    var nomor: String
    )