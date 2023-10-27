package at.stefanirndorfer.feature.movielist

import android.util.Log
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.itemsIndexed
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import at.stefanirndorfer.core.data.model.MovieItem
import at.stefanirndorfer.core.data.model.Movies
import at.stefanirndorfer.core.data.util.ResourceState
import at.stefanirndorfer.designsystem.components.DynamicAsyncImage
import at.stefanirndorfer.designsystem.theme.PopularMoviesTheme
import at.stefanirndorfer.feature.movielist.util.Constants


const val TAG = "MovieListScreen"

@Composable
fun MovieListScreen(
    navController: NavController,
    viewModel: MovieListViewModel = hiltViewModel()
) {
    val movies by viewModel.movies.collectAsStateWithLifecycle()


    when (movies) {
        is ResourceState.Loading -> {
            Log.d(TAG, "Loading")
        }

        is ResourceState.Success -> {
            Log.d(TAG, "Success")
            LazyVerticalStaggeredGridMovies((movies as ResourceState.Success<Movies>).data.movies)
        }

        is ResourceState.Error -> {
            Log.d(TAG, "Error: ${(movies as ResourceState.Error<Movies>).error}")
        }
    }
}

@Composable
private fun LazyVerticalStaggeredGridMovies(movieList: List<MovieItem>) {
    Surface(
        modifier = Modifier
            .fillMaxSize(),
        color = Color.Gray
    ) {
        LazyVerticalStaggeredGrid(
            state = rememberLazyStaggeredGridState(),
            columns = StaggeredGridCells.Fixed(1),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(8.dp), // outside padding
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            itemsIndexed(
                items = movieList,
                key = { _: Int, item: MovieItem ->
                    item.id
                }
            ) { _, item ->
                MovieItemCard(movieItem = item)
            }
        }
    }
}

@Composable
private fun MovieItemCard(movieItem: MovieItem) {

    val cornerRadius = 12.dp
    val defaultElevation = 2.dp
    val outerPadding = 4.dp
    PopularMoviesTheme {
        Card(
            shape = RoundedCornerShape(cornerRadius),
            modifier = Modifier
                .padding(horizontal = outerPadding, vertical = outerPadding)
                .fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = defaultElevation)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = Color.White)
            ) {
                Column(
                    modifier = Modifier
                        .padding(outerPadding)
                        .width(120.dp)
                ) {
                    val thumbnailPath: String = Constants.BASIC_POSTER_PATH + Constants.THUMB_NAIL_WIDTH + movieItem.posterPath
                    DynamicAsyncImage(
                        imageUrl = thumbnailPath,
                        contentDescription = "poster of ${movieItem.title}",
                        modifier = Modifier
                            .wrapContentSize()
                            .scale(1.0f),
                        imageModifier = Modifier
                            .padding(8.dp)
                            .size(100.dp)
                            .clip(RoundedCornerShape(16.dp))
                    )
                }
                Column(
                    modifier = Modifier
                        .padding(all = outerPadding),
                ) {
                    Text(
                        text = movieItem.title,
                        style = MaterialTheme.typography.titleMedium,
                    )
                    if (movieItem.originalTitle != movieItem.title) {
                        Text(
                            text = movieItem.originalTitle
                        )
                    }
                    Text(
                        text = movieItem.releaseDate.substring(0, 4),
                        style = MaterialTheme.typography.titleSmall
                    )
                    Spacer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(12.dp)
                    )
                    Row {
                        Text(text = stringResource(R.string.popularity_label), style = MaterialTheme.typography.labelSmall)
                        Text(text = movieItem.popularity.toString(), style = MaterialTheme.typography.labelSmall)
                    }
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(text = stringResource(R.string.vote_average_label), style = MaterialTheme.typography.labelSmall)
                        Text(
                            text = movieItem.voteAverage.toString(),
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            Row(
                modifier = Modifier
                    .padding(all = 12.dp)
                    .fillMaxWidth()
            ) {
                // storing expanded state
                val showMore = remember { mutableStateOf(false) }
                Column(modifier = Modifier
                    .animateContentSize(animationSpec = tween(300))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { showMore.value = !showMore.value })
                {
                    if (showMore.value) {
                        Text(
                            text = movieItem.overview,
                            style = MaterialTheme.typography.bodySmall
                        )
                    } else {
                        Text(text = movieItem.overview, style = MaterialTheme.typography.bodySmall, maxLines = 2, overflow = TextOverflow.Ellipsis)
                    }

                }
            }
        }
    }
}

@Preview
@Composable
fun PreviewMovieItemCard() {
    val movieListItem = MovieItem(
        isAdult = false,
        backdropPath = "/rMvPXy8PUjj1o8o1pzgQbdNCsvj.jpg",
        genreIds = listOf(28, 12, 53),
        id = 299054,
        originalLanguage = "en",
        originalTitle = "Expend4bles",
        overview = "Armed with every weapon they can get their hands on and the skills to use them, The Expendables are the world’s last line of defense and the team that gets called when all other options are off the table. But new team members with new styles and tactics are going to give “new blood” a whole new meaning.",
        popularity = 3741.062,
        posterPath = "/mOX5O6JjCUWtlYp5D8wajuQRVgy.jpg",
        releaseDate = "2023-09-15",
        title = "Expend4bles",
        hasVideo = false,
        voteAverage = 6.4,
        voteCount = 364
    )
    MovieItemCard(movieListItem)
}
