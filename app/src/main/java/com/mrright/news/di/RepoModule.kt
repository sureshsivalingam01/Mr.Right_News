package com.mrright.news.di

import com.mrright.news.db.api.NewsService
import com.mrright.news.db.api.repositories.NewsRepoImpl
import com.mrright.news.db.api.repositories.NewsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object RepoModule {

    @Provides
    @Singleton
    fun provideNewsRepo(
        @ApiKey apiKey: String,
        newsService: NewsService,
    ): NewsRepository {
        return NewsRepoImpl(apiKey, newsService)
    }

}