package com.mrright.news.utils.helpers

import android.widget.TextView
import androidx.annotation.StringRes

fun TextView.setStringRes(@StringRes id: Int) {
    this.text = this.context.getString(id)
}