package at.stefanirndorfer.core.data.model

enum class MoviesOrder(val order: String) {
    POPULAR("popular"),
    TOP_RATED("top_rated"),
    FAVORITES("favorites")
}