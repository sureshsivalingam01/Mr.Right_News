package com.mrright.news.ui.main.edit_profile

sealed class ButtonState {
	object Enabled : ButtonState()
	object Disabled : ButtonState()
}