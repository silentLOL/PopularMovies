package at.stefanirndorfer.network

import at.stefanirndorfer.network.model.MoviesResponse
import retrofit2.Response

interface RemoteMoviesDataSource {
    suspend fun getMovies(page: Int): Response<MoviesResponse>
}