package com.mrright.news.presentation.main.home

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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.receiveAsFlow
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
    private var breakingNewsResponse : NewsDTO? = null

    private val _news = MutableLiveData<HeadlinesState>(HeadlinesState.None)
    val news : LiveData<HeadlinesState> get() = _news

    private val _msgChannel : Channel<MessageEvent> = Channel()
    val msgEvent = _msgChannel.receiveAsFlow()

    fun getTopHeadlines() {
        viewModelScope.launch(Dispatchers.Main) {

            val result = withContext(Dispatchers.IO) {
                newsRepository.getTopHeadlines(breakingNewsPage)
            }
            result.collect {
                when (it) {
                    is Resource.Failure -> {
                        _msgChannel.send(MessageEvent.Toast(it.ex.handle()))
                        _news.value = HeadlinesState.Failed
                    }
                    is Resource.Success -> {

                        val news = it.value

                        breakingNewsPage++
                        if (breakingNewsResponse == null) {
                            breakingNewsResponse = news
                        }
                        else {
                            val oldArticles = breakingNewsResponse?.articles
                            val newArticles = news.articles!!
                            oldArticles?.addAll(newArticles)
                        }

                        listSize = news.totalResults!!
                        _news.value = HeadlinesState.Success(breakingNewsResponse?.toNews() ?: news.toNews())
                    }
                }
            }
        }
    }

    fun changeUIState(uiState: UIState) {
        this.uiState.value = uiState
    }


}


