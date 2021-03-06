package com.mrright.news.utils.helpers

import android.view.View
import androidx.annotation.StringRes
import androidx.appcompat.widget.Toolbar
import com.google.android.material.snackbar.Snackbar


fun View.visible() {
	this.visibility = View.VISIBLE
}

fun View.inVisible() {
	this.visibility = View.INVISIBLE
}

fun View.gone() {
	this.visibility = View.GONE
}

/**
 * Short snackbar
 *
 * @param text
 * @param actionText
 * @param action
 */
fun View.shortSnack(
	text : String,
	actionText : String? = null,
	action : ((View) -> Unit)? = null,
) {
	Snackbar.make(this, text, Snackbar.LENGTH_SHORT)
		.setAction(actionText, action)
		.show()
}


/**
 * Set string resource title
 *
 * @param id
 */
fun Toolbar.setStringResTitle(@StringRes id : Int) {
	this.title = this.context.getString(id)
}