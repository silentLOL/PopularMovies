package at.stefanirndorfer.popularmovies

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import at.stefanirndorfer.core.data.util.NetworkMonitor
import at.stefanirndorfer.designsystem.theme.PopularMoviesTheme
import at.stefanirndorfer.popularmovies.ui.PMApp
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var networkMonitor: NetworkMonitor
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PopularMoviesTheme {
                PMApp(networkMonitor)
            }
        }
    }
}


