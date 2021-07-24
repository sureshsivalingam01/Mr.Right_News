package com.mrright.news.ui.states

sealed class NetworkState<out T> {
    data class Success<out R>(val value: R) : NetworkState<R>()
    data class Error(val msg: String = "Unknown Error") : NetworkState<kotlin.Nothing>()
    data class Loading(val msg: String = "Loading") : NetworkState<kotlin.Nothing>()
    object Nothing : NetworkState<kotlin.Nothing>()
}