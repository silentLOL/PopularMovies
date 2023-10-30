package at.stefanirndorfer.core.data.util

import at.stefanirndorfer.database.model.MovieEntity
import at.stefanirndorfer.network.model.MovieItemResponse

fun List<MovieItemResponse>.mapMovieItemResponseToMovieEntity(page: Int): List<MovieEntity> {
    return map { movieItemResponse ->
        MovieEntity(
            id = movieItemResponse.id,
            page = page,
            voteCount = movieItemResponse.voteCount,
            voteAverage = movieItemResponse.voteAverage,
            popularity = movieItemResponse.popularity,
            hasVideo = movieItemResponse.hasVideo,
            isAdult = movieItemResponse.isAdult,
            title = movieItemResponse.title,
            originalTitle = movieItemResponse.originalTitle,
            originalLanguage = movieItemResponse.originalLanguage,
            overview = movieItemResponse.overview,
            releaseDate = movieItemResponse.releaseDate,
            posterPath = movieItemResponse.posterPath,
            backdropPath = movieItemResponse.backdropPath
        )
    }
}