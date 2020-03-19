package com.chase.covid19.ext

import com.google.gson.Gson

// Gson is an expensive object so should be created once.
val gson by lazy { Gson() }

inline fun <reified T : Any> String.fromJson(): T? {
    return gson.fromJson(this, T::class.java)
}