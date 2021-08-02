package com.mrright.news.ui.main.edit_profile

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
import com.mrright.news.ui.main.profile.UserState
import com.mrright.news.ui.states.MessageEvent
import com.mrright.news.utils.constants.State
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
class EditProfileViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val userRepository: UserRepository,
) : ViewModel() {

    private val _userDetails: MutableLiveData<UserState> = MutableLiveData(UserState.None)
    val userDetails: LiveData<UserState> get() = _userDetails

    private var user = User()
    private var name = ""
    private var mobileNo = ""

    private val _userUpdate: MutableStateFlow<State> = MutableStateFlow(State.NONE)
    val userUpdate: StateFlow<State> = _userUpdate

    private val _msgChannel: Channel<MessageEvent> = Channel()
    val msgEvent = _msgChannel.receiveAsFlow()

    private val _sameContent: MutableStateFlow<ButtonState> =
        MutableStateFlow(ButtonState.Disabled)

    val sameContent: StateFlow<ButtonState> get() = _sameContent

    init {
        getUserDetails()
    }

    suspend fun updateDetails() {

        userRepository.updateUser(name, mobileNo).collect {
            when (it) {
                is Source.Failure -> {
                    _userUpdate.value = State.FAILED
                    _msgChannel.send(MessageEvent.Toast(it.ex.handle()))
                }
                is Source.Success -> {
                    _msgChannel.send(MessageEvent.SnackBar("Updated"))
                    delay(1500L)
                    _userUpdate.value = State.SUCCESS
                }
            }
        }

    }

    private fun getUserDetails() {
        viewModelScope.launch(Dispatchers.Main) {

            val result = withContext(IO) {
                userRepository.getUser(auth.currentUser?.uid!!)
            }

            when (result) {
                is Resource.Failure -> _userDetails.value = UserState.Error
                is Resource.Success -> {
                    user = result.value.toUser()
                    _userDetails.value = UserState.Success(result.value.toUser())
                }
            }

        }
    }

    fun setNewName(newName: String) = viewModelScope.launch {
        name = newName
        checkValues()
    }

    fun setMobileNo(newMobileNo: String) = viewModelScope.launch {
        mobileNo = newMobileNo
        checkValues()
    }

    private fun checkValues() {
        if (name == user.name && mobileNo == user.phoneNo) {
            _sameContent.value = ButtonState.Disabled
            return
        } else if (name.isEmpty() || mobileNo.length < 10) {
            _sameContent.value = ButtonState.Disabled
            return
        } else {
            _sameContent.value = ButtonState.Enabled
            return
        }
    }

    fun updateProfilePic(uri: Uri) {
        viewModelScope.launch(IO) {
            userRepository.updateProfilePic(uri).collect {
                when (it) {
                    State.SUCCESS -> {
                        _msgChannel.send(MessageEvent.SnackBar("Updated"))
                        delay(1500L)
                        _userUpdate.value = State.SUCCESS
                    }
                    State.FAILED -> Unit
                    else -> Unit
                }
            }
        }
    }

}

sealed class ButtonState {
    object Enabled : ButtonState()
    object Disabled : ButtonState()
}

