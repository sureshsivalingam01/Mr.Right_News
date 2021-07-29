package com.mrright.news.ui.splash

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.mrright.news.databinding.ActivitySplashBinding
import com.mrright.news.ui.signing.SigningActivity
import com.mrright.news.ui.main.MainActivity
import com.mrright.news.ui.states.UIState
import com.mrright.news.utils.helpers.openActivity
import com.mrright.news.utils.helpers.shortToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {

    private lateinit var bind: ActivitySplashBinding

    private val splashVM: SplashViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(bind.root)

        collectUIState()
        collectUserState()

    }

    private fun collectUIState() {
        lifecycleScope.launchWhenCreated {
            splashVM.uiState.collect {
                when (it) {
                    UIState.Init -> splashVM.checkUser()
                    UIState.None -> Unit
                }
            }
        }
    }

    private fun collectUserState() {
        splashVM.isUserLoggedIn.observe(this@SplashActivity) {
            when (it) {
                is SplashViewModel.UserState.None -> Unit
                is SplashViewModel.UserState.Error -> shortToast(it.msg)
                is SplashViewModel.UserState.Loading -> shortToast(it.msg)
                is SplashViewModel.UserState.SignUp -> {
                    openActivity(SigningActivity::class.java)
                    finish()
                }
                is SplashViewModel.UserState.SignedIn -> {
                    openActivity(MainActivity::class.java)
                    finish()
                }
            }
        }
    }
}