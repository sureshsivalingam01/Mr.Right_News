package com.mrright.news.ui.signing

import androidx.annotation.StringRes

sealed class SigningUIState {
    object SignIn : SigningUIState()
    object SignUp : SigningUIState()
}

sealed class SigningState {
    object SignedIn : SigningState()
    object SignedUp : SigningState()
    object Error : SigningState()
    data class LoadingText(val msg: String = "Loading") : SigningState()
    data class LoadingStringRes(@StringRes val stringId: Int) : SigningState()
    object None : SigningState()
}