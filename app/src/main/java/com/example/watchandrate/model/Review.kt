package com.example.watchandrate.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reviews")
data class Review(

    @PrimaryKey
    val id: String,

    val userId: String,
    val movieId: String,
    val movieTitle: String,
    val text: String,
    val imageUrl: String?,
    val localImagePath: String?,
    val userPhotoUrl: String,
    val username: String,
    val likedBy: List<String> = emptyList(),
    val likesCount: Int = 0,
    val createdAt: Long,
    val updatedAt: Long
)