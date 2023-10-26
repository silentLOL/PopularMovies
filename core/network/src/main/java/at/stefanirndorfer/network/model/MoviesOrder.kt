package at.stefanirndorfer.network.model

enum class MoviesOrder(val order: String) {
    POPULAR("popular"),
    TOP_RATED("top_rated"),
    FAVORITES("favorites")
}