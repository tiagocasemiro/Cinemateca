package com.cinemateca.domain.trailers.model

data class TrailerFilters(
    val genres: Set<TrailerGenre> = emptySet(),
    val categories: Set<VideoCategory> = emptySet(),
    val language: ContentLanguage? = null,
    val page: Int = DEFAULT_PAGE,
    val limit: Int = DEFAULT_LIMIT,
) {
    init {
        require(page >= 1) { "page must be at least 1" }
        require(limit in 1..MAX_LIMIT) { "limit must be between 1 and $MAX_LIMIT" }
    }

    private companion object {
        const val DEFAULT_PAGE = 1
        const val DEFAULT_LIMIT = 25
        const val MAX_LIMIT = 100
    }
}
