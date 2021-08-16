package com.mrright.news.presentation.main.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mrright.news.db.Resource
import com.mrright.news.db.api.repositories.NewsRepository
import com.mrright.news.db.api.responses.NewsDTO
import com.mrright.news.presentation.states_events.MessageEvent
import com.mrright.news.presentation.states_events.UIState
import com.mrright.news.utils.exceptions.handle
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
	private val newsRepository : NewsRepository,
) : ViewModel() {

	private val _uiState = MutableStateFlow<UIState>(UIState.Init)
	val uiState : Flow<UIState> get() = _uiState

	var queryNewsPage = 1
	var listSize = 0
	private var queryText = ""
	private var searchNewsResponse : NewsDTO? = null

	private val _news = MutableLiveData<SearchState>(SearchState.None)
	val news : LiveData<SearchState> get() = _news

	private val _msgChannel : Channel<MessageEvent> = Channel()
	val msgEvent = _msgChannel.receiveAsFlow()


	fun searchParticular(query : QueryEvent = QueryEvent.Paginate) {

		viewModelScope.launch(Dispatchers.Main) {

			if (query is QueryEvent.Search) {
				_news.value = SearchState.Loading("Searching")
			}

			val result = withContext(Dispatchers.IO) {
				if (query is QueryEvent.Search) {
					queryText = query.txt
					queryNewsPage = 1
					searchNewsResponse = null
					return@withContext newsRepository.searchQuery(query.txt, queryNewsPage)
				}
				else {
					return@withContext newsRepository.searchQuery(queryText, queryNewsPage)
				}

			}
			result.collect {
				when (it) {
					is Resource.Failure -> {
						_msgChannel.send(MessageEvent.Toast(it.ex.handle()))
						_news.value = SearchState.Error
					}
					is Resource.Success -> {

						val news = it.value

						queryNewsPage++
						if (searchNewsResponse == null) {
							searchNewsResponse = news
						}
						else {
							val oldArticles = searchNewsResponse?.articles
							val newArticles = news.articles!!
							oldArticles?.addAll(newArticles)
						}

						listSize = news.totalResults!!


						if (query is QueryEvent.Paginate) {
							_news.value = SearchState.Paginated(searchNewsResponse?.toNews() ?: news.toNews())
						}
						else {
							_news.value = SearchState.Searched(searchNewsResponse?.toNews()!!)
						}


					}
				}
			}
		}
	}

	fun changeUIState(uiState : UIState) {
		this._uiState.value = uiState
	}

}