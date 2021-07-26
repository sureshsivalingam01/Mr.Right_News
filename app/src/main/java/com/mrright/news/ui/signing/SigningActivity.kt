package com.mrright.news.ui.signing

import android.content.Intent
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.mrright.news.databinding.ActivityLoggingBinding
import com.mrright.news.utils.constants.SIGN
import com.mrright.news.utils.shortToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import javax.inject.Inject

@AndroidEntryPoint
class SigningActivity : AppCompatActivity() {

    private lateinit var bind: ActivityLoggingBinding

    private lateinit var googleSignIn: ActivityResultLauncher<Intent>
    private lateinit var googleSignUp: ActivityResultLauncher<Intent>

    private val viewModel: SigningViewModel by viewModels()

    @Inject
    lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivityLoggingBinding.inflate(layoutInflater)
        setContentView(bind.root)

        activityResult()
        btnClicks()

        collectUIState()
        collectAuthState()


    }

    private fun collectAuthState() {
        lifecycleScope.launchWhenCreated {
            viewModel.authSigningFlow.collect {
                when (it) {
                    is SigningViewModel.SigningState.Loading -> shortToast(it.msg)
                    is SigningViewModel.SigningState.Error -> shortToast(it.msg)
                    is SigningViewModel.SigningState.SignedIn -> shortToast(it.name)
                    is SigningViewModel.SigningState.SignedUp -> shortToast(it.name)
                    else -> Unit
                }
            }
        }
    }

    private fun collectUIState() {
        lifecycleScope.launchWhenCreated {
            viewModel.signingUIState.collect {
                when (it) {
                    SigningViewModel.SigningUIState.None -> Unit
                    SigningViewModel.SigningUIState.SignIn -> TODO()
                    SigningViewModel.SigningUIState.SignUp -> TODO()
                }
            }
        }
    }

    private fun btnClicks() {
        with(bind) {
            btnSignIn.setOnClickListener {
                googleSignIn.launch(googleSignInClient.signInIntent)
            }
            btnSignUp.setOnClickListener {
                googleSignUp.launch(googleSignInClient.signInIntent)
            }
        }
    }

    private fun activityResult() {

        googleSignIn = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(it.data)
                viewModel.googleSigning(task, SIGN.IN)
            }
        }

        googleSignUp = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(it.data)
                viewModel.googleSigning(task, SIGN.UP)
            }
        }


    }

}