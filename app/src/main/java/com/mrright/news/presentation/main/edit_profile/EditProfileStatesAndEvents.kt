package com.mrright.news.presentation.main.edit_profile

sealed class ButtonState {
	object Enabled : ButtonState()
	object Disabled : ButtonState()
}

sealed class ImageUpdateState {
	object None : ImageUpdateState()
	data class Loading(val msg : String = "Uploading") : ImageUpdateState()
	object Success : ImageUpdateState()
	object Failed : ImageUpdateState()
}