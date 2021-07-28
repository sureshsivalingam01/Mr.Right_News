package com.mrright.news.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mrright.news.databinding.RvProfileMenusBinding
import com.mrright.news.utils.constants.Menu
import com.mrright.news.utils.glideDrawableRes
import com.mrright.news.utils.setStringRes


class MenuAdapter(
    private val menus: List<Menu>,
    private val onClick: (Menu) -> Unit = {}
) : RecyclerView.Adapter<MenuAdapter.MenuViewHolder>() {

    inner class MenuViewHolder(val bind: RvProfileMenusBinding) :
        RecyclerView.ViewHolder(bind.root) {

        init {
            bind.root.setOnClickListener {
                onClick(menus[absoluteAdapterPosition])
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        val bind = RvProfileMenusBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MenuViewHolder(bind)
    }

    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {

        with(holder.bind) {
            txtName.setStringRes(menus[position].menu)
            imgIcon.glideDrawableRes(menus[position].icon)
        }
    }

    override fun getItemCount(): Int = menus.size
}