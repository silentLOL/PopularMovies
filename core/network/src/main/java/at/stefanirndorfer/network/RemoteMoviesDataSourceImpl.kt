package at.stefanirndorfer.network

import at.stefanirndorfer.network.model.MoviesOrder
import at.stefanirndorfer.network.retrofit.RemoteMovieApi
import at.stefanirndorfer.network.util.ApiKey
import javax.inject.Inject

class RemoteMoviesDataSourceImpl @Inject constructor(
    private val moviesRemoteService: RemoteMovieApi
) : RemoteMoviesDataSource {
    override suspend fun getMovies(page: Int) = moviesRemoteService.getMovies(
        sortBy = MoviesOrder.POPULAR.order,
        api_key = ApiKey.API_KEY,
        page = page
    )
}