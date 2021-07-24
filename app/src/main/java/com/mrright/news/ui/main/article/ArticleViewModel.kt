package com.mrright.news.ui.main.article

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mrright.news.db.api.repositories.NewsRepository
import com.mrright.news.models.Article
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ArticleViewModel @Inject constructor(
    private val newsRepository: NewsRepository
) : ViewModel() {

    val url = MutableLiveData(Article())

    fun setArticle(url: Article) {
        this.url.value = url
    }

}