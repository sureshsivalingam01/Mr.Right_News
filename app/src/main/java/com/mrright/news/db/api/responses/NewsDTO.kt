package com.mrright.news.db.api.responses


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep
import com.mrright.news.models.News

@Keep
data class NewsDTO(
    @SerializedName("articles")
    val articles: MutableList<ArticleDTO>? = null,
    @SerializedName("status")
    val status: String? = null,
    @SerializedName("totalResults")
    val totalResults: Int? = null
) {

    fun toNews(): News {
        return News(articles?.toArticles(), status, totalResults)
    }

}
