package com.mrright.news.ui.main.edit_profile

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.mrright.news.databinding.EditProfileFragmentBinding
import com.mrright.news.ui.main.profile.UserState
import com.mrright.news.ui.states.MessageEvent
import com.mrright.news.utils.constants.State
import com.mrright.news.utils.helpers.glideUrl
import com.mrright.news.utils.helpers.shortSnack
import com.mrright.news.utils.helpers.toast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EditProfileFragment : Fragment() {

	private var _bind : EditProfileFragmentBinding? = null
	private val bind get() = _bind!!

	private val viewModel : EditProfileViewModel by viewModels()

	private lateinit var getContent : ActivityResultLauncher<String>

	override fun onCreateView(
		inflater : LayoutInflater,
		container : ViewGroup?,
		savedInstanceState : Bundle?,
	) : View {
		_bind = EditProfileFragmentBinding.inflate(inflater, container, false)
		collectUserDetails()
		collectButtonState()
		return bind.root
	}

	private fun collectButtonState() {
		lifecycleScope.launchWhenStarted {
			viewModel.sameContent.collect {
				when (it) {
					ButtonState.Disabled -> bind.btnUpdate.isEnabled = false
					ButtonState.Enabled -> bind.btnUpdate.isEnabled = true
				}
			}
		}
	}

	override fun onViewCreated(
		view : View,
		savedInstanceState : Bundle?,
	) {
		super.onViewCreated(view, savedInstanceState)

		btnClicks()
		collectUserUpdate()
		collectMsgs()
		registerForRes()

	}

	private fun registerForRes() {
		getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
			uri?.let {
				uploadData(it)
			}
		}
	}

	private fun uploadData(uri : Uri) {
		viewModel.updateProfilePic(uri)
	}

	private fun collectMsgs() {
		lifecycleScope.launchWhenStarted {
			viewModel.msgEvent.collect {
				when (it) {
					is MessageEvent.Toast -> requireContext().toast(it.msg)
					is MessageEvent.SnackBar -> {
						bind.root.shortSnack(it.msg)
					}
					else -> Unit
				}
			}
		}
	}

	private fun collectUserUpdate() {
		lifecycleScope.launchWhenStarted {
			viewModel.userUpdate.collect {
				when (it) {
					State.SUCCESS -> findNavController().popBackStack()
					else -> Unit
				}
			}
		}
	}

	private fun btnClicks() {

		bind.imgProfilePic.setOnClickListener {
			getContent.launch("image/*")
		}

		bind.etName.addTextChangedListener {
			viewModel.setNewName(it.toString())
		}

		bind.etPhoneNo.addTextChangedListener {
			viewModel.setMobileNo(it.toString())
		}

		bind.btnUpdate.setOnClickListener {


			lifecycleScope.launch(Dispatchers.Main) {
				viewModel.updateDetails()
			}
		}
	}

	private fun collectUserDetails() {
		viewModel.userDetails.observe(viewLifecycleOwner) {
			when (it) {
				is UserState.Error -> Unit
				is UserState.Success -> {
					bind.etName.setText(it.user.name)
					bind.etPhoneNo.setText(it.user.phoneNo)
					bind.imgProfilePic.glideUrl(it.user.profilePicUrl)
				}
				else -> Unit
			}
		}
	}


	override fun onDestroyView() {
		super.onDestroyView()
		_bind = null
	}

}

