package com.mrright.news.ui.main.article


sealed class ArticleFabState {
	object Liked : ArticleFabState()
	object UnLiked : ArticleFabState()
}