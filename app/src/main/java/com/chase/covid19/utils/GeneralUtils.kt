package com.chase.covid19.utils

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager

private fun View?.showKeyboard(show:Boolean){
    if (this == null)
        return

    try {
        val imm =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (!imm.isActive)
            return

        if (show)
            imm.showSoftInput(this, 0)
        else
            imm.hideSoftInputFromWindow(windowToken, 0)
    } catch (e: Exception) {
        // Please handle exception or remove this catch block.
    }
}

fun View?.showKeyboard() =
    showKeyboard(true)

fun View?.hideKeyboard() =
    showKeyboard(false)