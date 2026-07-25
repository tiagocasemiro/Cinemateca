package com.cinemateca.domain.movies.model

import com.cinemateca.domain.trailers.model.ContentLanguage
import com.cinemateca.domain.trailers.model.VideoCategory

data class MovieVideoFilters(
    val categories: Set<VideoCategory> = emptySet(),
    val language: ContentLanguage? = null,
)
