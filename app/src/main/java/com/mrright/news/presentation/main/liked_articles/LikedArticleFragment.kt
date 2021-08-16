package com.mrright.news.presentation.main.liked_articles

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.mrright.news.R
import com.mrright.news.databinding.LikedArticleFragmentBinding
import com.mrright.news.presentation.adapters.ArticleFirestoreAdapter
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class LikedArticleFragment : Fragment() {

	private var _bind : LikedArticleFragmentBinding? = null
	private val bind get() = _bind!!

	private lateinit var articleAdapter : ArticleFirestoreAdapter

	private val viewModel : LikedArticleViewModel by activityViewModels()

	override fun onCreateView(
		inflater : LayoutInflater,
		container : ViewGroup?,
		savedInstanceState : Bundle?,
	) : View {
		_bind = LikedArticleFragmentBinding.inflate(inflater, container, false)
		return bind.root
	}


	override fun onViewCreated(
		view : View,
		savedInstanceState : Bundle?,
	) {
		super.onViewCreated(view, savedInstanceState)


		//onItemView Click Navigates to Article Fragment
		articleAdapter = ArticleFirestoreAdapter(viewModel.options) {
			findNavController().navigate(
				R.id.action_likedArticleFragment_to_articleFragment,
				Bundle().apply { putSerializable("article", it.toArticle()) },
			)
		}

		//init Liked Articles RecyclerView
		bind.rvLikedArticles.apply {
			adapter = articleAdapter
			layoutManager = LinearLayoutManager(requireContext())
		}

	}


	override fun onStart() {
		super.onStart()
		//StartListening Must for Firestore RecyclerView
		articleAdapter.startListening()
	}

	override fun onDestroyView() {
		super.onDestroyView()
		_bind = null
	}

	override fun onDestroy() {
		super.onDestroy()
		//StopListening Must for Firestore RecyclerView
		articleAdapter.stopListening()
	}

}