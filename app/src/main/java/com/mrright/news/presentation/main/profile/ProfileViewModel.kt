package com.mrright.news.presentation.main.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.mrright.news.R
import com.mrright.news.db.firestore.repositories.UserRepository
import com.mrright.news.presentation.states_events.MessageEvent
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
	private val auth : FirebaseAuth,
	private val userRepository : UserRepository,
) : ViewModel() {

	val profileMenus = Menu.values()
		.toList()

	private val _msgChannel : Channel<MessageEvent> = Channel()
	val msgEvent = _msgChannel.receiveAsFlow()

	@ExperimentalCoroutinesApi
	suspend fun userDetailsCallBack() = withContext(Dispatchers.IO) {
		userRepository.userDetailsCallBack()
	}.asLiveData()


	fun signOut() {
		viewModelScope.launch {
			auth.signOut()
			_msgChannel.send(MessageEvent.ToastStringRes(R.string.signout))
		}
	}


}