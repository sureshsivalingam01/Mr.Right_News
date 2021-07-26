package com.mrright.news.ui.main.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.mrright.news.databinding.FragmentProfileBinding
import com.mrright.news.utils.glide
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private var _bind: FragmentProfileBinding? = null
    private val bind get() = _bind!!

    private val viewModel: ProfileViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _bind = FragmentProfileBinding.inflate(inflater, container, false)

        collectUserDetails()

        return bind.root
    }

    private fun collectUserDetails() {
        viewModel.userDetails.observe(viewLifecycleOwner) {
            with(bind) {
                imgProfile.glide(it.profilePicUrl)
                txtName.text = it.name
                txtEmail.text = it.email
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _bind = null
    }
}