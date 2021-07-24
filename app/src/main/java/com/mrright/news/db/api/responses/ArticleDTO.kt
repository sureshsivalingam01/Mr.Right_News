package com.mrright.news.db.api.responses


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep
import com.mrright.news.models.Article

@Keep
data class ArticleDTO(
    @SerializedName("author")
    val author: String? = null,
    @SerializedName("content")
    val content: String? = null,
    @SerializedName("description")
    val description: String? = null,
    @SerializedName("publishedAt")
    val publishedAt: String? = null,
    @SerializedName("source")
    val source: SourceDTO? = null,
    @SerializedName("title")
    val title: String? = null,
    @SerializedName("url")
    val url: String? = null,
    @SerializedName("urlToImage")
    val urlToImage: String? = null
) {

    fun toArticle(): Article {
        return Article(
            author,
            content,
            description,
            publishedAt,
            source?.toSource(),
            title,
            url,
            urlToImage
        )
    }

}



fun List<ArticleDTO>.toArticles(): MutableList<Article> {
    val articles = mutableListOf<Article>()
    this.forEach {
        articles.add(it.toArticle())
    }
    return articles
}