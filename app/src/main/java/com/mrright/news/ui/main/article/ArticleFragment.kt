package com.mrright.news.ui.main.article

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import com.mrright.news.databinding.ArticleFragmentBinding
import com.mrright.news.models.Article

class ArticleFragment : Fragment() {

    private var _bind: ArticleFragmentBinding? = null
    private val bind get() = _bind!!

    private val args: ArticleFragmentArgs by navArgs()
    private val articleViewModel: ArticleViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _bind = ArticleFragmentBinding.inflate(inflater, container, false)
        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getArgs()
        collectUrl()
    }

    private fun collectUrl() {
        articleViewModel.url.observe(viewLifecycleOwner) {
            bind.webView.apply {
                webViewClient = WebViewClient()
                loadUrl(it.url!!)
            }
        }
    }

    private fun getArgs() {
        val article = args.article
        article?.let {
            articleViewModel.setArticle(it)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        articleViewModel.setArticle(Article())
        _bind = null
    }


}