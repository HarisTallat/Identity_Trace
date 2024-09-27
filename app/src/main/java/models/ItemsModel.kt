package models

import java.io.Serializable

data class ItemsModel(
    val title: String,
    val description: String,
    val picUrl: ArrayList<String>,
    val size: ArrayList<String>,
    val price: Double,
    val rating: Double,
    val numberInCart: Int,
    val categoryId: Int,
    val sellerName: String
) : Serializable