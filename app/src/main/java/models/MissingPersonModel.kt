package models

import java.io.Serializable

data class MissingPersonModel(
    val title: String,
    val location: String,
    val imageUrl: Int

) : Serializable