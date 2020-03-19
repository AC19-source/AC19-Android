package com.chase.covid19.model

import com.google.gson.annotations.SerializedName as SN

data class LaunchModel(
    @SN("pushToken")
    val pushToken: String,
    @SN("deviceType")
    val deviceType: String,
    @SN("appVersion")
    val appVersion: String,
    @SN("deviceModel")
    val deviceModel: String
)
