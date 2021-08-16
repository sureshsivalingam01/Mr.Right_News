package com.mrright.news.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mrright.news.R
import com.mrright.news.databinding.RvArticleBinding
import com.mrright.news.models.Article
import com.mrright.news.utils.helpers.glideUrl


class ArticlesAdapter(
	private val onClick : (article : Article) -> Unit = {},
) : ListAdapter<Article, ArticlesAdapter.ArticleVH>(ArticleUtil()) {

	override fun onCreateViewHolder(
		parent : ViewGroup,
		viewType : Int,
	) : ArticleVH {
		return ArticleVH(RvArticleBinding.inflate(
			LayoutInflater.from(parent.context),
			parent,
			false,
		))
	}


	override fun onBindViewHolder(
		holder : ArticleVH,
		position : Int,
	) {
		val article = currentList[position]

		with(holder.bind) {
			txtTitle.text = article.title
			txtDate.text = article.publishedAt
			imgArticle.glideUrl(article.urlToImage ?: "", R.drawable.news_placeholder)
		}
	}

	override fun getItemCount() : Int = currentList.size

	inner class ArticleVH(val bind : RvArticleBinding) : RecyclerView.ViewHolder(bind.root) {
		init {
			bind.root.setOnClickListener {
				onClick(currentList[absoluteAdapterPosition])
			}
		}
	}

	class ArticleUtil : DiffUtil.ItemCallback<Article>() {

		override fun areItemsTheSame(
			oldItem : Article,
			newItem : Article,
		) : Boolean {
			return oldItem.url == newItem.url
		}

		override fun areContentsTheSame(
			oldItem : Article,
			newItem : Article,
		) : Boolean {
			return oldItem == newItem
		}

	}

}
