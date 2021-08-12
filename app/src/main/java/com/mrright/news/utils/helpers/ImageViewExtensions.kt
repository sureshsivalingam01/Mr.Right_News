package com.mrright.news.utils.helpers

import android.widget.ImageView
import androidx.annotation.DrawableRes
import com.bumptech.glide.Glide


fun ImageView.glideUrl(url : String) {
	Glide.with(this.context)
		.load(url)
		.into(this)
}


fun ImageView.glideUrl(
	url : String,
	@DrawableRes placeholder : Int,
) {
	Glide.with(this.context)
		.load(url)
		.placeholder(placeholder)
		.into(this)
}


fun ImageView.glideDrawableRes(@DrawableRes id : Int) {
	Glide.with(this.context)
		.load(id)
		.into(this)
}
