package com.mrright.news.utils.helpers

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.Fragment


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


inline fun <T> Fragment.openActivity(
    activity: Class<T>,
    extras: Bundle.() -> Unit = {},
) {
    Intent(requireContext(), activity).apply {
        putExtras(Bundle().apply(extras))
        startActivity(this)
    }
}