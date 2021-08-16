package com.mrright.news.presentation.main.edit_profile

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.mrright.news.db.Resource
import com.mrright.news.db.Source
import com.mrright.news.db.firestore.repositories.UserRepository
import com.mrright.news.models.User
import com.mrright.news.presentation.main.profile.UserState
import com.mrright.news.presentation.states_events.MessageEvent
import com.mrright.news.utils.constants.State
import com.mrright.news.utils.exceptions.handle
import com.mrright.news.utils.helpers.LONG
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel @Inject constructor(
	private val auth : FirebaseAuth,
	private val userRepository : UserRepository,
) : ViewModel() {

	private val _userDetails : MutableLiveData<UserState> = MutableLiveData(UserState.None)
	val userDetails : LiveData<UserState> get() = _userDetails

	private var user = User()
	private var name = ""
	private var mobileNo = ""

	private val _userUpdate : MutableStateFlow<ImageUpdateState> = MutableStateFlow(ImageUpdateState.None)
	val userUpdate : StateFlow<ImageUpdateState> = _userUpdate

	private val _msgChannel : Channel<MessageEvent> = Channel()
	val msgEvent = _msgChannel.receiveAsFlow()

	private val _sameContent : MutableStateFlow<ButtonState> = MutableStateFlow(ButtonState.Disabled)
	val sameContent : StateFlow<ButtonState> get() = _sameContent

	init {
		getUserDetails()
	}

	suspend fun updateDetails() {

		_userUpdate.value = ImageUpdateState.Loading("Updating")

		userRepository.updateUser(name, mobileNo)
			.collect {
				when (it) {
					is Source.Failure -> {
						_userUpdate.value = ImageUpdateState.Failed
						_msgChannel.send(MessageEvent.Toast(it.ex.handle(), LONG))
					}
					is Source.Success -> {
						_msgChannel.send(MessageEvent.SnackBar("Updated"))
						_userUpdate.value = ImageUpdateState.Success
					}
				}
			}
	}

	private fun getUserDetails() {
		viewModelScope.launch(Dispatchers.Main) {

			val result = withContext(IO) {
				userRepository.getUser(auth.currentUser?.uid!!)
			}

			result.collect {

				when (it) {
					is Resource.Failure -> _userDetails.value = UserState.Error
					is Resource.Success -> {
						user = it.value.toUser()
						_userDetails.value = UserState.Success(it.value.toUser())
					}
				}

			}
		}
	}

	fun setNewName(newName : String) = viewModelScope.launch {
		name = newName
		checkValues()
	}

	fun setMobileNo(newMobileNo : String) = viewModelScope.launch {
		mobileNo = newMobileNo
		checkValues()
	}

	private fun checkValues() {
		if (name == user.name && mobileNo == user.phoneNo) {
			_sameContent.value = ButtonState.Disabled
			return
		}
		else if (name.isEmpty() || mobileNo.length < 10) {
			_sameContent.value = ButtonState.Disabled
			return
		}
		else {
			_sameContent.value = ButtonState.Enabled
			return
		}
	}

	fun updateProfilePic(uri : Uri) {
		viewModelScope.launch(IO) {
			_userUpdate.value = ImageUpdateState.Loading()
			userRepository.updateProfilePic(uri)
				.collect {
					when (it) {
						State.SUCCESS -> {
							_userUpdate.value = ImageUpdateState.Success
							_msgChannel.send(MessageEvent.SnackBar("Updated"))
						}
						State.FAILED -> {
							_userUpdate.value = ImageUpdateState.Failed
							_msgChannel.send(MessageEvent.SnackBar("Failed to Upload"))
						}
						else -> Unit
					}
				}
		}
	}

}

