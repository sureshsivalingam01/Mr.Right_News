package com.mrright.news.ui.main.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.mrright.news.db.api.Resource
import com.mrright.news.db.firestore.repositories.UserRepository
import com.mrright.news.models.User
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

    private val _userDetails: MutableLiveData<User> = MutableLiveData(User())
    val userDetails: LiveData<User> get() = _userDetails

    init {
        getUserDetails()
    }

    private fun getUserDetails() {
        viewModelScope.launch(Dispatchers.Main) {

            val result = withContext(Dispatchers.IO) {
                userRepository.getUser(firebaseAuth.currentUser?.email!!)
            }

            when (result) {
                is Resource.Error -> TODO()
                is Resource.Exception -> TODO()
                is Resource.Success -> _userDetails.value = result.value
            }

        }
    }


}