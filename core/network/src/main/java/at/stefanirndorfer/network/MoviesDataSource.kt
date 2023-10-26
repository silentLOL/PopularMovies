package at.stefanirndorfer.network

import at.stefanirndorfer.network.model.MoviesResponse
import retrofit2.Response

interface MoviesDataSource {
    suspend fun getMovies(): Response<MoviesResponse>
}