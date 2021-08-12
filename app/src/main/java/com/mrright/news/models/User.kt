package com.mrright.news.models

import com.mrright.news.db.firestore.dto.UserDTO

data class User(
	val uid : String = "",
	val email : String = "",
	val name : String = "",
	val phoneNo : String = "",
	val profilePicUrl : String = "",
	val token : String = "",
) {
	fun toUserDTO() : UserDTO {
		return UserDTO(uid, email, name, phoneNo, profilePicUrl, token)
	}
}
