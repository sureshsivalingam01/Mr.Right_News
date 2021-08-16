package com.mrright.news.presentation.signing

import android.content.Intent
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.mrright.news.R
import com.mrright.news.databinding.ActivitySigningBinding
import com.mrright.news.presentation.main.MainActivity
import com.mrright.news.presentation.states_events.MessageEvent
import com.mrright.news.utils.*
import com.mrright.news.utils.constants.SIGN
import com.mrright.news.utils.helpers.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import javax.inject.Inject

@AndroidEntryPoint
class SigningActivity : AppCompatActivity() {

	private lateinit var bind : ActivitySigningBinding

	private lateinit var googleSignIn : ActivityResultLauncher<Intent>
	private lateinit var googleSignUp : ActivityResultLauncher<Intent>

	private val progressDialog = ProgressDialog()
	private lateinit var dialog : AlertDialog

	private val viewModel : SigningViewModel by viewModels()

	@Inject
	lateinit var googleSignInClient : GoogleSignInClient

	override fun onCreate(savedInstanceState : Bundle?) {
		super.onCreate(savedInstanceState)
		bind = ActivitySigningBinding.inflate(layoutInflater)
		setContentView(bind.root)

		dialog = progressDialog.instance(this)

		activityResults()
		clicks()

		collectUIState()
		collectAuthState()
		collectMessage()


	}

	private fun collectAuthState() {
		viewModel.authSigning.observe(this@SigningActivity) { signingState ->
			when (signingState) {
				is SigningState.LoadingText -> {

					if (dialog.isShowing) {
						progressDialog.setMsg(signingState.msg)
					}
					else {
						dialog.show()
						progressDialog.setMsg(signingState.msg)
					}
				}
				is SigningState.LoadingStringRes -> {
					if (dialog.isShowing) {
						progressDialog.setMsg(getString(signingState.stringId))
					}
					else {
						dialog.show()
						progressDialog.setMsg(getString(signingState.stringId))
					}
				}
				is SigningState.SignedIn -> {
					dialog.dismiss()
					openActivity(MainActivity::class.java)
					finish()
				}
				is SigningState.SignedUp -> {
					dialog.dismiss()
					openActivity(MainActivity::class.java)
					finish()
				}
				SigningState.Error -> dialog.dismiss()
				else -> Unit
			}
		}
	}

	private fun collectUIState() {
		lifecycleScope.launchWhenCreated {
			viewModel.signingUIState.collect {
				with(bind) {
					when (it) {
						SigningUIState.SignIn -> {

							btnSignIn.visible()
							btnSignUp.gone()
							txtToggle.text = getString(R.string.new_user_register)

						}
						SigningUIState.SignUp -> {

							btnSignIn.gone()
							btnSignUp.visible()
							txtToggle.text = getString(R.string.old_user_sign_in)
						}
					}
				}
			}
		}
	}

	private fun collectMessage() {
		lifecycleScope.launchWhenStarted {
			viewModel.msgFlow.collect {
				when (it) {
					is MessageEvent.SnackBar -> Unit
					is MessageEvent.ToastStringRes -> toast(it.stringId)
					is MessageEvent.Toast -> toast(it.msg)
				}
			}
		}
	}

	private fun clicks() {
		with(bind) {
			btnSignIn.setOnClickListener {
				googleSignIn.launch(googleSignInClient.signInIntent)
			}
			btnSignUp.setOnClickListener {
				googleSignUp.launch(googleSignInClient.signInIntent)
			}
			txtToggle.setOnClickListener {
				viewModel.toggle()
			}
		}
	}

	private fun activityResults() {

		googleSignIn = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
			warnLog("activityResults | ${it.resultCode}")
			if (it.resultCode == RESULT_OK) {
				val task = GoogleSignIn.getSignedInAccountFromIntent(it.data)
				viewModel.googleSigning(SIGN.IN, task)
			}
		}

		googleSignUp = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
			warnLog("activityResults | ${it.resultCode}")
			if (it.resultCode == RESULT_OK) {
				val task = GoogleSignIn.getSignedInAccountFromIntent(it.data)
				viewModel.googleSigning(SIGN.UP, task)
			}
		}


	}

}