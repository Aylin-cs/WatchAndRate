package com.example.watchandrate.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "comments")
data class Comment(
    @PrimaryKey
    val id: String = "",
    val reviewId: String = "",
    val userId: String = "",
    val text: String = "",
    val username: String = "",
    val createdAt: Long = 0L
)