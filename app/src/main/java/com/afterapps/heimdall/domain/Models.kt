package com.afterapps.heimdall.domain

data class Collection(
    val id: String,
    val name: String,
    val coverUrl: String,
    val shareUrl: String,
    val totalItemCount: Int,
    val createdTime: String,
    val updatedTime: String
)

data class Image(
    val id: String,
    val imageUrl: String,
    val thumbnailUrl: String,
    val collectionId: String
)

data class Result(
    val id: String,
    val imageUrl: String,
    val thumbnailUrl: String,
    val websiteUrl: String,
    val description: String
)