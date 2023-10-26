package at.stefanirndorfer.core.data.model

data class Movies(
    val page: Int,
    val movies: List<MovieItem>,
    val totalPages: Int, val totalMovieResults: Int
)
