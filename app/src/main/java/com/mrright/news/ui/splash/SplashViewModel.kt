package com.mrright.news.ui.splash

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.mrright.news.ui.states.MessageEvent
import com.mrright.news.ui.states.UIState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val auth: FirebaseAuth,
) : ViewModel(), FirebaseAuth.AuthStateListener {

    val isUserLoggedIn: MutableLiveData<UserState> = MutableLiveData(UserState.None)
    private val currentUser: MutableLiveData<FirebaseUser> = MutableLiveData(null)

    private val msgChannel = Channel<MessageEvent>()
    val msgFlow = msgChannel.receiveAsFlow()

    val uiState = MutableStateFlow<UIState>(UIState.Init)

    init {
        currentUser.value = auth.currentUser
        auth.addAuthStateListener(this)
    }

    suspend fun checkUser() {
        uiState.value = UIState.None
        delay(2000L)
        currentUser.value?.email?.let {
            isUserLoggedIn.value = UserState.SignedIn
            return
        }
        isUserLoggedIn.value = UserState.SignUp
    }

    override fun onAuthStateChanged(firebaseAuth: FirebaseAuth) {
        currentUser.value = firebaseAuth.currentUser
    }

    override fun onCleared() {
        super.onCleared()
        auth.removeAuthStateListener(this)
    }

    sealed class UserState {
        object None : UserState()
        object SignUp : UserState()
        object SignedIn : UserState()
        data class Loading(val msg: String = "Loading") : UserState()
        data class Error(val msg: String = "Unknown Error") : UserState()
    }

}