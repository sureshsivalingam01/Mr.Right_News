package com.mrright.news.presentation.main.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mrright.news.R
import com.mrright.news.databinding.FragmentSearchBinding
import com.mrright.news.presentation.adapters.ArticlesAdapter
import com.mrright.news.presentation.states_events.MessageEvent
import com.mrright.news.presentation.states_events.UIState
import com.mrright.news.utils.ProgressDialog
import com.mrright.news.utils.constants.QUERY_PAGE_SIZE
import com.mrright.news.utils.helpers.toast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class SearchFragment : Fragment() {

	private var _bind : FragmentSearchBinding? = null
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
				viewModel.searchParticular()
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

	private val progressDialog = ProgressDialog()
	private var dialog : AlertDialog? = null

	private lateinit var articlesAdapter : ArticlesAdapter

	private val viewModel : SearchViewModel by activityViewModels()


	override fun onStart() {
		super.onStart()
		collectUIState()
		collectMessageEvent()
	}

	private fun collectMessageEvent() {
		lifecycleScope.launchWhenStarted {
			viewModel.msgEvent.collect {
				when (it) {
					is MessageEvent.Toast -> requireContext().toast(it.msg, it.duration)
					else -> Unit
				}
			}
		}
	}

	override fun onCreateView(
		inflater : LayoutInflater,
		container : ViewGroup?,
		savedInstanceState : Bundle?,
	) : View {
		_bind = FragmentSearchBinding.inflate(inflater, container, false)
		initView()
		return bind.root
	}

	private fun initView() {

		dialog = progressDialog.instance(requireActivity())

		articlesAdapter = ArticlesAdapter {

			findNavController().navigate(
				R.id.action_navigation_search_to_articleFragment,
				Bundle().apply { putSerializable("article", it) },
			)

		}

		bind.rvArticles.apply {
			adapter = articlesAdapter
			layoutManager = LinearLayoutManager(context)
			addOnScrollListener(this@SearchFragment.scrollListener)
		}
	}

	private fun collectUIState() {
		lifecycleScope.launchWhenStarted {
			viewModel.uiState.collect {
				when (it) {
					UIState.Init -> {
						bind.search.queryHint = "Tamil Nadu"
						viewModel.searchParticular(QueryEvent.Search())
						viewModel.changeUIState(UIState.None)
					}
					else -> Unit
				}
			}
		}
	}

	override fun onViewCreated(
		view : View,
		savedInstanceState : Bundle?,
	) {
		super.onViewCreated(view, savedInstanceState)

		collectNews()

		bind.search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

			override fun onQueryTextSubmit(query : String?) : Boolean {
				query?.let {
					viewModel.searchParticular(QueryEvent.Search(it))
				}
				return true
			}

			override fun onQueryTextChange(newText : String?) : Boolean {
				return true
			}
		})

	}

	private fun collectNews() {
		viewModel.news.observe(viewLifecycleOwner) {
			when (it) {
				is SearchState.Loading -> {

					if (!dialog?.isShowing!!) {
						dialog?.show()
					}
					progressDialog.setMsg(it.msg)
					isLoading = true
				}
				is SearchState.Error -> {
					dialog?.dismiss()
					isLoading = false
				}
				is SearchState.Paginated -> {
					isLoading = false
					articlesAdapter.submitList(it.value.articles)
					val totalPages = it.value.totalResults!! / QUERY_PAGE_SIZE + 2
					isLastPage = viewModel.queryNewsPage == totalPages
				}
				is SearchState.Searched -> {
					dialog?.dismiss()
					isLoading = false
					articlesAdapter.submitList(emptyList())
					articlesAdapter.submitList(it.value.articles)
					val totalPages = it.value.totalResults!! / QUERY_PAGE_SIZE + 2
					isLastPage = viewModel.queryNewsPage == totalPages
				}
				else -> {
					dialog?.dismiss()
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
		dialog?.dismiss()
	}

}