package com.mrright.news.db.firestore.repositories

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.storage.FirebaseStorage
import com.mrright.news.db.Resource
import com.mrright.news.db.Source
import com.mrright.news.db.firestore.dto.UserDTO
import com.mrright.news.di.UserCollection
import com.mrright.news.models.User
import com.mrright.news.ui.main.profile.UserState
import com.mrright.news.utils.constants.State
import com.mrright.news.utils.exceptions.NoUserException
import com.mrright.news.utils.helpers.errorLog
import com.mrright.news.utils.helpers.infoLog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


class UserRepoImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val storage: FirebaseStorage,
    @UserCollection private val userCollection: CollectionReference,
) : UserRepository {

    override suspend fun createUser(
        user: UserDTO,
    ): Source {
        return try {
            val result = userCollection.document(user.uid)
                .set(user)
                .await()
            infoLog("createUser | Success | $result")
            Source.Success
        } catch (e: Exception) {
            errorLog("createUser | Exception", e)
            Source.Failure(e)
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
                throw NoUserException()
            }
        } catch (e: Exception) {
            errorLog("getAccountDetails | Exception", e)
            Resource.Failure(e)
        }
    }


    override suspend fun updateUser(name: String?, phoneNo: String?): Flow<Source> = flow {

        try {
            val map: MutableMap<String, Any> = mutableMapOf()
            if (name != null) {
                map["name"] = name
            }
            if (phoneNo != null) {
                map["phoneNo"] = phoneNo
            }

            userCollection.document(auth.currentUser?.uid!!).update(map).await()
            infoLog("updateUser | Success | Updated")
            emit(Source.Success)
        } catch (e: Exception) {
            errorLog("updateUser | Failure | ${e.message}")
            emit(Source.Failure(e))
        }
    }

    @ExperimentalCoroutinesApi
    override suspend fun userDetailsCallBack(): Flow<UserState> = callbackFlow {
        val listener =
            userCollection.document(auth.currentUser?.uid!!).addSnapshotListener { value, error ->
                error?.let {
                    trySend(UserState.Error)
                    cancel(error.message ?: "", error)
                    errorLog("getUserCallBack | Failed | ${error.message}")
                    return@addSnapshotListener
                }

                trySend(UserState.Success(value?.toObject(User::class.java) ?: User()))
                infoLog("getUserCallBack | Success | $value")
            }
        awaitClose { listener.remove() }
    }.flowOn(Dispatchers.IO)

    override suspend fun updateProfilePic(profilePic: Uri): Flow<State> = flow {
        try {

            val result =
                storage.reference.child("Profile Pictures/${auth.currentUser?.email}/profilePic")
                    .putFile(profilePic).await()

            val uri = result.metadata?.reference?.downloadUrl?.await()

            if (uri != null) {
                userCollection.document(auth.currentUser?.uid!!)
                    .update("profilePicUrl", uri.toString()).await()
                infoLog("updateProfilePic | Success | $uri")
                emit(State.SUCCESS)
            } else {
                throw Exception("UploadFailed")
            }

        } catch (e: Exception) {
            errorLog("updateProfilePic | Failed | ${e.message}")
            emit(State.FAILED)
        }
    }

    override suspend fun checkUserExist(email: String): Flow<Source> = flow {
        try {

            val result = userCollection.whereEqualTo("email", email).limit(1).get().await()
                .toObjects(UserDTO::class.java)

            when (result.size) {
                1 -> {
                    infoLog("checkUserExist | Success | ${result[0]}")
                    emit(Source.Success)
                }
                else -> {
                    throw Exception("No User Exists")
                }
            }

        } catch (e: Exception) {
            errorLog("checkUserExist | Failed | ${e.message}")
            emit(Source.Failure(e))
        }
    }
}


interface UserRepository {

    suspend fun createUser(user: UserDTO): Source

    suspend fun getUser(uid: String): Resource<UserDTO>

    suspend fun updateUser(name: String? = null, phoneNo: String? = null): Flow<Source>

    suspend fun userDetailsCallBack(): Flow<UserState>

    suspend fun updateProfilePic(profilePic: Uri): Flow<State>

    suspend fun checkUserExist(email: String): Flow<Source>

}