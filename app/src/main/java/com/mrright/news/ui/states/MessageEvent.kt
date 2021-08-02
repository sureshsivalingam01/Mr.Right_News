package com.mrright.news.ui.states

import android.view.View

sealed class MessageEvent {
    data class Toast(val msg: String) : MessageEvent()
    data class SnackBar(
        val msg: String,
        val actionText: String? = null,
        val action: ((View) -> Unit)? = null,
    ) : MessageEvent()
}
