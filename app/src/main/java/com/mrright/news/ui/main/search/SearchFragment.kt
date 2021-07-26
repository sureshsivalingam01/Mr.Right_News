package com.mrright.news.ui.main.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.mrright.news.R
import com.mrright.news.databinding.FragmentSearchBinding
import com.mrright.news.ui.adapters.ArticlesAdapter
import com.mrright.news.ui.states.NetworkState
import com.mrright.news.ui.states.UIState
import com.mrright.news.utils.shortToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class SearchFragment : Fragment() {

    private var _bind: FragmentSearchBinding? = null
    private val bind get() = _bind!!

    private val searchViewModel: SearchViewModel by activityViewModels()

    private lateinit var articlesAdapter: ArticlesAdapter

    override fun onStart() {
        super.onStart()
        collectUIState()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _bind = FragmentSearchBinding.inflate(inflater, container, false)
        initView()
        return bind.root
    }

    private fun initView() {
        articlesAdapter = ArticlesAdapter {

            findNavController().navigate(
                R.id.action_navigation_search_to_articleFragment,
                Bundle().apply {
                    putSerializable("article", it)
                },
            )
        }
        bind.rvArticles.apply {
            adapter = articlesAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun collectUIState() {
        lifecycleScope.launchWhenStarted {
            searchViewModel.uiState.collect {
                when (it) {
                    UIState.Init -> {
                        searchViewModel.searchParticular()
                        searchViewModel.changeUIState(UIState.None)
                    }
                    else -> Unit
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        collectNews()

        bind.search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    searchViewModel.searchParticular(it)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }
        })

    }

    private fun collectNews() {
        searchViewModel.news.observe(viewLifecycleOwner) {
            when (it) {
                is NetworkState.Loading -> {
                    shortToast(it.msg)
                }
                is NetworkState.Error -> {
                    shortToast(it.msg)
                }
                is NetworkState.Success -> {
                    articlesAdapter.submitList(it.value.articles)
                    articlesAdapter.notifyDataSetChanged()
                }
                else -> Unit
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _bind = null
    }
}