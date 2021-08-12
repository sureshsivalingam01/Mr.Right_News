package com.mrright.news.ui.main.article

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.mrright.news.R
import com.mrright.news.databinding.ArticleFragmentBinding
import com.mrright.news.ui.states.MessageEvent
import com.mrright.news.ui.states.UIState
import com.mrright.news.utils.helpers.toast
import com.mrright.news.utils.helpers.visible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class ArticleFragment : Fragment() {

	private var _bind : ArticleFragmentBinding? = null
	private val bind get() = _bind!!

	private val args : ArticleFragmentArgs by navArgs()
	private val viewModel : ArticleViewModel by viewModels()

	override fun onCreateView(
		inflater : LayoutInflater,
		container : ViewGroup?,
		savedInstanceState : Bundle?,
	) : View {
		_bind = ArticleFragmentBinding.inflate(inflater, container, false)
		return bind.root
	}

	override fun onViewCreated(
		view : View,
		savedInstanceState : Bundle?,
	) {
		super.onViewCreated(view, savedInstanceState)
		btnCLicks()
		collectUI()
		collectUrl()
		collectIfLiked()
		collectMsg()
	}

	private fun collectUI() {
		lifecycleScope.launchWhenStarted {
			viewModel.uiState.collect {
				when (it) {
					UIState.Init -> getArgs()
					UIState.None -> Unit
				}
			}
		}
	}

	private fun collectMsg() {
		lifecycleScope.launchWhenStarted {
			viewModel.msgFlow.collect {
				when (it) {
					is MessageEvent.Toast -> requireContext().toast(it.msg)
					else -> Unit
				}
			}
		}
	}

	private fun collectIfLiked() {
		lifecycleScope.launchWhenCreated {
			viewModel.likedArticle.collect {
				when (it) {
					ArticleFabState.Liked -> {
						bind.fabLike.visible()
						bind.fabLike.setImageResource(R.drawable.heart_24_like)
					}
					ArticleFabState.UnLiked -> {
						bind.fabLike.visible()
						bind.fabLike.setImageResource(R.drawable.heart_24_unlike)
					}
				}
			}
		}
	}

	private fun btnCLicks() {
		bind.fabLike.setOnClickListener {
			if (viewModel.likedArticle.value == ArticleFabState.Liked) {
				viewModel.removeFromLiked()
			}
			else {
				viewModel.addToLiked()
			}
		}
	}

	private fun collectUrl() {
		viewModel.articleLiveData.observe(viewLifecycleOwner) {
			bind.webView.apply {
				webViewClient = WebViewClient()
				loadUrl(it.url!!)
			}
		}
	}

	private fun getArgs() {
		val article = args.article
		article?.let {
			viewModel.setArticle(it)
		}
	}

	override fun onDestroyView() {
		super.onDestroyView()
		_bind = null
	}


}