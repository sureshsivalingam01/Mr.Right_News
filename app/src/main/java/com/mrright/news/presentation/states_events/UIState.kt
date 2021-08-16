package com.mrright.news.presentation.states_events

sealed class UIState {
	object Init : UIState()
	object None : UIState()
}
