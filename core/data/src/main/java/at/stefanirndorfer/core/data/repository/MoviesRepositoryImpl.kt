package at.stefanirndorfer.core.data.repository

import at.stefanirndorfer.core.data.model.MoviesResponse
import at.stefanirndorfer.core.data.remote.MoviesDataSource
import at.stefanirndorfer.core.data.util.ResourceState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class MoviesRepositoryImpl @Inject constructor(
    private val moviesDataSource: MoviesDataSource
) : MoviesRepository {
    //todo add parameter for movie order
    override suspend fun getMostPopularMovies(): Flow<ResourceState<MoviesResponse>> {
        return flow {
            emit(ResourceState.Loading())
            val response = moviesDataSource.getMovies()
            if (response.isSuccessful && response.body() != null) {
                emit(ResourceState.Success(response.body()!!))
            } else {
                emit(ResourceState.Error("Error fetching movie data"))
            }
        }.catch { e ->
            emit(ResourceState.Error(e?.localizedMessage ?: "Error when fetching movie data"))
        }
    }
}