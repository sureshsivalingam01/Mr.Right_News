package com.mrright.news.presentation.main.search

import com.mrright.news.models.News

sealed class SearchState {
	data class Searched(val value : News) : SearchState()
	data class Paginated(val value : News) : SearchState()
	object Error : SearchState()
	data class Loading(val msg : String = "Loading") : SearchState()
	object None : SearchState()
}

sealed class QueryEvent {
	object Paginate : QueryEvent()
	data class Search(val txt : String = "Tamil Nadu") : QueryEvent()
}