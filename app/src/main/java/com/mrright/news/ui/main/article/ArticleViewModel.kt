package com.mrright.news.ui.main.article

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mrright.news.db.Resource
import com.mrright.news.db.Source
import com.mrright.news.db.firestore.repositories.ArticleRepository
import com.mrright.news.models.Article
import com.mrright.news.ui.states.MessageEvent
import com.mrright.news.ui.states.UIState
import com.mrright.news.utils.exceptions.handle
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ArticleViewModel @Inject constructor(
    private val articleRepository: ArticleRepository,
) : ViewModel() {

    val articleLiveData = MutableLiveData(Article())

    val uiState: MutableStateFlow<UIState> = MutableStateFlow(UIState.Init)

    private var articleId = ""

    val likedArticle: MutableStateFlow<ArticleFabState> = MutableStateFlow(ArticleFabState.UnLiked)

    private val msgChannel = Channel<MessageEvent>()
    val msgFlow = msgChannel.receiveAsFlow()

    private fun getArticleIfLiked() {
        viewModelScope.launch(Dispatchers.IO) {
            when (val result = articleRepository.getArticleByUrl(articleLiveData.value?.url!!)) {
                is Resource.Failure -> {
                    //msgChannel.send(MessageEvent.Toast(result.ex.message))
                    likedArticle.value = ArticleFabState.UnLiked
                }
                is Resource.Success -> {
                    articleId = result.value.id
                    likedArticle.value = ArticleFabState.Liked
                }
            }
        }
    }

    fun setArticle(value: Article) {
        this.articleLiveData.value = value
        getArticleIfLiked()
    }


    fun addToLiked() {
        viewModelScope.launch(Dispatchers.Main) {
            articleLiveData.value?.let {

                val result = withContext(Dispatchers.IO) {
                    articleRepository.addArticle(it)
                }

                when (result) {
                    is Resource.Failure -> likedArticle.value = ArticleFabState.UnLiked
                    is Resource.Success -> {
                        msgChannel.send(MessageEvent.Toast("Liked"))
                        articleId = result.value
                        likedArticle.value = ArticleFabState.Liked
                    }
                }
            }
        }
    }

    fun removeFromLiked() {
        viewModelScope.launch(Dispatchers.IO) {
            when (val result = articleRepository.deleteArticle(articleId)) {
                is Source.Success -> {
                    msgChannel.send(MessageEvent.Toast("UnLiked"))
                    likedArticle.value = ArticleFabState.UnLiked
                }
                is Source.Failure -> {
                    msgChannel.send(MessageEvent.Toast(result.ex.handle()))
                    likedArticle.value = ArticleFabState.Liked
                }
            }
        }
    }
}

sealed class ArticleFabState {
    object Liked : ArticleFabState()
    object UnLiked : ArticleFabState()
}