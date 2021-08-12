package com.mrright.news.utils.helpers

import android.app.Activity
import android.content.Intent
import android.os.Bundle


inline fun <T> Activity.openActivity(
	activity : Class<T>,
	extras : Bundle.() -> Unit = {},
) {
	Intent(applicationContext, activity).apply {
		putExtras(Bundle().apply(extras))
		startActivity(this)
	}
}




















