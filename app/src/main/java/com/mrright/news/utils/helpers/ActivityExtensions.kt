package com.mrright.news.utils.helpers

import android.app.Activity
import android.widget.Toast


fun Activity.shortToast(
    text: CharSequence = this::class.java.simpleName
) {
    Toast.makeText(applicationContext, text, Toast.LENGTH_SHORT)
        .show()
}

fun Activity.longToast(
    text: CharSequence = this::class.java.simpleName
) {
    Toast.makeText(applicationContext, text, Toast.LENGTH_LONG)
        .show()
}
