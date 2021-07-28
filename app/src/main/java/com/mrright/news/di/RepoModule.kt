package com.mrright.news.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.mrright.news.db.api.NewsService
import com.mrright.news.db.api.repositories.NewsRepoImpl
import com.mrright.news.db.api.repositories.NewsRepository
import com.mrright.news.db.firestore.repositories.AuthRepoImpl
import com.mrright.news.db.firestore.repositories.AuthRepository
import com.mrright.news.db.firestore.repositories.UserRepoImpl
import com.mrright.news.db.firestore.repositories.UserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(ViewModelComponent::class)
object RepoModule {

    @Provides
    @ViewModelScoped
    fun provideNewsRepo(
        @ApiKey apiKey: String,
        newsService: NewsService,
    ): NewsRepository {
        return NewsRepoImpl(apiKey, newsService)
    }

    @Provides
    @ViewModelScoped
    fun provideAuthRepo(
        auth: FirebaseAuth
    ): AuthRepository = AuthRepoImpl(auth)

    @Provides
    @ViewModelScoped
    fun provideUserRepo(
        @UserCollection userCollection: CollectionReference
    ): UserRepository = UserRepoImpl(userCollection)


}