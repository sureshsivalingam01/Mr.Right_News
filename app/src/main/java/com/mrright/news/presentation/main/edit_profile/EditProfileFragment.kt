package com.mrright.news.presentation.main.edit_profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.mrright.news.R
import com.mrright.news.databinding.EditProfileFragmentBinding
import com.mrright.news.presentation.main.profile.UserState
import com.mrright.news.presentation.states_events.MessageEvent
import com.mrright.news.utils.ProgressDialog
import com.mrright.news.utils.helpers.glideUrl
import com.mrright.news.utils.helpers.shortSnack
import com.mrright.news.utils.helpers.toast
import com.mrright.news.utils.helpers.visible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


@AndroidEntryPoint
class EditProfileFragment : Fragment() {

	private var _bind : EditProfileFragmentBinding? = null
	private val bind get() = _bind!!

	private lateinit var getContent : ActivityResultLauncher<String>

	private val progressDialog = ProgressDialog()
	private var dialog : AlertDialog? = null

	private val viewModel : EditProfileViewModel by viewModels()

	override fun onCreateView(
		inflater : LayoutInflater,
		container : ViewGroup?,
		savedInstanceState : Bundle?,
	) : View {
		_bind = EditProfileFragmentBinding.inflate(inflater, container, false)

		dialog = progressDialog.instance(requireActivity())
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
				viewModel.updateProfilePic(it)
			}
		}
	}

	private fun collectMsgs() {
		lifecycleScope.launchWhenStarted {
			viewModel.msgEvent.collect {
				when (it) {
					is MessageEvent.Toast -> requireContext().toast(it.msg, it.duration)
					is MessageEvent.SnackBar -> bind.root.shortSnack(it.msg)
					else -> Unit
				}
			}
		}
	}

	private fun collectUserUpdate() {
		lifecycleScope.launchWhenStarted {
			viewModel.userUpdate.collect {
				when (it) {
					is ImageUpdateState.Loading -> {
						if (!dialog?.isShowing!!) {
							dialog?.show()
						}
						progressDialog.setMsg(it.msg)
					}
					ImageUpdateState.Success -> {
						dialog?.dismiss()
						delay(500L)
						findNavController().popBackStack()
					}
					else -> dialog?.dismiss()
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
					if (!bind.cardProfile.isVisible) {
						val animate =
							AnimationUtils.loadAnimation(requireContext(), R.anim.slide_in_top)
						animate.duration = 600
						bind.cardProfile.startAnimation(animate)
						bind.cardProfile.visible()
					}
					bind.etName.setText(it.user.name)
					bind.etPhoneNo.setText(it.user.phoneNo)
					bind.imgProfilePic.glideUrl(it.user.profilePicUrl, R.drawable.user)
				}
				else -> Unit
			}
		}
	}


	override fun onDestroyView() {
		super.onDestroyView()
		_bind = null
		dialog = null
	}


}

