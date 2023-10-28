package at.stefanirndorfer.bookmarks

import androidx.lifecycle.ViewModel
import at.stefanirndorfer.core.data.repository.MoviesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class BookmarksViewModel @Inject constructor(moviesRepository: MoviesRepository) : ViewModel() {
}