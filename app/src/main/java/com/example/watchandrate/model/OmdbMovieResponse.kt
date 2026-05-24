package com.example.watchandrate.model

data class OmdbMovieResponse(
    val Title: String?,
    val Year: String?,
    val Genre: String?,
    val Director: String?,
    val Plot: String?,
    val imdbRating: String?,
    val Response: String?,
    val Error: String?
)