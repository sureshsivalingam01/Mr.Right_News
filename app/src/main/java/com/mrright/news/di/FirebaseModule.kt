package com.mrright.news.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {


	@Provides
	@Singleton
	fun provideAuth() = FirebaseAuth.getInstance()


	@Provides
	@Singleton
	fun provideFireStore() = FirebaseFirestore.getInstance()


	@Provides
	@Singleton
	fun provideStorage() = FirebaseStorage.getInstance()

}