package com.mrright.news.utils.helpers

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