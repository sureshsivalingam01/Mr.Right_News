package com.mrright.news.db.api.responses


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep
import com.mrright.news.models.Source

@Keep
data class SourceDTO(
    @SerializedName("id")
    val id: Any? = null,
    @SerializedName("name")
    val name: String? = null
) {

    fun toSource(): Source {
        return Source(id, name)
    }

}