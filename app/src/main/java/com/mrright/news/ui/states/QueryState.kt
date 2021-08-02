package com.mrright.news.ui.states

sealed class QueryState {
    object Paginate : QueryState()
    data class Search(val txt: String) : QueryState()
}
