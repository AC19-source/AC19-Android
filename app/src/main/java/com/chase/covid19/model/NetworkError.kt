package com.chase.covid19.model

import com.google.gson.annotations.SerializedName as SN

data class NetworkError (
    @SN("code")
    val code: Int?,

    @SN("msg")
    val message: String?
)