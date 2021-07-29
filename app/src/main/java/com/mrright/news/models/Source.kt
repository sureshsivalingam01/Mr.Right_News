package com.mrright.news.models

import com.mrright.news.db.api.responses.SourceDTO
import java.io.Serializable

data class Source(
    val id: Any? = null,
    val name: String? = "",
) : Serializable {

    fun toSourceDTO(): SourceDTO {
        return SourceDTO(id, name)
    }

}
