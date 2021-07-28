package com.mrright.news.ui.main.liked_articles

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.mrright.news.databinding.LikedArticleFragmentBinding

class LikedArticleFragment : Fragment() {

    private var _bind: LikedArticleFragmentBinding? = null
    private val bind get() = _bind!!

    private val viewModel: LikedArticleViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _bind = LikedArticleFragmentBinding.inflate(inflater, container, false)
        return bind.root
    }


}