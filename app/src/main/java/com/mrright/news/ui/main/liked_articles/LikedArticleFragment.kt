package com.mrright.news.ui.main.liked_articles

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.mrright.news.R
import com.mrright.news.databinding.LikedArticleFragmentBinding
import com.mrright.news.db.firestore.dto.ArticleFDTO
import com.mrright.news.ui.adapters.ArticleFirestoreAdapter
import com.mrright.news.utils.constants.Collection
import com.mrright.news.utils.helpers.shortToast
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class LikedArticleFragment : Fragment() {

    private var _bind: LikedArticleFragmentBinding? = null
    private val bind get() = _bind!!

    @Inject
    lateinit var auth: FirebaseAuth

    private val viewModel: LikedArticleViewModel by activityViewModels()

    private lateinit var articleAdapter: ArticleFirestoreAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _bind = LikedArticleFragmentBinding.inflate(inflater, container, false)
        return bind.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val query: Query = FirebaseFirestore.getInstance()
            .collection(Collection.USERS).document(auth.currentUser?.uid!!)
            .collection(Collection.ARTICLES)
            .orderBy("title")
            .limit(20)

        val options = FirestoreRecyclerOptions.Builder<ArticleFDTO>()
            .setQuery(query, ArticleFDTO::class.java)
            .build()

        articleAdapter = ArticleFirestoreAdapter(options) {
           findNavController().navigate(
               R.id.action_likedArticleFragment_to_articleFragment,
               Bundle().apply {
                   putSerializable("article",it.toArticle())
               }
           )
        }

        bind.rvLikedArticles.apply {
            adapter = articleAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

    }

    override fun onStart() {
        super.onStart()
        articleAdapter.startListening()
    }

}