package at.stefanirndorfer.network.model

import com.squareup.moshi.Json

data class MoviesResponse(
    @Json(name = "page") val page: Int,
    @Json(name = "results") val movies: List<MovieItemResponse>,
    @Json(name = "total_pages") val totalPages: Int,
    @Json(name = "total_results") val totalMovieResults: Int
)

