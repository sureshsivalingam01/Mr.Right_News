package com.mrright.news.models

import com.mrright.news.db.api.responses.ArticleDTO
import java.io.Serializable

data class Article(
    val author: String? = "",
    val content: String? = "",
    val description: String? = "",
    val publishedAt: String? = "",
    val source: Source? = Source(),
    val title: String? = "",
    val url: String? = "",
    val urlToImage: String? = "",
) : Serializable {

    fun toArticleDTO(): ArticleDTO {
        return ArticleDTO(
            author,
            content,
            description,
            publishedAt,
            source?.toSourceDTO(),
            title,
            url,
            urlToImage
        )
    }

}


fun List<Article>.toArticlesDTO(): MutableList<ArticleDTO> {
    val articles = mutableListOf<ArticleDTO>()
    this.forEach {
        articles.add(it.toArticleDTO())
    }
    return articles
}