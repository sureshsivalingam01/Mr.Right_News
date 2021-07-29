package com.mrright.news.db.firestore.dto

import com.mrright.news.models.Article
import com.mrright.news.models.Source


data class ArticleFDTO(
    val id: String = "",
    val author: String? = "",
    val content: String? = "",
    val description: String? = "",
    val publishedAt: String? = "",
    val title: String? = "",
    val url: String? = "",
    val urlToImage: String? = "",
) {
    fun toArticle(): Article {
        return Article(author, content, description, publishedAt, Source(), title, url, urlToImage)
    }
}
