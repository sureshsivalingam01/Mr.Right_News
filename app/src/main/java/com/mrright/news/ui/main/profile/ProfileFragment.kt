package com.mrright.news.ui.main.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.mrright.news.databinding.FragmentProfileBinding
import com.mrright.news.ui.adapters.MenuAdapter
import com.mrright.news.ui.signing.SigningActivity
import com.mrright.news.utils.constants.Menu
import com.mrright.news.utils.glideUrl
import com.mrright.news.utils.inVisible
import com.mrright.news.utils.openActivity
import com.mrright.news.utils.visible
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private var _bind: FragmentProfileBinding? = null
    private val bind get() = _bind!!

    private val viewModel: ProfileViewModel by viewModels()

    private lateinit var menuAdapter: MenuAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _bind = FragmentProfileBinding.inflate(inflater, container, false)

        initRV()
        collectUserDetails()

        return bind.root
    }

    private fun initRV() {
        menuAdapter = MenuAdapter(viewModel.profileMenus) {
            when (it) {
                Menu.LIKED_ARTICLE -> TODO()
                Menu.EDIT_PROFILE -> TODO()
                Menu.SIGNOUT -> {
                    viewModel.signOut()
                    requireContext().openActivity(SigningActivity::class.java)
                    requireActivity().finish()
                }
            }
        }
        bind.rvMenus.apply {
            adapter = menuAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun collectUserDetails() {
        viewModel.userDetails.observe(viewLifecycleOwner) {
            with(bind) {
                when (it) {
                    is UserState.None -> Unit
                    is UserState.Error -> root.inVisible()
                    is UserState.Success -> {
                        imgProfile.glideUrl(it.user.profilePicUrl)
                        txtName.text = it.user.name
                        txtEmail.text = it.user.email
                        bind.root.visible()
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