package com.mrright.news.db

sealed class Resource<out T> {
    data class Success<out R>(val value: R) : Resource<R>()
    data class Failure(val ex: Throwable) : Resource<Nothing>()
}

sealed class ResourceNone {
    object Success : ResourceNone()
    data class Failure(val ex: Throwable) : ResourceNone()
}