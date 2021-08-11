package com.mrright.news.ui.main.liked_articles

import androidx.lifecycle.ViewModel
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.mrright.news.db.firestore.dto.ArticleFDTO
import com.mrright.news.utils.constants.Collection
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LikedArticleViewModel @Inject constructor(
    private val auth: FirebaseAuth
) : ViewModel() {

    private val query: Query = FirebaseFirestore.getInstance()
        .collection(Collection.USERS.value).document(auth.currentUser?.uid!!)
        .collection(Collection.ARTICLES.value)
        .orderBy("title")
        .limit(20)

    val options = FirestoreRecyclerOptions.Builder<ArticleFDTO>()
        .setQuery(query, ArticleFDTO::class.java)
        .build()

}