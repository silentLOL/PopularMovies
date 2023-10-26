package at.stefanirndorfer.testing.repository

import at.stefanirndorfer.core.data.model.Movies
import at.stefanirndorfer.core.data.repository.MoviesRepository
import at.stefanirndorfer.core.data.util.ResourceState
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow

class TestMoviesRepository : MoviesRepository {

    private val moviesResponseFlow: MutableSharedFlow<ResourceState<Movies>> = MutableSharedFlow(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    override suspend fun getMostPopularMovies(): Flow<ResourceState<Movies>> {
        return moviesResponseFlow
    }

    fun sendMostPopularMoviesLoading() {
        moviesResponseFlow.tryEmit(ResourceState.Loading())
    }

    fun sendMostPopularMoviesSuccessfully(moviesResponse: Movies) {
        moviesResponseFlow.tryEmit(ResourceState.Success(moviesResponse))
    }

    fun sendMostPopularMoviesError() {
        moviesResponseFlow.tryEmit(ResourceState.Error("testing errors"))
    }
}