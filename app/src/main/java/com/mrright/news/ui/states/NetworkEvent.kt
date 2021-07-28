package com.mrright.news.ui.states

sealed class NetworkEvent<out T> {
    data class Success<out R>(val value: R) : NetworkEvent<R>()
    data class Error(val msg: String = "Unknown Error") : NetworkEvent<Nothing>()
    data class Loading(val msg: String = "Loading") : NetworkEvent<Nothing>()
    object None : NetworkEvent<Nothing>()
}