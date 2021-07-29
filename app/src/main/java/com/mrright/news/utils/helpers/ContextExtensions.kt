package com.mrright.news.utils.helpers

import android.content.Context
import android.content.Intent
import android.os.Bundle


inline fun <T> Context.openActivity(
    activity: Class<T>,
    extras: Bundle.() -> Unit = {},
) {
    Intent(applicationContext, activity).apply {
        putExtras(Bundle().apply(extras))
        startActivity(this)
    }
}
