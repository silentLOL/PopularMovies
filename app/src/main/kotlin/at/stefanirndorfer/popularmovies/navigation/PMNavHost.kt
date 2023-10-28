package at.stefanirndorfer.popularmovies.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import at.stefanirndorfer.bookmarks.navigation.bookmarksScreen
import at.stefanirndorfer.popularmovies.ui.PMAppState
import movieListNavigationRoute
import movieListScreen

@Composable
fun PMNavHost(
    appState: PMAppState,
    onShowSnackbar: suspend (String, String?) -> Boolean,
    modifier: Modifier = Modifier,
    startDestination: String = movieListNavigationRoute
) {
    val navController = appState.navController
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        movieListScreen()
        bookmarksScreen()
    }
}