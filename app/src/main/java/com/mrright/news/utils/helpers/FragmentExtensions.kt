package com.mrright.news.utils.helpers

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment


/**
 * Open activity
 *
 * @param T
 * @param activity
 * @param extras
 * @receiver
 */
inline fun <T> Fragment.openActivity(
	activity : Class<T>,
	extras : Bundle.() -> Unit = {},
) {
	Intent(requireContext(), activity).apply {
		putExtras(Bundle().apply(extras))
		startActivity(this)
	}
}