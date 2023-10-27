package at.stefanirndorfer.popularmovies.ui

import androidx.compose.runtime.Composable
import at.stefanirndorfer.core.data.util.NetworkMonitor
import kotlinx.coroutines.CoroutineScope

@Composable
fun PMApp(
    coroutineScope: CoroutineScope,
    networkMonitor: NetworkMonitor
) {
    Navigation()
//    val isOffline = networkMonitor.isAvailable
//        .stateIn(
//            scope = coroutineScope,
//            started = SharingStarted.WhileSubscribed(5_000),
//            initialValue = NetworkStatus.Unavailable,
//        )
}