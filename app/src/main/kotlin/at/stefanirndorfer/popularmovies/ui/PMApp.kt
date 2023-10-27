package at.stefanirndorfer.popularmovies.ui

import PMBackground
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import at.stefanirndorfer.core.data.util.NetworkMonitor
import at.stefanirndorfer.popularmovies.R
import at.stefanirndorfer.popularmovies.navigation.PMNavHost

@Composable
fun PMApp(
    networkMonitor: NetworkMonitor,
    appState: PMAppState = rememberPMAppState(
        networkMonitor = networkMonitor
    )
) {
    PMBackground {
        val snackbarHostState = remember { SnackbarHostState() }
        val isOffline by appState.isOffline.collectAsStateWithLifecycle()

        // If user is not connected to the internet show a snack bar to inform them.
        val notConnectedMessage = stringResource(R.string.not_connected)
        LaunchedEffect(isOffline) {
            if (isOffline) {
                snackbarHostState.showSnackbar(
                    message = notConnectedMessage,
                    duration = SnackbarDuration.Indefinite,
                )
            }
        }

        PMNavHost(appState = appState, onShowSnackbar = { message, action ->
            snackbarHostState.showSnackbar(
                message = message,
                actionLabel = action,
                duration = SnackbarDuration.Short,
            ) == SnackbarResult.ActionPerformed
        })


    }

}