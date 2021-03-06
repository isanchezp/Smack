package com.ivan.smack.utils

import android.app.Activity
import android.content.Context
import android.view.inputmethod.InputMethodManager

fun Activity.hideKeyboard() {
    val inputManager = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    if (inputManager.isAcceptingText) {
        inputManager.hideSoftInputFromWindow(this.currentFocus!!.windowToken, 0)
    }
}