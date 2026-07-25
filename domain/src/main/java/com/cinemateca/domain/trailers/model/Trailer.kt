package com.cinemateca.domain.trailers.model

data class Trailer(
    val id: String,
    val youtubeVideoId: String?,
    val youtubeChannelId: String?,
    val youtubeThumbnail: String?,
    val title: String,
    val url: String?,
    val thumbnail: String?,
    val language: String?,
    val categories: List<String>,
    val genres: List<String>,
    val published: String?,
    val views: Long?,
    val resource: MediaReference?,
)

data class MediaReference(
    val type: String?,
    val path: String?,
    val kinoCheckId: String?,
    val imdbId: String?,
    val tmdbId: Int?,
)

data class TrailerPage(
    val trailers: List<Trailer>,
    val metadata: PageMetadata,
)

data class PageMetadata(
    val limit: Int,
    val page: Int,
    val totalPages: Int,
    val totalCount: Int,
)
