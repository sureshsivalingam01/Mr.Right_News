package com.mrright.news.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.mrright.news.R
import com.mrright.news.databinding.RvArticleBinding
import com.mrright.news.db.firestore.dto.ArticleFDTO
import com.mrright.news.utils.helpers.glideUrl

class ArticleFirestoreAdapter(
	options : FirestoreRecyclerOptions<ArticleFDTO>,
	private val onClick : (ArticleFDTO) -> Unit = {},
) : FirestoreRecyclerAdapter<
		ArticleFDTO,
		ArticleFirestoreAdapter.ArticleVH,
		>(options) {

	inner class ArticleVH(val bind : RvArticleBinding) : RecyclerView.ViewHolder(bind.root)

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
		article : ArticleFDTO,
	) {
		with(holder.bind) {
			imgArticle.glideUrl(article.urlToImage ?: "", R.drawable.news_placeholder)
			txtTitle.text = article.title
			txtDate.text = article.publishedAt
			root.setOnClickListener {
				if (position != RecyclerView.NO_POSITION) {
					onClick(article)
				}
			}
		}
	}
}