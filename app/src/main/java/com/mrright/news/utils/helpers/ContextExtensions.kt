package com.mrright.news.utils.helpers

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast
import androidx.annotation.StringRes
import com.mrright.news.R


const val SHORT = 0
const val LONG = 1

@SuppressLint("WrongConstant")
fun Context.toast(
	text : CharSequence = this::class.java.simpleName,
	duration : Int = SHORT,
) {
	Toast.makeText(this, text, duration)
		.show()
}


@SuppressLint("WrongConstant")
fun Context.toast(
	@StringRes id : Int = R.string.app_name,
	duration : Int = SHORT,
) {
	Toast.makeText(this, id, duration)
		.show()
}