package com.mrright.news.ui.states

sealed class MessageEvent {
    data class Toast(val msg: String?) : MessageEvent()
    data class SnackBar(val msg: String?) : MessageEvent()
}
