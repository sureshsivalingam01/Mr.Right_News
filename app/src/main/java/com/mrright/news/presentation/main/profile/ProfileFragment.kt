package com.mrright.news.presentation.main.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.mrright.news.R
import com.mrright.news.databinding.FragmentProfileBinding
import com.mrright.news.presentation.adapters.MenuAdapter
import com.mrright.news.presentation.signing.SigningActivity
import com.mrright.news.presentation.states_events.MessageEvent
import com.mrright.news.utils.constants.Menu
import com.mrright.news.utils.helpers.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class ProfileFragment : Fragment() {

	private var _bind : FragmentProfileBinding? = null
	private val bind get() = _bind!!

	private val viewModel : ProfileViewModel by viewModels()

	private lateinit var menuAdapter : MenuAdapter


	override fun onCreateView(
		inflater : LayoutInflater,
		container : ViewGroup?,
		savedInstanceState : Bundle?,
	) : View {
		_bind = FragmentProfileBinding.inflate(inflater, container, false)

		initRV()
		collectUserDetails()
		collectMsgEvents()

		return bind.root
	}

	private fun collectMsgEvents() {

		lifecycleScope.launchWhenStarted {
			viewModel.msgEvent.collect {
				when (it) {
					is MessageEvent.Toast -> requireContext().toast(it.msg)
					is MessageEvent.ToastStringRes -> requireContext().toast(it.stringId)
					else -> Unit
				}
			}
		}
	}

	private fun initRV() {
		menuAdapter = MenuAdapter(viewModel.profileMenus) {
			when (it) {
				Menu.LIKED_ARTICLE -> {
					findNavController().navigate(R.id.action_navigation_profile_to_likedArticleFragment)
				}
				Menu.EDIT_PROFILE -> {
					findNavController().navigate(R.id.action_navigation_profile_to_editProfileFragment)
				}
				Menu.SIGNOUT -> {
					viewModel.signOut()
					openActivity(SigningActivity::class.java)
					requireActivity().finish()
				}
			}
		}
		bind.rvMenus.apply {
			adapter = menuAdapter
			layoutManager = LinearLayoutManager(context)
		}
	}


	private fun collectUserDetails() {
		lifecycleScope.launchWhenStarted {
			viewModel.userDetailsCallBack()
				.observe(viewLifecycleOwner) {
					with(bind) {
						when (it) {
							is UserState.None -> Unit
							is UserState.Error -> root.inVisible()
							is UserState.Success -> {
								imgProfile.glideUrl(it.user.profilePicUrl)
								txtName.text = it.user.name
								txtEmail.text = it.user.email
								bind.root.visible()
							}
						}
					}
				}
		}
	}

	override fun onDestroyView() {
		super.onDestroyView()
		_bind = null
	}
}
