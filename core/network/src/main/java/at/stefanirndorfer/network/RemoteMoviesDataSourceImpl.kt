package at.stefanirndorfer.network

import at.stefanirndorfer.network.model.MoviesOrder
import at.stefanirndorfer.network.retrofit.RemoteMovieApi
import at.stefanirndorfer.network.util.ApiKey
import javax.inject.Inject

class RemoteMoviesDataSourceImpl @Inject constructor(
    private val moviesRemoteService: RemoteMovieApi
) : MoviesDataSource {
    override suspend fun getMovies() = moviesRemoteService.getMovies(
        sortBy = MoviesOrder.POPULAR.order,
        apiKey = ApiKey.APIKEY,
        1
    )
}