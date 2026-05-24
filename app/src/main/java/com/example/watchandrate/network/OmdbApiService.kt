package com.example.watchandrate.network

import com.example.watchandrate.model.OmdbMovieResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface OmdbApiService {
    @GET("/")
    suspend fun searchMovie(
        @Query("apikey") apiKey: String,
        @Query("t") title: String
    ): OmdbMovieResponse
}