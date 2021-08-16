package com.mrright.news.presentation.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.mrright.news.presentation.states_events.UIState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
	private val auth : FirebaseAuth,
) : ViewModel(), FirebaseAuth.AuthStateListener {

	private val _isUserLoggedIn : MutableLiveData<UserState> = MutableLiveData(UserState.None)
	val isUserLoggedIn : LiveData<UserState> get() = _isUserLoggedIn

	private val currentUser : MutableLiveData<FirebaseUser> = MutableLiveData(null)

	val uiState = MutableStateFlow<UIState>(UIState.Init)


	init {
		currentUser.value = auth.currentUser
		auth.addAuthStateListener(this)
	}

	suspend fun checkUser() {
		uiState.value = UIState.None
		delay(2000L)
		currentUser.value?.email?.let {
			_isUserLoggedIn.value = UserState.SignedIn
			return
		}
		_isUserLoggedIn.value = UserState.SignUp
	}

	override fun onAuthStateChanged(firebaseAuth : FirebaseAuth) {
		currentUser.value = firebaseAuth.currentUser
	}

	override fun onCleared() {
		super.onCleared()
		auth.removeAuthStateListener(this)
	}

}