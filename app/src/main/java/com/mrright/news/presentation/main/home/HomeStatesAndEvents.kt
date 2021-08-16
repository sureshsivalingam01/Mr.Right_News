package com.mrright.news.presentation.main.home

import com.mrright.news.models.News


sealed class HeadlinesState {
	object None : HeadlinesState()
	object Loading : HeadlinesState()
	object Failed : HeadlinesState()
	data class Success(val news : News) : HeadlinesState()
}