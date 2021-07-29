package com.mrright.news.db

sealed class Resource<out T> {
    data class Success<out R>(val value: R) : Resource<R>()
    data class Failure(val ex: Throwable) : Resource<Nothing>()
}

sealed class FSource {
    object Success : FSource()
    data class Failure(val ex: Throwable) : FSource()
}

enum class Source {
    SUCCESS,
    FAILURE,
}