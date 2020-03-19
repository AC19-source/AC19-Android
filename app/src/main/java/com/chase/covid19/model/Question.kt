package com.chase.covid19.model

import com.google.gson.annotations.SerializedName as SN

data class Question(
    @SN("id")
    var id: String?,
    @SN("txt")
    var txt: String,
    @SN("img")
    var img: String
)