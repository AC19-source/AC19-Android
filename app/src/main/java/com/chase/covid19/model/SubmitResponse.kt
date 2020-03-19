package com.chase.covid19.model

import com.google.gson.annotations.SerializedName as SN

data class SubmitResponse(
    @SN("msg")
    var message: String?,

    @SN("url")
    var url: String? ,

    @SN("view")
    var view: String? ,

    @SN("step")
    var step: String?,

    @SN("color")
    var color: String?
)