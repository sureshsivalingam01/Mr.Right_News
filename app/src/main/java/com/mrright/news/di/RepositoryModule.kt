package com.mrright.news.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.storage.FirebaseStorage
import com.mrright.news.db.api.NewsService
import com.mrright.news.db.api.repositories.NewsRepoImpl
import com.mrright.news.db.api.repositories.NewsRepository
import com.mrright.news.db.firestore.repositories.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped


@Module
@InstallIn(ViewModelComponent::class)
object RepositoryModule {

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
        auth: FirebaseAuth,
    ): AuthRepository = AuthRepoImpl(auth)

    @Provides
    @ViewModelScoped
    fun provideUserRepo(
        auth: FirebaseAuth,
        storage:FirebaseStorage,
        @UserCollection userCollection: CollectionReference,
    ): UserRepository = UserRepoImpl(auth,storage,userCollection)

    @Provides
    @ViewModelScoped
    fun provideArticleRepo(
        auth: FirebaseAuth,
        @UserCollection userCollection: CollectionReference,
    ): ArticleRepository = ArticleRepoImpl(auth, userCollection)

}