package com.mrright.news.ui.main.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.mrright.news.db.Resource
import com.mrright.news.db.firestore.repositories.UserRepository
import com.mrright.news.models.User
import com.mrright.news.utils.constants.Menu
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val firebaseAuth: FirebaseAuth,
) : ViewModel() {

    private val _userDetails: MutableLiveData<UserState> = MutableLiveData(UserState.None)
    val userDetails: LiveData<UserState> get() = _userDetails

    val profileMenus = Menu.values().toList()

    init {
        getUserDetails()
    }

    private fun getUserDetails() {
        viewModelScope.launch(Dispatchers.Main) {

            val result = withContext(Dispatchers.IO) {
                userRepository.getUser(firebaseAuth.currentUser?.uid!!)
            }

            when (result) {
                is Resource.Failure -> _userDetails.value = UserState.Error
                is Resource.Success -> _userDetails.value = UserState.Success(result.value.toUser())
            }

        }
    }

    fun signOut() {
        firebaseAuth.signOut()
    }


}

sealed class UserState {
    data class Success(val user: User) : UserState()
    object Error : UserState()
    object None : UserState()
}