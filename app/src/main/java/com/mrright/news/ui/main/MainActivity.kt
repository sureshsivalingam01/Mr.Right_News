package com.mrright.news.ui.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.mrright.news.R
import com.mrright.news.databinding.ActivityMainBinding
import com.mrright.news.utils.gone
import com.mrright.news.utils.visible
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var bind: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivityMainBinding.inflate(layoutInflater)
        setContentView(bind.root)

        val navController = findNavController(R.id.nav_host_main)
        bind.btmNav.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->

            when (destination.id) {
                R.id.navigation_home -> {
                    bind.topAppBar.title = "Home"
                    bind.btmNav.visible()
                    bind.topAppBar.navigationIcon = null
                }
                R.id.navigation_search -> {
                    bind.topAppBar.title = "Search"
                    bind.btmNav.visible()
                    bind.topAppBar.navigationIcon = null
                }
                R.id.navigation_profile -> {
                    bind.topAppBar.title = "Profile"
                    bind.btmNav.visible()
                    bind.topAppBar.navigationIcon = null
                }
                R.id.articleFragment -> {
                    bind.topAppBar.title = "Article"
                    bind.btmNav.gone()
                    bind.topAppBar.setNavigationIcon(R.drawable.ic_home_black_24dp)
                }
                else -> Unit
            }
        }

        bind.topAppBar.setNavigationOnClickListener {
            onBackPressed()
        }

    }


}








