package com.mrright.news.utils.exceptions

class NoMoreArticlesException : Exception("No More Articles")

class NoLikedArticleException : Exception("No Liked Article")

class SignInException : Exception("Error While Signing In")

class NoUserException : Exception("No User from your email")