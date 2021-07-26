package com.mrright.news.di


import com.mrright.news.BuildConfig
import com.mrright.news.db.api.NewsService
import com.mrright.news.utils.constants.CONNECT_TIMEOUT
import com.mrright.news.utils.constants.READ_TIMEOUT
import com.mrright.news.utils.constants.WRITE_TIMEOUT
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RetrofitModule {


    @Provides
    @Singleton
    fun provideGsonConverterFactory(): GsonConverterFactory = GsonConverterFactory.create()


    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = if (BuildConfig.DEBUG) {
                    Level.BODY
                } else {
                    Level.NONE
                }
            }).apply {
                readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
                connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
                writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
            }
            .build()
    }


    @Provides
    @Singleton
    fun provideRetrofit(
        @BaseUrl baseUrl: String,
        gsonConverterFactory: GsonConverterFactory,
        okHttpClient: OkHttpClient,
    ): Retrofit {
        return Retrofit.Builder().addConverterFactory(gsonConverterFactory).baseUrl(baseUrl)
            .client(okHttpClient).build()
    }


    @Provides
    @Singleton
    fun provideNewsService(
        retrofit: Retrofit,
    ): NewsService = retrofit.create(NewsService::class.java)

}
















