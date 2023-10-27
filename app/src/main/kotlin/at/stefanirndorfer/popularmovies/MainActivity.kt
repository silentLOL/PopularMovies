package at.stefanirndorfer.popularmovies

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import at.stefanirndorfer.core.data.util.NetworkMonitor
import at.stefanirndorfer.designsystem.theme.PopularMoviesTheme
import at.stefanirndorfer.popularmovies.ui.PMApp
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var networkMonitor: NetworkMonitor
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PopularMoviesTheme {
                PMApp(networkMonitor = networkMonitor,
                    windowSizeClass = calculateWindowSizeClass(activity = this))
            }
        }
    }
}


