package com.mrright.news.ui.main.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.mrright.news.db.firestore.repositories.UserRepository
import com.mrright.news.di.UserCollection
import com.mrright.news.models.User
import com.mrright.news.ui.states.MessageEvent
import com.mrright.news.utils.constants.Menu
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val auth: FirebaseAuth,
    @UserCollection private val userCollection: CollectionReference,
) : ViewModel() {

    val profileMenus = Menu.values().toList()

    private val _msgChannel: Channel<MessageEvent> = Channel()
    val msgEvent = _msgChannel.receiveAsFlow()

    @ExperimentalCoroutinesApi
    suspend fun userDetailsCallBack() = withContext(Dispatchers.IO) {
        userRepository.userDetailsCallBack()
    }.asLiveData()


    fun signOut() {
        viewModelScope.launch {
            auth.signOut()
            _msgChannel.send(MessageEvent.Toast("Signed Out"))
        }
    }


}

sealed class UserState {
    data class Success(val user: User) : UserState()
    object Error : UserState()
    object None : UserState()
}