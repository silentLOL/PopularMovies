package at.stefanirndorfer.core.data.remote

import at.stefanirndorfer.core.data.model.MoviesResponse
import retrofit2.Response

interface MoviesDataSource {
    suspend fun getMovies(): Response<MoviesResponse>
}