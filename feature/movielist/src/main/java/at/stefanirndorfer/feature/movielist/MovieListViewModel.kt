package at.stefanirndorfer.feature.movielist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import at.stefanirndorfer.core.data.model.Movies
import at.stefanirndorfer.core.data.repository.MoviesRepository
import at.stefanirndorfer.core.data.util.ResourceState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MovieListViewModel @Inject constructor(
    private val movieListRepository: MoviesRepository,
) : ViewModel() {
    private val _movies: MutableStateFlow<ResourceState<Movies>> = MutableStateFlow(ResourceState.Loading())
    val movies = _movies.asStateFlow()

    init {
        getMovies()
    }

    private fun getMovies() {
        viewModelScope.launch {
            movieListRepository.getMostPopularMovies()
                .collectLatest { moviesResponse ->
                    _movies.value = moviesResponse
                }
        }
    }

}