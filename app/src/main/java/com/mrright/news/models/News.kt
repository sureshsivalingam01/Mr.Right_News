package com.mrright.news.models


import com.mrright.news.db.api.responses.NewsDTO

data class News(
	val articles : List<Article>? = mutableListOf(),
	val status : String? = "",
	val totalResults : Int? = 0,
) {

	fun toNewsDTO() : NewsDTO {
		return NewsDTO(articles?.toArticlesDTO(), status, totalResults)
	}

}
