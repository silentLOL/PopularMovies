package at.stefanirndorfer.popularmovies.navigation

import androidx.compose.ui.graphics.vector.ImageVector
import at.stefanirndorfer.designsystem.icon.PMIcons
import at.stefanirndorfer.popularmovies.R
import at.stefanirndorfer.bookmarks.R as bookmarksR
import at.stefanirndorfer.feature.movielist.R as movieListR


/**
 * Type for the top level destinations in the application.
 */
enum class TopLevelDestination(
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val iconTextId: Int,
    val titleTextId: Int
) {
    MOVIE_LIST(
        selectedIcon = PMIcons.MovieIcon,
        unselectedIcon = PMIcons.MovieIconBorder,
        iconTextId = movieListR.string.movie_list,
        titleTextId = R.string.app_name
    ),
    BOOKMARKS(
        selectedIcon = PMIcons.Bookmarks,
        unselectedIcon = PMIcons.BookmarksBorder,
        iconTextId = bookmarksR.string.bookmarks,
        titleTextId = bookmarksR.string.bookmarks_top_bar_title
    )
}
