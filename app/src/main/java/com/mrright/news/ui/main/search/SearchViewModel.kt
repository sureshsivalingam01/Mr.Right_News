package com.mrright.news.ui.main.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mrright.news.db.Resource
import com.mrright.news.db.api.repositories.NewsRepository
import com.mrright.news.db.api.responses.NewsDTO
import com.mrright.news.models.News
import com.mrright.news.ui.states.NetworkEvent
import com.mrright.news.ui.states.UIState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val newsRepository: NewsRepository
) : ViewModel() {

    val uiState = MutableStateFlow<UIState>(UIState.Init)

    var queryNewsPage = 1
    var listSize = 0
    private var query: String? = "Corona"

    private var queryNewsResponse: NewsDTO? = null

    private val _news = MutableLiveData<NetworkEvent<News>>(NetworkEvent.None)
    val news: LiveData<NetworkEvent<News>> get() = _news

    fun searchParticular() {

        query?.let {
            viewModelScope.launch(Dispatchers.Main) {

                _news.value = NetworkEvent.Loading()

                val result = withContext(Dispatchers.IO) {
                    newsRepository.searchQuery(it, queryNewsPage)
                }
                when (result) {
                    is Resource.Failure -> {
                        _news.value = NetworkEvent.Error(result.ex.message ?: "UnKnown Error")
                    }
                    is Resource.Success -> {
                        queryNewsPage++
                        if (queryNewsResponse == null) {
                            queryNewsResponse = result.value
                        } else {
                            val oldArticles = queryNewsResponse?.articles
                            val newArticles = result.value.articles
                            newArticles?.let {
                                oldArticles?.addAll(it)
                            }
                        }

                        listSize = result.value.totalResults!!
                        _news.value =
                            NetworkEvent.Success(
                                queryNewsResponse?.toNews() ?: result.value.toNews()
                            )
                    }
                }
            }
        }
    }

    fun changeUIState(uiState: UIState) {
        this.uiState.value = uiState
    }

    fun setQ(query: String?) {
        this.query = query
    }

}