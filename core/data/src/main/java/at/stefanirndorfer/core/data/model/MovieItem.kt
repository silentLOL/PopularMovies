package at.stefanirndorfer.core.data.model

import com.squareup.moshi.Json

data class MovieItem(
    val id: Int,
    @Json(name = "vote_count") val voteCount: Int,
    @Json(name = "vote_average") val voteAverage: Double,
    val popularity: Double,
    @Json(name = "video") val hasVideo: Boolean,
    @Json(name = "adult") val isAdult: Boolean,
    val title: String,
    val overview: String,
    @Json(name = "release_date") val releaseDate: String,
    @Json(name = "poster_path") val posterPath: String,
    @Json(name = "backdrop_path") val backdropPath: String,
    @Json(name = "original_language") val originalLanguage: String,
    @Json(name = "original_title") val originalTitle: String,
    @Json(name = "genre_ids") val genreIds: List<Int>,
)
