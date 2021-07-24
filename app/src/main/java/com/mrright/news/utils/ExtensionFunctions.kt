@file:Suppress("FunctionName")

package com.mrright.news.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide


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

fun Fragment.shortToast(
    text: CharSequence = this::class.java.simpleName
) {
    Toast.makeText(requireContext(), text, Toast.LENGTH_SHORT)
        .show()
}

fun Fragment.longToast(
    text: CharSequence = this::class.java.simpleName
) {
    Toast.makeText(requireContext(), text, Toast.LENGTH_LONG)
        .show()
}


fun Any.debugLog(
    msg: String = this::class.java.simpleName,
    tr: Throwable? = null,
) = Log.d(this::class.java.simpleName, msg, tr)

fun Any.errorLog(
    msg: String = this::class.java.simpleName,
    tr: Throwable? = null,
) = Log.e(this::class.java.simpleName, msg, tr)

fun Any.infoLog(
    msg: String = this::class.java.simpleName,
    tr: Throwable? = null,
) = Log.i(this::class.java.simpleName, msg, tr)

fun Any.verboseLog(
    msg: String = this::class.java.simpleName,
    tr: Throwable? = null,
) = Log.v(this::class.java.simpleName, msg, tr)

fun Any.warnLog(
    msg: String = this::class.java.simpleName,
    tr: Throwable? = null,
) = Log.w(this::class.java.simpleName, msg, tr)

fun Any.wtfLog(
    msg: String = this::class.java.simpleName,
    tr: Throwable? = null,
) = Log.wtf(this::class.java.simpleName, msg, tr)


fun View.visible() {
    this.visibility = View.VISIBLE
}

fun View.inVisible() {
    this.visibility = View.INVISIBLE
}

fun View.gone() {
    this.visibility = View.GONE
}

fun ImageView.glide(url: String?, @DrawableRes placeholder: Int? = null) {
    if (placeholder == null) {
        Glide.with(this.context).load(url)
            .into(this)
    } else {
        Glide.with(this.context).load(url).placeholder(placeholder)
            .into(this)
    }
}


inline fun <T> Context.openActivity(
    activity: Class<T>? = null,
    extras: Bundle.() -> Unit = {},
) {
    Intent(applicationContext, activity).apply {
        putExtras(Bundle().apply(extras))
        startActivity(this)
    }
}










