package com.mrright.news.db.firestore.repositories

import com.google.firebase.firestore.CollectionReference
import com.mrright.news.db.Resource
import com.mrright.news.db.FSource
import com.mrright.news.db.firestore.dto.UserDTO
import com.mrright.news.di.UserCollection
import com.mrright.news.utils.helpers.errorLog
import com.mrright.news.utils.helpers.infoLog
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


class UserRepoImpl @Inject constructor(
    @UserCollection private val userCollection: CollectionReference
) : UserRepository {

    override suspend fun createUser(
        user: UserDTO,
    ): FSource {
        return try {
            val result = userCollection.document(user.uid)
                .set(user)
                .await()
            infoLog("createUser | Success | $result")
            FSource.Success
        } catch (e: Exception) {
            errorLog("createUser | Exception", e)
            FSource.Failure(e)
        }
    }

    override suspend fun getUser(
        uid: String,
    ): Resource<UserDTO> {

        return try {
            val result = userCollection.document(uid)
                .get()
                .await().toObject(UserDTO::class.java)

            if (result != null) {
                infoLog("getAccountDetails | Success | $result")
                Resource.Success(result)
            } else {
                throw Exception("No User Found")
            }
        } catch (e: Exception) {
            errorLog("getAccountDetails | Exception", e)
            Resource.Failure(e)
        }
    }

}


interface UserRepository {

    suspend fun createUser(user: UserDTO): FSource

    suspend fun getUser(uid: String): Resource<UserDTO>

}