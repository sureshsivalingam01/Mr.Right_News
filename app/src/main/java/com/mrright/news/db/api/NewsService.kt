package com.mrright.news.db.api

import com.mrright.news.db.api.responses.NewsDTO
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsService {

	@GET("top-headlines")
	suspend fun getTopHeadlines(
		@Query("page") pageNo : Int,
		@Query("apiKey") apiKey : String,
		@Query("country") countryCode : String = "in",
	) : Response<NewsDTO>

	@GET("everything")
	suspend fun searchParticular(
		@Query("q") query : String = "Corona",
		@Query("page") pageNo : Int,
		@Query("apiKey") apiKey : String,
	) : Response<NewsDTO>

}