package com.cinemateca.networking.gateway

import com.cinemateca.networking.response.MovieResponse
import com.cinemateca.networking.response.TrailerPageResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

internal interface KinoCheckGateway {
    @GET("trailers/trending")
    suspend fun getTrending(
        @Query("genres") genres: String?,
        @Query("categories") categories: String?,
        @Query("language") language: String?,
        @Query("page") page: Int,
        @Query("limit") limit: Int,
    ): Response<TrailerPageResponse>

    @GET("trailers/latest")
    suspend fun getLatest(
        @Query("genres") genres: String?,
        @Query("categories") categories: String?,
        @Query("language") language: String?,
        @Query("page") page: Int,
        @Query("limit") limit: Int,
    ): Response<TrailerPageResponse>

    @GET("trailers")
    suspend fun getTrailers(
        @Query("genres") genres: String?,
        @Query("categories") categories: String?,
        @Query("language") language: String?,
        @Query("page") page: Int,
        @Query("limit") limit: Int,
    ): Response<TrailerPageResponse>

    @GET("movies")
    suspend fun getMovie(
        @Query("id") id: String?,
        @Query("tmdb_id") tmdbId: Int?,
        @Query("imdb_id") imdbId: String?,
        @Query("categories") categories: String?,
        @Query("language") language: String?,
    ): Response<MovieResponse>
}
