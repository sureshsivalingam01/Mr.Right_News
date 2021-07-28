package com.mrright.news.db.firestore.dto

import com.mrright.news.models.User

data class UserDTO(
    val uid: String = "",
    val email: String = "",
    val name: String = "",
    val phoneNo: String = "",
    val profilePicUrl: String = "",
    val token: String = "",
) {
    fun toUser(): User {
        return User(uid, email, name, phoneNo, profilePicUrl, token)
    }
}
