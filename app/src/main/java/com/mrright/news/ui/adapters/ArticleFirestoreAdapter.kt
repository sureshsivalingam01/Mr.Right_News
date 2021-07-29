package com.mrright.news.ui.adapters

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
    options: FirestoreRecyclerOptions<ArticleFDTO>,
    private val onClick: (ArticleFDTO) -> Unit = {},
) : FirestoreRecyclerAdapter<
        ArticleFDTO,
        ArticleFirestoreAdapter.ArticleVH,
        >(options) {

    inner class ArticleVH(val bind: RvArticleBinding) : RecyclerView.ViewHolder(bind.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleVH {
        val bind = RvArticleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ArticleVH(bind)
    }

    override fun onBindViewHolder(holder: ArticleVH, position: Int, article: ArticleFDTO) {
        with(holder.bind) {
            imgArticle.glideUrl(article.urlToImage, R.drawable.ic_launcher_background)
            txtTitle.text = article.title
            root.setOnClickListener {
                if (position != RecyclerView.NO_POSITION) {
                    onClick(article)
                }
            }
        }
    }
}