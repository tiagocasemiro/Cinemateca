package com.cinemateca.local.adapter

import com.cinemateca.domain.movies.repository.WatchlistMovieRepository
import com.cinemateca.local.database.WatchlistMovieDao
import com.cinemateca.local.database.WatchlistMovieEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

class WatchlistMovieLocalImpl(
    private val dao: WatchlistMovieDao,
) : WatchlistMovieRepository.Local {
    override fun observeMovieIds(): Flow<Set<String>> {
        return dao.observeMovieIds()
            .map(List<String>::toSet)
            .distinctUntilChanged()
    }

    override suspend fun setSelected(
        movieId: String,
        isSelected: Boolean,
    ) {
        if (isSelected) {
            dao.insert(WatchlistMovieEntity(movieId = movieId))
        } else {
            dao.delete(movieId)
        }
    }
}
