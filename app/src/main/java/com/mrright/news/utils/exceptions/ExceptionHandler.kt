package com.mrright.news.utils.exceptions


fun Throwable.handle() : String {
	return when (this) {
		is NoMoreArticlesException -> this.message ?: ""
		is NoLikedArticleException -> this.message ?: ""
		is SignInException -> this.message ?: ""
		is NoUserException -> this.message ?: ""
		else -> this.message!!
	}
}