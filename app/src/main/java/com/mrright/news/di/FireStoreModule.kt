package com.mrright.news.di

import com.google.firebase.firestore.FirebaseFirestore
import com.mrright.news.utils.constants.Collection
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FireStoreModule {


    @Provides
    @Singleton
    @UserCollection
    fun provideUserCollection(
        fireStore: FirebaseFirestore,
    ) = fireStore.collection(Collection.USERS)


}