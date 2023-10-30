package at.stefanirndorfer.core.data.model

import at.stefanirndorfer.database.model.MovieEntity
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
    val genreIds: List<Int>? = emptyList(),
    val page: Int
) {
    constructor(movieItemResponse: MovieItemResponse, page: Int) : this(
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
        genreIds = movieItemResponse.genreIds,
        page = page
    )

    constructor(movieEntity: MovieEntity) : this(
        id = movieEntity.id,
        voteCount = movieEntity.voteCount,
        voteAverage = movieEntity.voteAverage,
        popularity = movieEntity.popularity,
        hasVideo = movieEntity.hasVideo,
        isAdult = movieEntity.isAdult,
        title = movieEntity.title,
        overview = movieEntity.overview,
        releaseDate = movieEntity.releaseDate,
        posterPath = movieEntity.posterPath,
        backdropPath = movieEntity.backdropPath,
        originalLanguage = movieEntity.originalLanguage,
        originalTitle = movieEntity.originalTitle,
        page = movieEntity.page
    )
}

fun List<MovieItemResponse>.mapMovieItemResponseToMovieItem(page: Int): List<MovieItem> {
    return map { MovieItem(it, page) }
}

fun List<MovieEntity>.mapMovieEntityToMovieItem(): List<MovieItem> {
    return map { MovieItem(it) }
}
