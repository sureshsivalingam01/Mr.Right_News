package com.mrright.news.presentation.main.article


sealed class ArticleFabState {
	object Liked : ArticleFabState()
	object UnLiked : ArticleFabState()
	object None : ArticleFabState()
}