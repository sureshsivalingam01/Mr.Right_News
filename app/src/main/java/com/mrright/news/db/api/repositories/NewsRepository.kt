package com.mrright.news.db.api.repositories

import com.mrright.news.db.Resource
import com.mrright.news.db.api.NewsService
import com.mrright.news.db.api.responses.NewsDTO
import com.mrright.news.di.ApiKey
import com.mrright.news.utils.exceptions.NoMoreArticlesException
import com.mrright.news.utils.helpers.errorLog
import com.mrright.news.utils.helpers.infoLog
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject


class NewsRepoImpl @Inject constructor(
	@ApiKey private val apiKey : String,
	private val newsService : NewsService,
) : NewsRepository {


	override suspend fun getTopHeadlines(pageNo : Int) : Flow<Resource<NewsDTO>> = flow {
		try {
			val result = newsService.getTopHeadlines(pageNo, apiKey)
			if (result.isSuccessful && result.body() != null) {
				infoLog("getTopHeadlines | Success | ${result.body()}")
				emit(Resource.Success(result.body()!!))
			}
			else {
				errorLog("getTopHeadlines | Error | ${result.errorBody()}")
				throw NoMoreArticlesException()
			}
		}
		catch (e : Exception) {
			errorLog("getTopHeadlines | Exception | $e")
			emit(Resource.Failure(e))
		}
	}

	override suspend fun searchQuery(
		query : String,
		pageNo : Int,
	) : Flow<Resource<NewsDTO>> = flow {
		try {
			val result = newsService.searchParticular(query, pageNo, apiKey)
			if (result.isSuccessful && result.body() != null) {
				infoLog("searchParticular | Success | ${result.body()}")
				emit(Resource.Success(result.body()!!))
			}
			else {
				errorLog("searchParticular | Error | ${result.errorBody()}")
				throw NoMoreArticlesException()
			}
		}
		catch (e : Exception) {
			errorLog("searchParticular | Exception | $e")
			emit(Resource.Failure(e))
		}
	}

}


interface NewsRepository {

	suspend fun getTopHeadlines(
		pageNo : Int,
	) : Flow<Resource<NewsDTO>>

	suspend fun searchQuery(
		query : String,
		pageNo : Int,
	) : Flow<Resource<NewsDTO>>

}