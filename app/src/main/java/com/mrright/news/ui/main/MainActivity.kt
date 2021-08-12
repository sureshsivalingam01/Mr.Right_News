package com.mrright.news.ui.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.mrright.news.R
import com.mrright.news.databinding.ActivityMainBinding
import com.mrright.news.utils.helpers.inVisible
import com.mrright.news.utils.helpers.setStringResTitle
import com.mrright.news.utils.helpers.visible
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

	private lateinit var bind : ActivityMainBinding

	override fun onCreate(savedInstanceState : Bundle?) {
		super.onCreate(savedInstanceState)
		bind = ActivityMainBinding.inflate(layoutInflater)
		setContentView(bind.root)

		val navController = findNavController(R.id.nav_host_main)
		bind.btmNav.setupWithNavController(navController)

		navController.addOnDestinationChangedListener { _, destination, _ ->

			with(bind) {
				when (destination.id) {
					R.id.navigation_home -> {
						topAppBar.setStringResTitle(R.string.home)
						btmNav.visible()
						topAppBar.navigationIcon = null
					}
					R.id.navigation_search -> {
						topAppBar.setStringResTitle(R.string.search)
						btmNav.visible()
						topAppBar.navigationIcon = null
					}
					R.id.navigation_profile -> {
						topAppBar.setStringResTitle(R.string.profile)
						btmNav.visible()
						topAppBar.navigationIcon = null
					}
					R.id.articleFragment -> {
						topAppBar.setStringResTitle(R.string.article)
						btmNav.inVisible()
						topAppBar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24)
					}
					R.id.likedArticleFragment -> {
						topAppBar.setStringResTitle(R.string.liked_articles)
						btmNav.inVisible()
						topAppBar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24)
					}
					R.id.editProfileFragment -> {
						topAppBar.setStringResTitle(R.string.edit_profile)
						btmNav.inVisible()
						topAppBar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24)
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








