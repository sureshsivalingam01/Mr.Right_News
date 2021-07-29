package com.mrright.news.db.firestore.repositories

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.mrright.news.db.Resource
import com.mrright.news.db.Source
import com.mrright.news.db.FSource
import com.mrright.news.db.firestore.dto.ArticleFDTO
import com.mrright.news.di.UserCollection
import com.mrright.news.models.Article
import com.mrright.news.utils.constants.Collection
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ArticleRepoImpl @Inject constructor(
    auth: FirebaseAuth,
    @UserCollection private val userCollection: CollectionReference,
) : ArticleRepository {

    private val articleCollection =
        userCollection.document(auth.currentUser?.uid!!).collection(Collection.ARTICLES)

    override suspend fun addArticle(article: Article): FSource {
        return try {

            val id = articleCollection.document().id
            val newArticle = ArticleFDTO(
                id,
                article.author,
                article.content,
                article.description,
                article.publishedAt,
                article.title,
                article.url,
                article.urlToImage,
            )

            articleCollection.document(id).set(newArticle).await()
            FSource.Success

        } catch (e: Exception) {
            FSource.Failure(e)
        }
    }

    override suspend fun getArticleByUrl(articleUrl: String): Resource<ArticleFDTO> {
        return try {
            val result = articleCollection.whereEqualTo("url", articleUrl).limit(1).get().await()
            if (result != null) {
                Resource.Success(result.toObjects(ArticleFDTO::class.java)[0])
            } else {
                throw Exception("No Article from article $articleUrl")
            }
        } catch (e: Exception) {
            Resource.Failure(e)
        }
    }

    override suspend fun deleteArticle(articleId: String): Source {
        return try {
            articleCollection.document(articleId).delete().await()
            Source.SUCCESS
        } catch (e: Exception) {
            Source.FAILURE
        }
    }
}


interface ArticleRepository {

    suspend fun addArticle(article: Article): FSource

    suspend fun getArticleByUrl(articleUrl: String): Resource<ArticleFDTO>

    suspend fun deleteArticle(articleId: String): Source

}