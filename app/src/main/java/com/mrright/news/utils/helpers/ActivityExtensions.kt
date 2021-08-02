package com.mrright.news.utils.helpers

import android.app.Activity
import android.content.Intent
import android.os.Bundle
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

inline fun <T> Activity.openActivity(
    activity: Class<T>,
    extras: Bundle.() -> Unit = {},
) {
    Intent(applicationContext, activity).apply {
        putExtras(Bundle().apply(extras))
        startActivity(this)
    }
}




















