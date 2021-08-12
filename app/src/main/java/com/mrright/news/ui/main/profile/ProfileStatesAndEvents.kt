package com.mrright.news.ui.main.profile

import com.mrright.news.models.User

sealed class UserState {
	data class Success(val user : User) : UserState()
	object Error : UserState()
	object None : UserState()
}