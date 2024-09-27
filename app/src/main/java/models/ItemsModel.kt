package models

import java.io.Serializable

data class ItemsModel(
    val title: String,
    val location: String,
    val imageUrl: Int

) : Serializable