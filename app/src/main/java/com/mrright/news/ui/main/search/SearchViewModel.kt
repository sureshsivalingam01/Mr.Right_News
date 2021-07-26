package com.mrright.news.ui.main.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mrright.news.db.api.Resource
import com.mrright.news.db.api.repositories.NewsRepository
import com.mrright.news.db.api.responses.NewsDTO
import com.mrright.news.models.News
import com.mrright.news.ui.states.NetworkState
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

    private val _news = MutableLiveData<NetworkState<News>>(NetworkState.None)
    val news: LiveData<NetworkState<News>> get() = _news

    fun searchParticular() {

        query?.let {
            viewModelScope.launch(Dispatchers.Main) {

                _news.value = NetworkState.Loading()
                val result = withContext(Dispatchers.IO) {
                    newsRepository.searchQuery(it, queryNewsPage)
                }
                when (result) {
                    is Resource.Error -> {
                        _news.value = NetworkState.Error(result.msg)
                    }
                    is Resource.Exception -> {
                        _news.value = NetworkState.Error(result.ex.message ?: "UnKnown Error")
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
                            NetworkState.Success(
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