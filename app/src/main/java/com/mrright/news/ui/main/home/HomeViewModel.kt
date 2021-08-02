package com.mrright.news.ui.main.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mrright.news.db.Resource
import com.mrright.news.db.api.repositories.NewsRepository
import com.mrright.news.db.api.responses.NewsDTO
import com.mrright.news.models.News
import com.mrright.news.ui.states.NetworkState
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
class HomeViewModel @Inject constructor(
    private val newsRepository: NewsRepository
) : ViewModel() {

    val uiState = MutableStateFlow<UIState>(UIState.Init)

    var breakingNewsPage = 1
    var listSize = 0
    private var breakingNewsResponse: NewsDTO? = null

    private val _news = MutableLiveData<NetworkState<News>>(NetworkState.None)
    val news: LiveData<NetworkState<News>> get() = _news

    fun getTopHeadlines() {
        viewModelScope.launch(Dispatchers.Main) {

            _news.value = NetworkState.Loading()
            val result = withContext(Dispatchers.IO) {
                newsRepository.getTopHeadlines(breakingNewsPage)
            }
            result.collect {
                when(it){
                    is Resource.Failure -> {
                        _news.value = NetworkState.Error(it.ex.handle())
                    }
                    is Resource.Success -> {
                        breakingNewsPage++
                        if (breakingNewsResponse == null) {
                            breakingNewsResponse = it.value
                        }

                        listSize = it.value.totalResults!!
                        _news.value =
                            NetworkState.Success(
                                breakingNewsResponse?.toNews() ?: it.value.toNews()
                            )
                    }
                }
            }
        }
    }

    fun changeUIState(uiState: UIState) {
        this.uiState.value = uiState
    }


}


