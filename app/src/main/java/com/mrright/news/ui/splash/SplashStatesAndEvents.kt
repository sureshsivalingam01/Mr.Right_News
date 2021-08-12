package com.mrright.news.ui.splash

sealed class UserState {
	object None : UserState()
	object SignUp : UserState()
	object SignedIn : UserState()
	data class Error(val msg : String = "Unknown Error") : UserState()
}