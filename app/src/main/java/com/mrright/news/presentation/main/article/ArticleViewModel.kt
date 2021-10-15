package com.mrright.news.presentation.main.article

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mrright.news.R
import com.mrright.news.db.Resource
import com.mrright.news.db.Source
import com.mrright.news.db.firestore.repositories.ArticleRepository
import com.mrright.news.models.Article
import com.mrright.news.presentation.states_events.MessageEvent
import com.mrright.news.presentation.states_events.UIState
import com.mrright.news.utils.exceptions.handle
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ArticleViewModel @Inject constructor(
	private val articleRepository : ArticleRepository,
) : ViewModel() {

	val articleLiveData = MutableLiveData(Article())

	val uiState : MutableStateFlow<UIState> = MutableStateFlow(UIState.Init)

	private var articleId = ""

	private val _likedArticle : MutableStateFlow<ArticleFabState> = MutableStateFlow(ArticleFabState.None)
	val likedArticle : StateFlow<ArticleFabState> get() = _likedArticle

	private val msgChannel = Channel<MessageEvent>()
	val msgFlow = msgChannel.receiveAsFlow()

	private fun getArticleIfLiked() {
		viewModelScope.launch(Dispatchers.IO) {

			articleRepository.getArticleByUrl(articleLiveData.value?.url!!)
				.collect {

					when (it) {
						is Resource.Failure -> {
							_likedArticle.value = ArticleFabState.UnLiked
						}
						is Resource.Success -> {
							articleId = it.value.id
							_likedArticle.value = ArticleFabState.Liked
						}
					}

				}
		}
	}

	fun setArticle(value : Article) {
		this.articleLiveData.value = value
		getArticleIfLiked()
	}


	fun addToLiked() {
        viewModelScope.launch(Dispatchers.IO) {
            articleLiveData.value?.let { article ->

                articleRepository.addArticle(article)
                    .collect {
                        when (it) {
                            is Resource.Failure -> _likedArticle.value = ArticleFabState.UnLiked
                            is Resource.Success -> {
                                msgChannel.send(MessageEvent.ToastStringRes(R.string.liked))
                                articleId = it.value
                                _likedArticle.value = ArticleFabState.Liked
                            }
                        }
                    }
            }
		}
	}

	fun removeFromLiked() {
		viewModelScope.launch(Dispatchers.IO) {

			articleRepository.deleteArticle(articleId)
				.collect {
					when (it) {
						is Source.Success -> {
                            msgChannel.send(MessageEvent.ToastStringRes(R.string.un_liked))
                            _likedArticle.value = ArticleFabState.UnLiked
                        }
						is Source.Failure -> {
							msgChannel.send(MessageEvent.Toast(it.ex.handle()))
							_likedArticle.value = ArticleFabState.Liked
						}
					}

				}
		}
	}
}