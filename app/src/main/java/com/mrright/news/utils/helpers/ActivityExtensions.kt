package com.mrright.news.utils.helpers

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.StringRes
import com.mrright.news.R


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

fun Activity.shortToast(
    @StringRes id: Int = R.string.app_name,
    vararg values: Any?,
) {

    if (values.isEmpty()) {
        Toast.makeText(applicationContext, id, Toast.LENGTH_SHORT).show()
    } else {
        Toast.makeText(
            applicationContext,
            getString(R.string.welcome_signed_in, values),
            Toast.LENGTH_SHORT,
        ).show()
    }
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




















