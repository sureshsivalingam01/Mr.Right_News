package com.mrright.news.ui.states

sealed class UIState{
    object Init : UIState()
    object Non : UIState()
}
