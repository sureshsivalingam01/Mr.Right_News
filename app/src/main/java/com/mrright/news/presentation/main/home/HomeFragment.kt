package com.mrright.news.presentation.main.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mrright.news.R
import com.mrright.news.databinding.FragmentHomeBinding
import com.mrright.news.presentation.adapters.ArticlesAdapter
import com.mrright.news.presentation.states_events.UIState
import com.mrright.news.utils.constants.QUERY_PAGE_SIZE
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class HomeFragment : Fragment() {

	private var _bind : FragmentHomeBinding? = null
	private val bind get() = _bind!!

	private var isLoading = false
	private var isLastPage = false
	private var isScrolling = false

	private val scrollListener = object : RecyclerView.OnScrollListener() {
		override fun onScrolled(
			recyclerView : RecyclerView,
			dx : Int,
			dy : Int,
		) {
			super.onScrolled(recyclerView, dx, dy)

			val layoutManager = recyclerView.layoutManager as LinearLayoutManager
			val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
			val visibleItemCount = layoutManager.childCount
			val totalItemCount = layoutManager.itemCount

			val isNotLoadingAndNotLastPage = !isLoading && !isLastPage
			val isAtLastItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount
			val isNotAtBeginning = firstVisibleItemPosition >= 0
			val isTotalMoreThanVisible = totalItemCount >= QUERY_PAGE_SIZE
			val shouldPaginate = isNotLoadingAndNotLastPage && isAtLastItem && isNotAtBeginning && isTotalMoreThanVisible && isScrolling
			if (shouldPaginate) {
				viewModel.getTopHeadlines()
				isScrolling = false
			}
			else {
				bind.rvArticles.setPadding(0, 0, 0, 0)
			}
		}

		override fun onScrollStateChanged(
			recyclerView : RecyclerView,
			newState : Int,
		) {
			super.onScrollStateChanged(recyclerView, newState)
			if (articlesAdapter.currentList.size < viewModel.listSize) {
				if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
					isScrolling = true
				}
			}
		}
	}

	private lateinit var articlesAdapter : ArticlesAdapter

	private val viewModel : HomeViewModel by activityViewModels()

	override fun onStart() {
		super.onStart()
		collectUIState()
	}

	override fun onCreateView(
		inflater : LayoutInflater,
		container : ViewGroup?,
		savedInstanceState : Bundle?,
	) : View {
		_bind = FragmentHomeBinding.inflate(inflater, container, false)
		initView()
		return bind.root
	}

	override fun onViewCreated(
		view : View,
		savedInstanceState : Bundle?,
	) {
		super.onViewCreated(view, savedInstanceState)
		collectNews()
	}

	private fun initView() {

		articlesAdapter = ArticlesAdapter {
			findNavController().navigate(
				R.id.action_navigation_home_to_articleFragment,
				Bundle().apply { putSerializable("article", it) },
			)
		}

		bind.rvArticles.apply {
			adapter = articlesAdapter
			layoutManager = LinearLayoutManager(context)
			addOnScrollListener(this@HomeFragment.scrollListener)
		}
	}

	private fun collectUIState() {
		lifecycleScope.launchWhenStarted {
			viewModel.uiState.collect {
				when (it) {
					UIState.Init -> {
						viewModel.getTopHeadlines()
						viewModel.changeUIState(UIState.None)
					}
					else -> Unit
				}
			}
		}
	}

	private fun collectNews() {
		viewModel.news.observe(viewLifecycleOwner) {
			when (it) {
				is HeadlinesState.Loading -> {
					isLoading = true
				}
				is HeadlinesState.Failed -> {
					isLoading = false
				}
				is HeadlinesState.Success -> {
					isLoading = false
					val news = it.news
					articlesAdapter.submitList(news.articles)
					val totalPages = news.totalResults!! / QUERY_PAGE_SIZE + 2
					isLastPage = viewModel.breakingNewsPage == totalPages
				}
				else -> {
					if (isLoading) {
						isLoading = false
					}
				}
			}
		}
	}

	override fun onDestroyView() {
		super.onDestroyView()
		_bind = null
	}

}