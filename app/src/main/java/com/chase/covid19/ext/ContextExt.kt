package com.chase.covid19.ext

import android.content.Context
import java.io.IOException

fun Context.readAssetFileText(fileName: String): String {
    return try {
        this.assets.open(fileName).use { it.bufferedReader().readText() }
    } catch (e: IOException) {
        // Should never happen!
        throw RuntimeException(e)
    }
}