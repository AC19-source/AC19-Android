package com.chase.covid19.model

import com.google.gson.annotations.SerializedName as SN

data class CovidTableRow(
    @SN("ttl")
    val ttl: String?,

    @SN("tot")
    val total: Int,

    @SN("dth")
    val deaths: Int ,

    @SN("rec")
    val rec: Int
)

data class CovidInfo(
    @SN("update")
    val update: String?,

    @SN("tbl")
    val rows: ArrayList<CovidTableRow>?
)