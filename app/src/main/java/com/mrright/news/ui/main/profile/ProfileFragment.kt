package com.mrright.news.ui.main.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.mrright.news.databinding.FragmentProfileBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private lateinit var profileViewModel: ProfileViewModel
    private var _bind: FragmentProfileBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val bind get() = _bind!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        profileViewModel =
            ViewModelProvider(this).get(ProfileViewModel::class.java)

        _bind = FragmentProfileBinding.inflate(inflater, container, false)

        profileViewModel.text.observe(viewLifecycleOwner, {
            bind.txtProfile.text = it
        })
        return bind.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _bind = null
    }
}