package at.stefanirndorfer.core.data.model

import at.stefanirndorfer.network.model.MovieItemResponse

data class MovieItem(
    val id: Int,
    val voteCount: Int,
    val voteAverage: Double,
    val popularity: Double,
    val hasVideo: Boolean,
    val isAdult: Boolean,
    val title: String,
    val overview: String,
    val releaseDate: String,
    val posterPath: String,
    val backdropPath: String,
    val originalLanguage: String,
    val originalTitle: String,
    val genreIds: List<Int>,
) {
    constructor(movieItemResponse: MovieItemResponse) : this(
        id = movieItemResponse.id,
        voteCount = movieItemResponse.voteCount,
        voteAverage = movieItemResponse.voteAverage,
        popularity = movieItemResponse.popularity,
        hasVideo = movieItemResponse.hasVideo,
        isAdult = movieItemResponse.isAdult,
        title = movieItemResponse.title,
        overview = movieItemResponse.overview,
        releaseDate = movieItemResponse.releaseDate,
        posterPath = movieItemResponse.posterPath,
        backdropPath = movieItemResponse.backdropPath,
        originalLanguage = movieItemResponse.originalLanguage,
        originalTitle = movieItemResponse.originalTitle,
        genreIds = movieItemResponse.genreIds
    )

}
fun List<MovieItemResponse>.mapToMovieItem(): List<MovieItem> {
    return map { MovieItem(it) }
}
