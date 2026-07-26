package com.cinemateca.domain.movies.repository

import kotlinx.coroutines.flow.Flow

interface FavoriteMovieRepository {
    interface Local {
        fun observeMovieIds(): Flow<Set<String>>

        suspend fun setSelected(
            movieId: String,
            isSelected: Boolean,
        )
    }
}
