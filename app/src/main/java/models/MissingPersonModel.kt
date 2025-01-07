package models

import java.io.Serializable

data class MissingPersonModel(
    val name: String,
    val location: String,
    val imageUrl: String

) : Serializable