package at.stefanirndorfer.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import at.stefanirndorfer.designsystem.theme.PopularMoviesTheme

@Composable
fun PMLoadingSpinner(
    modifier: Modifier = Modifier.width(64.dp)
) {
    PopularMoviesTheme {
        Row(
            Modifier
                .fillMaxSize()
                .background(Color.White),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {

            CircularProgressIndicator(
                modifier = modifier,
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.inversePrimary,
            )
        }
    }
}

@Preview
@Composable
fun previewLoadingSpinner() {
    PMLoadingSpinner()
}