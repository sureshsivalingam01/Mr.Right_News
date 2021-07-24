package com.mrright.news.ui.main.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mrright.news.db.api.Resource
import com.mrright.news.db.api.repositories.NewsRepository
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

    private val breakingNewsPage = 1

    private val _news = MutableLiveData<NetworkState<News>>(NetworkState.Nothing)
    val news: LiveData<NetworkState<News>> get() = _news


    fun searchParticular(query: String = "Corona") {
        viewModelScope.launch(Dispatchers.Main) {

            _news.value = NetworkState.Loading()
            val result = withContext(Dispatchers.IO) {
                newsRepository.searchParticular(query, breakingNewsPage)
            }
            when (result) {
                is Resource.Error -> {
                    result.message?.let {
                        _news.value = NetworkState.Error(it)
                    }
                }
                is Resource.Exception -> {
                    result.ex?.message?.let {
                        _news.value = NetworkState.Error(it)
                    }
                }
                is Resource.Success -> {
                    _news.value = NetworkState.Success(result.value.toNews())
                }
            }
        }
    }

    fun changeUIState(uiState: UIState) {
        this.uiState.value = uiState
    }

}