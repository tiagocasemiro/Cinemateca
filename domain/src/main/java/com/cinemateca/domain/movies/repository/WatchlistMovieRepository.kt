package com.cinemateca.domain.movies.repository

import kotlinx.coroutines.flow.Flow

interface WatchlistMovieRepository {
    interface Local {
        fun observeMovieIds(): Flow<Set<String>>

        suspend fun setSelected(
            movieId: String,
            isSelected: Boolean,
        )
    }
}
