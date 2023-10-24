package at.stefanirndorfer.core.data.repository

import at.stefanirndorfer.core.data.model.MoviesResponse
import at.stefanirndorfer.core.data.util.ResourceState
import kotlinx.coroutines.flow.Flow

interface MoviesRepository {
    suspend fun getMostPopularMovies(): Flow<ResourceState<MoviesResponse>>
}