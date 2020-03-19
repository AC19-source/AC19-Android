package com.chase.covid19.ext

import com.chase.covid19.model.Question

fun LinkedHashMap<String?, String?>?.toQuestionList() =
    this?.mapNotNull {
        if (it.key?.startsWith("q") == true)
            it.value
        else
            null

    }

fun LinkedHashMap<String?, Question?>?.toQuestionsList() =
    ArrayList(this?.mapNotNull {
        if (it.key != null)
            it.value?.id = it.key

        it.value
    } ?: arrayListOf())