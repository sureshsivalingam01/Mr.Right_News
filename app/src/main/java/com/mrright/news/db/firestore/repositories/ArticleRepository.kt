package com.mrright.news.db.firestore.repositories

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.mrright.news.db.Resource
import com.mrright.news.db.ResourceNone
import com.mrright.news.db.api.responses.ArticleDTO
import com.mrright.news.di.UserCollection
import com.mrright.news.utils.constants.Collection
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ArticleRepoImpl @Inject constructor(
    private val auth: FirebaseAuth,
    @UserCollection private val userCollection: CollectionReference,
) : ArticleRepository {

    override suspend fun addArticle(article: ArticleDTO): ResourceNone {
        return try {
            val result =
                userCollection.document(auth.currentUser?.email!!).collection(Collection.ARTICLES)
                    .add(article).await()
            if (result != null) {
                ResourceNone.Success
            } else {
                throw Exception("Article doesn't add")
            }

        } catch (e: Exception) {
            ResourceNone.Failure(e)
        }
    }

    override suspend fun getArticle(articleId: String): Resource<ArticleDTO> {
        return try {
            val result =
                userCollection.document(auth.currentUser?.email!!).collection(Collection.ARTICLES)
                    .document(articleId)
                    .get().await().toObject(ArticleDTO::class.java)
            if (result != null) {
                Resource.Success(result)
            } else {
                throw Exception("No Article from article $articleId")
            }
        } catch (e: Exception) {
            Resource.Failure(e)
        }
    }

    override suspend fun deleteArticle(articleId: String): ResourceNone {
        return try {
            userCollection.document(auth.currentUser?.email!!).collection(Collection.ARTICLES)
                .document(articleId).delete().await()
            ResourceNone.Success
        } catch (e: Exception) {
            ResourceNone.Failure(e)
        }
    }
}


interface ArticleRepository {

    suspend fun addArticle(article: ArticleDTO): ResourceNone

    suspend fun getArticle(articleId: String): Resource<ArticleDTO>

    suspend fun deleteArticle(articleId: String): ResourceNone

}