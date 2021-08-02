package com.mrright.news.db.firestore.repositories

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.mrright.news.db.Resource
import com.mrright.news.db.Source
import com.mrright.news.db.firestore.dto.ArticleFDTO
import com.mrright.news.di.UserCollection
import com.mrright.news.models.Article
import com.mrright.news.utils.constants.Collection
import com.mrright.news.utils.exceptions.NoLikedArticleException
import com.mrright.news.utils.helpers.errorLog
import com.mrright.news.utils.helpers.infoLog
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ArticleRepoImpl @Inject constructor(
    auth: FirebaseAuth,
    @UserCollection private val userCollection: CollectionReference,
) : ArticleRepository {

    private val articleCollection =
        userCollection.document(auth.currentUser?.uid!!).collection(Collection.ARTICLES)

    override suspend fun addArticle(article: Article): Resource<String> {
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
            infoLog("addArticle | Success | $newArticle")
            Resource.Success(id)

        } catch (e: Exception) {
            errorLog("addArticle | Failure | ${e.message}")
            Resource.Failure(e)
        }
    }

    override suspend fun getArticleByUrl(articleUrl: String): Resource<ArticleFDTO> {
        return try {
            val result = articleCollection.whereEqualTo("url", articleUrl).limit(1).get().await()
                .toObjects(ArticleFDTO::class.java)
            when (result.size) {
                1 -> {
                    val article = result[0]
                    infoLog("getArticleByUrl | Success | $article")
                    Resource.Success(article)
                }
                else -> {
                    throw NoLikedArticleException()
                }
            }
        } catch (e: Exception) {
            errorLog("getArticleByUrl | Failure | ${e.message}")
            Resource.Failure(e)
        }
    }

    override suspend fun deleteArticle(articleId: String): Source {
        return try {
            articleCollection.document(articleId).delete().await()
            infoLog("deleteArticle | Success | $articleId")
            Source.Success
        } catch (e: Exception) {
            errorLog("deleteArticle | Failure | ${e.message}")
            Source.Failure(e)
        }
    }
}


interface ArticleRepository {

    suspend fun addArticle(article: Article): Resource<String>

    suspend fun getArticleByUrl(articleUrl: String): Resource<ArticleFDTO>

    suspend fun deleteArticle(articleId: String): Source

}