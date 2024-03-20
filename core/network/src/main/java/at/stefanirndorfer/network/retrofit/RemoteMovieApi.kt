package at.stefanirndorfer.network.retrofit

import at.stefanirndorfer.network.model.MoviesResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RemoteMovieApi {

    @GET("/3/movie/{sort_by}")
    suspend fun getMovies(
        @Path("sort_by") sortBy: String?,
        @Query("api_key") apiKey: String?,
        @Query("page") page: Int?
    ): Response<MoviesResponse>

}