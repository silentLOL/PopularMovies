package at.stefanirndorfer.core.data.model

import com.squareup.moshi.Json

data class MoviesResponse(
    @Json(name = "page") val page: Int,
    @Json(name = "results") val movies: List<MovieItem>,
    @Json(name = "total_pages") val totalPages: Int,
    @Json(name = "total_results") val totalMovieResults: Int
)

