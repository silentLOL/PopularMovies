package at.stefanirndorfer.core.data.remote

import at.stefanirndorfer.core.data.model.MoviesOrder
import javax.inject.Inject

class RemoteMoviesDataSourceImpl @Inject constructor(
    private val moviesRemoteService: RemoteMoviesApi
) : MoviesDataSource {
    override suspend fun getMovies() = moviesRemoteService.getMovies(
        sortBy = MoviesOrder.POPULAR.order,
        api_key = ApiKey.API_KEY,
        1
    )
}