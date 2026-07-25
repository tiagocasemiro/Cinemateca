package com.cinemateca.networking.response

import com.cinemateca.domain.movies.model.Movie
import com.cinemateca.domain.movies.model.MovieSummary
import com.cinemateca.domain.trailers.model.MediaReference
import com.cinemateca.domain.trailers.model.PageMetadata
import com.cinemateca.domain.trailers.model.Trailer
import com.cinemateca.domain.trailers.model.TrailerPage
import com.cinemateca.repository.DomainMapperResponse
import com.google.gson.annotations.SerializedName

internal data class TrailerPageResponse(
    val items: List<TrailerResponse>,
    val metadata: PageMetadataResponse,
) : DomainMapperResponse<TrailerPage> {
    override fun mapToDomain() = TrailerPage(
        trailers = items.map(TrailerResponse::mapToDomain),
        metadata = metadata.mapToDomain(),
    )
}

internal data class PageMetadataResponse(
    val limit: Int?,
    val page: Int?,
    @SerializedName("total_pages") val totalPages: Int?,
    @SerializedName("total_count") val totalCount: Int?,
) : DomainMapperResponse<PageMetadata> {
    override fun mapToDomain() = PageMetadata(
        limit = limit ?: 0,
        page = page ?: 0,
        totalPages = totalPages ?: 0,
        totalCount = totalCount ?: 0,
    )
}

internal data class TrailerResponse(
    val id: String?,
    @SerializedName("youtube_video_id") val youtubeVideoId: String?,
    @SerializedName("youtube_channel_id") val youtubeChannelId: String?,
    @SerializedName("youtube_thumbnail") val youtubeThumbnail: String?,
    val title: String?,
    val url: String?,
    val thumbnail: String?,
    val language: String?,
    val categories: List<String>?,
    val genres: List<String>?,
    val published: String?,
    val views: Long?,
    val resource: MediaReferenceResponse?,
) : DomainMapperResponse<Trailer> {
    override fun mapToDomain() = Trailer(
        id = id.orEmpty(),
        youtubeVideoId = youtubeVideoId,
        youtubeChannelId = youtubeChannelId,
        youtubeThumbnail = youtubeThumbnail,
        title = title.orEmpty(),
        url = url,
        thumbnail = thumbnail,
        language = language,
        categories = categories.orEmpty(),
        genres = genres.orEmpty(),
        published = published,
        views = views,
        resource = resource?.mapToDomain(),
    )
}

internal data class MediaReferenceResponse(
    val type: String?,
    val path: String?,
    val id: String?,
    @SerializedName("imdb_id") val imdbId: String?,
    @SerializedName("tmdb_id") val tmdbId: Int?,
) : DomainMapperResponse<MediaReference> {
    override fun mapToDomain() = MediaReference(
        type = type,
        path = path,
        kinoCheckId = id,
        imdbId = imdbId,
        tmdbId = tmdbId,
    )
}

internal data class MovieResponse(
    val id: String?,
    @SerializedName("tmdb_id") val tmdbId: Int?,
    @SerializedName("imdb_id") val imdbId: String?,
    val language: String?,
    val title: String?,
    val url: String?,
    val trailer: TrailerResponse?,
    val videos: List<TrailerResponse>?,
    val recommendations: List<MovieSummaryResponse>?,
) : DomainMapperResponse<Movie> {
    override fun mapToDomain() = Movie(
        id = id.orEmpty(),
        tmdbId = tmdbId,
        imdbId = imdbId,
        language = language,
        title = title.orEmpty(),
        url = url,
        trailer = trailer?.mapToDomain(),
        videos = videos.orEmpty().map(TrailerResponse::mapToDomain),
        recommendations = recommendations.orEmpty().map(MovieSummaryResponse::mapToDomain),
    )
}

internal data class MovieSummaryResponse(
    val id: String?,
    @SerializedName("tmdb_id") val tmdbId: Int?,
    @SerializedName("imdb_id") val imdbId: String?,
    val language: String?,
    val title: String?,
    val url: String?,
) : DomainMapperResponse<MovieSummary> {
    override fun mapToDomain() = MovieSummary(
        id = id.orEmpty(),
        tmdbId = tmdbId,
        imdbId = imdbId,
        language = language,
        title = title.orEmpty(),
        url = url,
    )
}
