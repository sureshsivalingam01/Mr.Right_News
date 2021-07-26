package com.mrright.news.ui.signing

import android.content.Intent
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.mrright.news.databinding.ActivitySigningBinding
import com.mrright.news.ui.main.MainActivity
import com.mrright.news.utils.*
import com.mrright.news.utils.constants.SIGN
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import javax.inject.Inject

@AndroidEntryPoint
class SigningActivity : AppCompatActivity() {

    private lateinit var bind: ActivitySigningBinding

    private lateinit var googleSignIn: ActivityResultLauncher<Intent>
    private lateinit var googleSignUp: ActivityResultLauncher<Intent>

    private val viewModel: SigningViewModel by viewModels()

    @Inject
    lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivitySigningBinding.inflate(layoutInflater)
        setContentView(bind.root)

        activityResult()
        btnClicks()

        collectUIState()
        collectAuthState()


    }

    private fun collectAuthState() {
        viewModel.authSigning.observe(this@SigningActivity) {
            when (it) {
                is SigningViewModel.SigningState.Loading -> {
                    with(bind) {
                        if (cardLoading.isVisible) {
                            txtMsg.text = it.msg
                        } else {
                            cardLoading.visible()
                            txtMsg.text = it.msg
                        }
                    }
                }
                is SigningViewModel.SigningState.Error -> {
                    bind.cardLoading.inVisible()
                    shortToast(it.msg)
                }
                is SigningViewModel.SigningState.SignedIn -> {
                    bind.cardLoading.inVisible()
                    shortToast(it.name)
                    openActivity(MainActivity::class.java)
                    finish()
                }
                is SigningViewModel.SigningState.SignedUp -> {
                    bind.cardLoading.inVisible()
                    shortToast(it.name)
                    openActivity(MainActivity::class.java)
                    finish()
                }
                else -> Unit
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