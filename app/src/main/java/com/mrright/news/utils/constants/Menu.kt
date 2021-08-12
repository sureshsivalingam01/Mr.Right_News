package com.mrright.news.utils.constants

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.mrright.news.R

enum class Menu(
	@StringRes val menu : Int,
	@DrawableRes val icon : Int,
) {
	LIKED_ARTICLE(R.string.liked_articles, R.drawable.heart_24_like),
	EDIT_PROFILE(R.string.edit_profile, R.drawable.menu_user_24),
	SIGNOUT(R.string.signout, R.drawable.signout_24),
}