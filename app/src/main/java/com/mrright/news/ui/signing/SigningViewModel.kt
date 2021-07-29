package com.mrright.news.ui.signing

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.mrright.news.db.Resource
import com.mrright.news.db.Source
import com.mrright.news.db.firestore.repositories.AuthRepository
import com.mrright.news.db.firestore.repositories.UserRepository
import com.mrright.news.models.User
import com.mrright.news.utils.constants.SIGN
import com.mrright.news.utils.helpers.infoLog
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SigningViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
) : ViewModel() {

    val signingUIState = MutableStateFlow<SigningUIState>(SigningUIState.None)

    private val _authSigning: MutableLiveData<SigningState> =
        MutableLiveData(SigningState.None)

    val authSigning: LiveData<SigningState> get() = _authSigning

    fun googleSigning(task: Task<GoogleSignInAccount>, sign: SIGN) {

        viewModelScope.launch(Dispatchers.Main) {
            _authSigning.value = SigningState.Loading("Getting Token Details")
            delay(2000L)

            val result = withContext(IO) {
                authRepository.getTokenId(task)
            }

            when (result) {
                is Resource.Failure -> {
                    result.ex.message?.let {
                        _authSigning.value = SigningState.Error(it)
                    }
                }
                is Resource.Success -> {
                    infoLog("SSS ${result.value.idToken}")
                    getAccount(GoogleAuthProvider.getCredential(result.value.idToken, null), sign)
                }
            }
        }

    }

    private suspend fun getAccount(credential: AuthCredential, sign: SIGN) {

        _authSigning.value = SigningState.Loading("Getting Account Details")
        delay(2000L)

        val result = withContext(IO) {
            authRepository.signIn(credential)
        }

        when (result) {
            is Resource.Failure -> {
                result.ex.message?.let {
                    _authSigning.value = SigningState.Error(it)
                }
            }
            is Resource.Success -> {

                if (sign == SIGN.IN) {
                    _authSigning.value = SigningState.SignedIn(result.value.user?.displayName!!)
                } else {
                    createUser(result.value.user!!)
                }

            }
        }

    }

    private suspend fun createUser(firebaseUser: FirebaseUser) {

        _authSigning.value = SigningState.Loading("Creating User")
        delay(2000L)

        val user = User(
            firebaseUser.uid,
            firebaseUser.email ?: "",
            firebaseUser.displayName ?: "",
            firebaseUser.providerData[0].phoneNumber ?: "",
            firebaseUser.providerData[0].photoUrl.toString(),
        )

        val result = withContext(IO) {
            userRepository.createUser(user.toUserDTO())
        }

        when (result) {
            is Source.Failure -> {
                result.ex.message?.let {
                    _authSigning.value = SigningState.Error(it)
                }
            }
            is Source.Success -> {
                _authSigning.value = SigningState.SignedUp(user.name)
            }
        }

    }

    fun changeSigningState(signingUIState: SigningUIState) {
        this.signingUIState.value = signingUIState
    }

    sealed class SigningUIState {
        object SignIn : SigningUIState()
        object SignUp : SigningUIState()
        object None : SigningUIState()
    }

    sealed class SigningState {
        data class SignedIn(val name: String) : SigningState()
        data class SignedUp(val name: String) : SigningState()
        data class Error(val msg: String = "Unknown Error") : SigningState()
        data class Loading(val msg: String = "Loading") : SigningState()
        object None : SigningState()
    }

}