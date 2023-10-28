package at.stefanirndorfer.bookmarks.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import at.stefanirndorfer.bookmarks.BookmarksRoute

const val bookmarksNavigationRoute = "bookmarks_route" // no arguments

fun NavController.navigateToBookmarks(navOptions: NavOptions? = null) {
    this.navigate(bookmarksNavigationRoute, navOptions)
}

fun NavGraphBuilder.bookmarksScreen() {
    composable(
        route = bookmarksNavigationRoute
    ) {
        BookmarksRoute()
    }
}