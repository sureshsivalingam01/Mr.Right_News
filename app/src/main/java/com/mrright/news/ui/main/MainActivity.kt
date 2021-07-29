package com.mrright.news.ui.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.mrright.news.R
import com.mrright.news.databinding.ActivityMainBinding
import com.mrright.news.utils.helpers.gone
import com.mrright.news.utils.helpers.visible
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

            with(bind) {
                when (destination.id) {
                    R.id.navigation_home -> {
                        topAppBar.title = "Home"
                        btmNav.visible()
                        topAppBar.navigationIcon = null
                    }
                    R.id.navigation_search -> {
                        topAppBar.title = "Search"
                        btmNav.visible()
                        topAppBar.navigationIcon = null
                    }
                    R.id.navigation_profile -> {
                        topAppBar.title = "Profile"
                        btmNav.visible()
                        topAppBar.navigationIcon = null
                    }
                    R.id.articleFragment -> {
                        topAppBar.title = "Article"
                        btmNav.gone()
                        topAppBar.setNavigationIcon(R.drawable.ic_home_black_24dp)
                    }
                    else -> Unit
                }
            }

        }

        bind.topAppBar.setNavigationOnClickListener {
            //onBackPressed()
            navController.popBackStack()
        }

    }


}








