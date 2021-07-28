package com.mrright.news.utils.constants

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.mrright.news.R

enum class Menu(@StringRes val menu: Int, @DrawableRes val icon: Int) {
    LIKED_ARTICLE(R.string.liked_articles, R.drawable.ic_baseline_local_fire_department_24),
    EDIT_PROFILE(R.string.edit_profile, R.drawable.ic_baseline_update_24),
    SIGNOUT(R.string.signout, R.drawable.ic_baseline_login_24),
}