package com.mrright.news.ui.main.edit_profile

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.mrright.news.R
import com.mrright.news.databinding.EditProfileFragmentBinding
import com.mrright.news.databinding.LikedArticleFragmentBinding
import com.mrright.news.ui.main.liked_articles.LikedArticleViewModel

class EditProfileFragment : Fragment() {

    private var _bind: EditProfileFragmentBinding? = null
    private val bind get() = _bind!!

    private val viewModel: EditProfileViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _bind = EditProfileFragmentBinding.inflate(inflater, container, false)
        return bind.root
    }


}