package at.stefanirndorfer.popularmovies.ui

import android.util.Log
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import at.stefanirndorfer.bookmarks.navigation.bookmarksNavigationRoute
import at.stefanirndorfer.bookmarks.navigation.navigateToBookmarks
import at.stefanirndorfer.core.data.util.NetworkMonitor
import at.stefanirndorfer.core.data.util.NetworkStatus
import at.stefanirndorfer.popularmovies.navigation.TopLevelDestination
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import movieListNavigationRoute
import navigateToMovieList

private const val TAG: String = "PMAppState"

@Composable
fun rememberPMAppState(
    windowSizeClass: WindowSizeClass,
    networkMonitor: NetworkMonitor,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    navController: NavHostController = rememberNavController()
): PMAppState {
    return remember(
        navController,
        coroutineScope,
        windowSizeClass,
        networkMonitor
    ) {
        PMAppState(
            navController,
            coroutineScope,
            windowSizeClass,
            networkMonitor
        )
    }
}

@Stable
class PMAppState(
    val navController: NavHostController,
    val coroutineScope: CoroutineScope,
    val windowSizeClass: WindowSizeClass,
    networkMonitor: NetworkMonitor
) {
    val currentDestination: NavDestination?
        @Composable get() = navController
            .currentBackStackEntryAsState().value?.destination

    val currentTopLevelDestination: TopLevelDestination?
        @Composable get() = when (currentDestination?.route) {
            movieListNavigationRoute -> TopLevelDestination.MOVIE_LIST
            bookmarksNavigationRoute -> TopLevelDestination.BOOKMARKS
            else -> null
        }

    val shouldShowBottomBar: Boolean
        get() = windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact

    // e.g. fold-ables
    val shouldShowNavRail: Boolean
        get() = !shouldShowBottomBar

    val isOffline: StateFlow<Boolean> = networkMonitor.isAvailable
        .map { available ->
            when (available) {
                is NetworkStatus.Available -> {
                    Log.d(TAG, "networkMonitor: offline is false")
                    false
                }

                else -> {
                    Log.d(TAG, "networkMonitor: offline is true")
                    true
                }
            }
        }
        .stateIn(
            scope = coroutineScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = true
        )

    /**
     * Map of top level destinations to be used in the TopBar, BottomBar and NavRail. The key is the
     * route.
     */
    val topLevelDestinations: List<TopLevelDestination> = TopLevelDestination.values().asList()

    /**
     * UI logic for navigating to a top level destination in the app. Top level destinations have
     * only one copy of the destination of the back stack, and save and restore state whenever you
     * navigate to and from it.
     *
     * @param topLevelDestination: The destination the app needs to navigate to.
     */
    fun navigateToTopLevelDestination(topLevelDestination: TopLevelDestination) {
        val topLevelNavOptions = navOptions {
            // Pop up to the start destination of the graph to
            // avoid building up a large stack of destinations
            // on the back stack as users select items
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            // Avoid multiple copies of the same destination when
            // reselecting the same item
            launchSingleTop = true
            // Restore state when reselecting a previously selected item
            restoreState = true
        }

        when (topLevelDestination) {
            TopLevelDestination.MOVIE_LIST -> navController.navigateToMovieList(topLevelNavOptions)
            TopLevelDestination.BOOKMARKS -> navController.navigateToBookmarks(topLevelNavOptions)
        }

    }
}


