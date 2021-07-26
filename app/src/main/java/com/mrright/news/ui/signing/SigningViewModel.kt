package com.mrright.news.ui.signing

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.GoogleAuthProvider
import com.mrright.news.db.api.Resource
import com.mrright.news.db.firestore.repositories.AuthRepository
import com.mrright.news.db.firestore.repositories.UserRepository
import com.mrright.news.models.User
import com.mrright.news.utils.constants.SIGN
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SigningViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
) : ViewModel() {

    val signingUIState = MutableStateFlow<SigningUIState>(SigningUIState.None)

    private val _authSigningFlow: MutableStateFlow<SigningState> =
        MutableStateFlow(SigningState.Nothing)

    val authSigningFlow: StateFlow<SigningState> get() = _authSigningFlow

    fun googleSigning(task: Task<GoogleSignInAccount>, sign: SIGN) {

        viewModelScope.launch(Dispatchers.IO) {
            _authSigningFlow.value = SigningState.Loading()

            when (val result = authRepository.getTokenId(task)) {
                is Resource.Error -> _authSigningFlow.value = SigningState.Error(result.msg)
                is Resource.Exception -> {
                    result.ex.message?.let {
                        _authSigningFlow.value =
                            SigningState.Error(result.ex.message!!)
                    }
                }
                is Resource.Success -> {
                    getAccount(GoogleAuthProvider.getCredential(result.value.idToken, null), sign)
                }
            }
        }

    }

    private suspend fun getAccount(credential: AuthCredential, sign: SIGN) {

        when (val result = authRepository.signIn(credential)) {
            is Resource.Error -> _authSigningFlow.value = SigningState.Error(result.msg)
            is Resource.Exception -> {
                result.ex.message?.let {
                    _authSigningFlow.value =
                        SigningState.Error(result.ex.message!!)
                }
            }
            is Resource.Success -> createUser(result.value, sign)
        }

    }

    private suspend fun createUser(authResult: AuthResult, sign: SIGN) {

        val user = User(
            authResult.user?.uid!!,
            authResult.user?.email!!,
            authResult.user?.displayName!!,
        )

        when (val result = userRepository.createUser(user)) {
            is Resource.Error -> _authSigningFlow.value = SigningState.Error(result.msg)
            is Resource.Exception -> {
                result.ex.message?.let {
                    _authSigningFlow.value =
                        SigningState.Error(result.ex.message!!)
                }
            }
            is Resource.Success -> {
                _authSigningFlow.value = if (sign == SIGN.IN) {
                    SigningState.SignedIn(user.name)
                } else {
                    SigningState.SignedUp(user.name)
                }
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
        object Nothing : SigningState()
    }

}