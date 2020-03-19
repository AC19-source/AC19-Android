package com.chase.covid19.model

import com.google.gson.annotations.SerializedName

data class CityModel(
    @SerializedName("id")
    var id: Int = 0,

    @SerializedName("name")
    var name: String = "",

    @SerializedName("State")
    var State : String = "",

    @SerializedName("City")
    var City : String = "",

    @SerializedName("latitude")
    var latitude : String = "",

    @SerializedName("longitude")
    var longitude : String = ""
)