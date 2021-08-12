package com.mrright.news.ui.main.search

import com.mrright.news.models.News

sealed class SearchState {
	data class Searched(val value : News) : SearchState()
	data class Paginated(val value : News) : SearchState()
	data class Error(val msg : String = "Unknown Error") : SearchState()
	data class Loading(val msg : String = "Loading") : SearchState()
	object None : SearchState()
}