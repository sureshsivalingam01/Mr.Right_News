package com.mrright.news.ui.adapters

import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mrright.news.R
import com.mrright.news.databinding.RvArticleBinding
import com.mrright.news.models.Article
import com.mrright.news.utils.helpers.glideUrl
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.*


class ArticlesAdapter(
    private val onClick: (article: Article) -> Unit = {}
) : ListAdapter<Article, ArticlesAdapter.ArticleVH>(ArticleUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleVH {
        val bind = RvArticleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ArticleVH(bind)
    }


    override fun onBindViewHolder(holder: ArticleVH, position: Int) {
        val article = currentList[position]

        with(holder.bind) {
            txtTitle.text = article.title
            txtDate.text = article.publishedAt
            imgArticle.glideUrl(article.urlToImage, R.drawable.ic_launcher_background)
        }
    }

    override fun getItemCount(): Int = currentList.size

    inner class ArticleVH(val bind: RvArticleBinding) : RecyclerView.ViewHolder(bind.root) {
        init {
            bind.root.setOnClickListener {
                onClick(currentList[absoluteAdapterPosition])
            }
        }
    }

    class ArticleUtil : DiffUtil.ItemCallback<Article>() {

        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem.url == newItem.url
        }

        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem == newItem
        }

    }

}
