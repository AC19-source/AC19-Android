package com.chase.covid19.ext

import android.app.Activity
import android.os.Bundle
import android.os.Parcelable
import java.io.Serializable

fun Bundle.put(key: String, value: Any?): Bundle {
    when (value) {
        is String -> putString(key, value)
        is Int -> putInt(key, value)
        is Byte -> putByte(key, value)
        is ByteArray -> putByteArray(key, value)
        is Char -> putChar(key, value)
        is CharArray -> putCharArray(key, value)
        is CharSequence -> putCharSequence(key, value)
        is Float -> putFloat(key, value)

        is Parcelable -> putParcelable(key, value)
        is Serializable -> putSerializable(key, value)
    }
    //TODO: fill this shit
    return this
}

fun <T : Parcelable> Activity.getParcelableExtra(key: String): T? {
    return intent?.getParcelableExtra(key)
}