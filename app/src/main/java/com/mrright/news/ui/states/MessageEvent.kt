package com.mrright.news.ui.states

import android.view.View
import androidx.annotation.StringRes

sealed class MessageEvent {
    data class Toast(val msg: String) : MessageEvent()
    data class ToastStringRes(@StringRes val stringId: Int) : MessageEvent()

    data class SnackBar(
        val msg: String,
        val actionText: String? = null,
        val action: ((View) -> Unit)? = null,
    ) : MessageEvent()
}
