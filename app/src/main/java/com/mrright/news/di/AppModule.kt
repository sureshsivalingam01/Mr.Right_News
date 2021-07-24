package com.mrright.news.di

import com.mrright.news.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {


    @BaseUrl
    @Provides
    @Singleton
    fun provideBaseUrl(): String = BuildConfig.BASE_URL


    @ApiKey
    @Provides
    @Singleton
    fun provideApiKey(): String = BuildConfig.API_KEY


}