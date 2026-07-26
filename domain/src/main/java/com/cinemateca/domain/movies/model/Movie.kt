package com.cinemateca.domain.movies.model

import com.cinemateca.domain.trailers.model.Trailer

data class Movie(
    val id: String,
    val tmdbId: Int?,
    val imdbId: String?,
    val language: String?,
    val title: String,
    val url: String?,
    val trailer: Trailer?,
    val videos: List<Trailer>,
    val recommendations: List<MovieSummary>,
)

data class MovieSummary(
    val id: String,
    val tmdbId: Int?,
    val imdbId: String?,
    val language: String?,
    val title: String,
    val url: String?,
)

enum class MediaResourceType {
    Movie,
    Show,
    ;

    companion object {
        fun fromApiValue(value: String?): MediaResourceType {
            return if (value.equals("show", ignoreCase = true)) {
                Show
            } else {
                Movie
            }
        }
    }
}
