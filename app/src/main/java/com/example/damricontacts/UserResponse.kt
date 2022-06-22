package com.example.damricontacts

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class UserResponse {
        @SerializedName("status")
        @Expose
        var status: String? = null

        @SerializedName("kode")
        @Expose
        var kode: Int? = null
}