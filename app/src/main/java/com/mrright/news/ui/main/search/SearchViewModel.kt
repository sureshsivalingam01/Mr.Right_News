package com.mrright.news.ui.main.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mrright.news.db.Resource
import com.mrright.news.db.api.repositories.NewsRepository
import com.mrright.news.db.api.responses.NewsDTO
import com.mrright.news.ui.states.QueryState
import com.mrright.news.ui.states.UIState
import com.mrright.news.utils.exceptions.handle
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
	private val newsRepository : NewsRepository,
) : ViewModel() {

	val uiState = MutableStateFlow<UIState>(UIState.Init)

	var queryNewsPage = 1
	var listSize = 0
	private var queryText = ""
	private var breakingNewsResponse : NewsDTO? = null

	private val _news = MutableLiveData<SearchState>(SearchState.None)
	val news : LiveData<SearchState> get() = _news


	fun searchParticular(query : QueryState = QueryState.Paginate) {

		viewModelScope.launch(Dispatchers.Main) {

			_news.value = SearchState.Loading()
			val result = withContext(Dispatchers.IO) {
				if (query is QueryState.Search) {
					queryText = query.txt
					queryNewsPage = 1
					breakingNewsResponse = null
					return@withContext newsRepository.searchQuery(query.txt, queryNewsPage)
				}
				else {
					return@withContext newsRepository.searchQuery(queryText, queryNewsPage)
				}

			}
			result.collect {
				when (it) {
					is Resource.Failure -> {
						_news.value = SearchState.Error(it.ex.handle())

					}
					is Resource.Success -> {

						if (breakingNewsResponse == null) {
							breakingNewsResponse = it.value
						}

						listSize = it.value.totalResults!!

						queryNewsPage++

						if (query is QueryState.Paginate) {
							_news.value = SearchState.Paginated(breakingNewsResponse?.toNews() ?: it.value.toNews())
						}
						else {
							_news.value = SearchState.Searched(breakingNewsResponse?.toNews() ?: it.value.toNews())
						}


					}
				}
			}
		}
	}

	fun changeUIState(uiState : UIState) {
		this.uiState.value = uiState
	}

}