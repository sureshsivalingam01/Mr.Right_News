package com.mrright.news.ui.signing

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.mrright.news.R
import com.mrright.news.db.Resource
import com.mrright.news.db.Source
import com.mrright.news.db.firestore.repositories.AuthRepository
import com.mrright.news.db.firestore.repositories.UserRepository
import com.mrright.news.models.User
import com.mrright.news.ui.states.MessageEvent
import com.mrright.news.utils.constants.SIGN
import com.mrright.news.utils.exceptions.handle
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SigningViewModel @Inject constructor(
	private val context : Context,
	private val authRepository : AuthRepository,
	private val userRepository : UserRepository,
) : ViewModel() {


	private val _signingUIState = MutableStateFlow<SigningUIState>(SigningUIState.SignIn)
	val signingUIState : StateFlow<SigningUIState> get() = _signingUIState


	private val _authSigning : MutableLiveData<SigningState> = MutableLiveData(SigningState.None)
	val authSigning : LiveData<SigningState> get() = _authSigning


	private val msgChannel = Channel<MessageEvent>()
	val msgFlow = msgChannel.receiveAsFlow()


	fun googleSigning(
		sign : SIGN,
		task : Task<GoogleSignInAccount>,
	) {

		viewModelScope.launch(Dispatchers.Main) {

			_authSigning.value = SigningState.LoadingStringRes(R.string.loading)
			delay(500L)

			val result = withContext(IO) {
				authRepository.getTokenId(task)
			}

			result.collect {
				when (it) {
					is Resource.Failure -> {
						_authSigning.value = SigningState.Error
						msgChannel.send(MessageEvent.Toast(it.ex.handle()))
					}
					is Resource.Success -> {
						checkUserExist(it.value, sign)
					}
				}
			}
		}

	}

	private suspend fun checkUserExist(
		account : GoogleSignInAccount,
		sign : SIGN,
	) {

		userRepository.checkUserExist(account.email!!)
			.collect {
				when (it) {
					is Source.Failure -> {
						if (sign == SIGN.IN) {
							_authSigning.value = SigningState.Error
							msgChannel.send(MessageEvent.Toast(it.ex.handle()))
						}
						else {
							signIn(
								GoogleAuthProvider.getCredential(account.idToken, null),
								sign,
							)
						}
					}
					is Source.Success -> {
						if (sign == SIGN.IN) {
							signIn(
								GoogleAuthProvider.getCredential(account.idToken, null),
								sign,
							)
						}
						else {
							_authSigning.value = SigningState.Error
							msgChannel.send(MessageEvent.ToastStringRes(R.string.account_exist))
						}
					}
				}
			}
	}

	private suspend fun signIn(
		credential : AuthCredential,
		sign : SIGN,
	) {

		_authSigning.value = SigningState.LoadingStringRes(R.string.getting_details)
		delay(500L)

		val result = withContext(IO) {
			authRepository.signIn(credential)
		}

		result.collect {
			when (it) {
				is Resource.Failure -> {
					_authSigning.value = SigningState.Error
					msgChannel.send(MessageEvent.Toast(it.ex.handle()))
				}
				is Resource.Success -> {

					if (sign == SIGN.IN) {
						_authSigning.value = SigningState.SignedIn
						msgChannel.send(MessageEvent.Toast(context.getString(R.string.welcome_signed_in, it.value.user?.displayName!!)))
					}
					else {
						createUser(it.value.user!!)
					}

				}
			}
		}

	}

	private suspend fun createUser(firebaseUser : FirebaseUser) {

		_authSigning.value = SigningState.LoadingStringRes(R.string.creating_user)
		delay(500L)

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

		result.collect {

			when (it) {
				is Source.Failure -> {
					_authSigning.value = SigningState.Error
					msgChannel.send(MessageEvent.Toast(it.ex.handle()))
				}
				is Source.Success -> {
					_authSigning.value = SigningState.SignedUp
					msgChannel.send(MessageEvent.Toast(context.resources.getString(R.string.welcome_signed_up, user.name)))
				}
			}

		}
	}

	fun toggle() {
		viewModelScope.launch {
			if (signingUIState.value == SigningUIState.SignUp) {
				_signingUIState.value = SigningUIState.SignIn
			}
			else {
				_signingUIState.value = SigningUIState.SignUp
			}
		}
	}


}