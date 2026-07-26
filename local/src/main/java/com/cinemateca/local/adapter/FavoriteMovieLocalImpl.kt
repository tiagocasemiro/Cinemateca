package com.cinemateca.local.adapter

import com.cinemateca.domain.movies.repository.FavoriteMovieRepository
import com.cinemateca.local.database.FavoriteMovieDao
import com.cinemateca.local.database.FavoriteMovieEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

class FavoriteMovieLocalImpl(
    private val dao: FavoriteMovieDao,
) : FavoriteMovieRepository.Local {
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
            dao.insert(FavoriteMovieEntity(movieId = movieId))
        } else {
            dao.delete(movieId)
        }
    }
}
