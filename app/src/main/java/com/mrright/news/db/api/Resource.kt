package com.mrright.news.db.api

sealed class Resource<out T> {
    data class Success<out R>(val value: R) : Resource<R>()
    data class Error(val msg: String) : Resource<Nothing>()
    data class Exception(val ex: Throwable) : Resource<Nothing>()
}