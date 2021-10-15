package com.mrright.news.di

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.mrright.news.R
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ActivityScoped

@Module
@InstallIn(ActivityComponent::class)
object GoogleModule {


	@Provides
	fun provideGSO(
		@ApplicationContext context : Context,
	) : GoogleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
		.apply {
			requestIdToken(context.getString(R.string.default_web_client_id))
			requestEmail()
			requestProfile()
		}
		.build()


    @Provides
    @ActivityScoped
	fun provideGSC(
		@ApplicationContext context : Context,
		googleSignInOptions : GoogleSignInOptions,
	) : GoogleSignInClient = GoogleSignIn.getClient(context, googleSignInOptions)


}