package com.mrright.news.db.firestore.repositories

import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.mrright.news.db.api.Resource
import com.mrright.news.utils.errorLog
import com.mrright.news.utils.infoLog
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


class AuthRepoImpl @Inject constructor(
    private val auth: FirebaseAuth,
) : AuthRepository {

    override suspend fun getTokenId(task: Task<GoogleSignInAccount>): Resource<GoogleSignInAccount> {
        return try {
            val result = task.await()
            infoLog("getTokenId | Success | ${result.account}")
            return Resource.Success(result)
        } catch (e: Exception) {
            errorLog("getTokenId | Exception", e)
            Resource.Exception(e)
        }
    }

    override suspend fun signIn(credential: AuthCredential): Resource<AuthResult> {
        return try {
            val result = auth.signInWithCredential(credential)
                .await()
            result?.let {
                infoLog("signIn | Success | ${result.user}")
                Resource.Success(result)
            } ?: Resource.Error("No Response")
        } catch (e: Exception) {
            errorLog("signIn | Exception", e)
            Resource.Exception(e)
        }
    }
}


interface AuthRepository {

    suspend fun getTokenId(task: Task<GoogleSignInAccount>): Resource<GoogleSignInAccount>

    suspend fun signIn(credential: AuthCredential): Resource<AuthResult>

}