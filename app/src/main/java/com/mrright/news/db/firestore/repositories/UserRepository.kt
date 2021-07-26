package com.mrright.news.db.firestore.repositories

import com.google.firebase.firestore.CollectionReference
import com.mrright.news.db.api.Resource
import com.mrright.news.di.UserCollection
import com.mrright.news.models.User
import com.mrright.news.utils.errorLog
import com.mrright.news.utils.infoLog
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


class UserRepoImpl @Inject constructor(
    @UserCollection private val userCollection: CollectionReference
) : UserRepository {

    override suspend fun createUser(
        user: User,
    ): Resource<String> {
        return try {
            val result = userCollection.document(user.email)
                .set(user)
                .await()
            infoLog("createUser | Success | $result")
            Resource.Success("Success")
        } catch (e: Exception) {
            errorLog("createUser | Exception", e)
            Resource.Exception(e)
        }
    }

    override suspend fun getUser(
        documentId: String,
    ): Resource<User> {

        return try {
            val result = userCollection.document(documentId)
                .get()
                .await()

            result?.toObject(User::class.java)
                ?.let {
                    infoLog("getAccountDetails | Success | $it")
                    Resource.Success(it)
                } ?: Resource.Error("No Response")

        } catch (e: Exception) {
            errorLog("getAccountDetails | Exception", e)
            Resource.Exception(e)
        }
    }

}


interface UserRepository {

    suspend fun createUser(user: User): Resource<String>

    suspend fun getUser(documentId: String): Resource<User>

}