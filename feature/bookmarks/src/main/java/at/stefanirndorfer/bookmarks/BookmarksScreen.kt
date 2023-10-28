package at.stefanirndorfer.bookmarks

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import at.stefanirndorfer.designsystem.theme.LocalBackgroundTheme
import at.stefanirndorfer.designsystem.theme.PopularMoviesTheme
import at.stefanirndorfer.designsystem.theme.Typography

@Composable
internal fun BookmarksRoute(
    modifier: Modifier = Modifier,
    viewModel: BookmarksViewModel = hiltViewModel()
) {
    //val bookmarkedMovies by viewModel.bookmarks.collectAsStateWithLifecycle()
    //BookmarksScreen(bookmarkedMovies = bookmarkedMovies, modifier = modifier)
    BookmarksScreen()
}

@Composable
fun BookmarksScreen() {
    PopularMoviesTheme {
        Column(
            modifier = Modifier
                .padding(30.dp)
                .fillMaxWidth()
                .wrapContentSize(Alignment.Center)
                .background(LocalBackgroundTheme.current.color)
                .clip(shape = RoundedCornerShape(16.dp)),
        ) {
            Box(
                modifier = Modifier
                    .border(width = 4.dp, color = Color.Gray, shape = RoundedCornerShape(16.dp)),
            ) {
                Text(
                    text = "Not much yet",
                    modifier = Modifier.padding(16.dp),
                    textAlign = TextAlign.Center,
                    style = Typography.titleMedium
                )
            }
        }
    }
}
