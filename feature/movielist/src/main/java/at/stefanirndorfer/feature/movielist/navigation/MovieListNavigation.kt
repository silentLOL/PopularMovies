import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import at.stefanirndorfer.feature.movielist.MovieListRoute

const val movieListNavigationRoute = "movie_list_route" // no arguments

fun NavController.navigateToMovieList(navOptions: NavOptions? = null) {
    this.navigate(movieListNavigationRoute, navOptions)
}

fun NavGraphBuilder.movieListScreen() {
    composable(
        route = movieListNavigationRoute
    ) {
        MovieListRoute()
    }
}