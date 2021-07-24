package com.mrright.news.models

import com.mrright.news.db.api.responses.SourceDTO

data class Source(
    val id: Any? = null,
    val name: String? = "",
){

    fun toSourceDTO(): SourceDTO {
        return SourceDTO(id, name)
    }

}
