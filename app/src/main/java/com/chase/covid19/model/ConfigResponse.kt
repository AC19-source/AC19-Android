package com.chase.covid19.model

import com.chase.covid19.ext.toQuestionsList
import com.google.gson.annotations.SerializedName as SN

data class ConfigResponse(
    @SN("questions")
    val questions: LinkedHashMap<String?, Question?>?,

    @SN("info")
    val info: CovidInfo?,

    @SN("welcome")
    val welcome: String?,

    @SN("userRegisterTitle")
    val userRegisterTitle: String?,

    @SN("versionCode")
    val versionCode: String?
) {
    private var internalQuestionsList: ArrayList<Question>? = null
    val questionsList: ArrayList<Question>
        get() {
            if (internalQuestionsList.isNullOrEmpty()) {
                internalQuestionsList = questions.toQuestionsList()
            }

            return internalQuestionsList ?: arrayListOf()
        }
}