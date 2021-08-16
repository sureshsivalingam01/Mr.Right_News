package com.mrright.news.presentation.states_events

import android.view.View
import androidx.annotation.StringRes
import com.mrright.news.utils.helpers.SHORT

sealed class MessageEvent {

	data class Toast(
		val msg : String,
		val duration : Int = SHORT,
	) : MessageEvent()

	data class ToastStringRes(
		@StringRes val stringId : Int,
		val duration : Int = SHORT,
	) : MessageEvent()

	data class SnackBar(
		val msg : String,
		val actionText : String? = null,
		val action : ((View) -> Unit)? = null,
	) : MessageEvent()

}
