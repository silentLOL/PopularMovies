package at.stefanirndorfer.feature.movielist

import at.stefanirndorfer.core.data.util.ResourceState
import at.stefanirndorfer.testing.repository.TestMoviesRepository
import at.stefanirndorfer.testing.util.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class MovieListViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val movieListRepository = TestMoviesRepository()
    private lateinit var viewModel: MovieListViewModel

    @Before
    fun setup() {
        viewModel = MovieListViewModel(movieListRepository = movieListRepository)
    }

    @Test
    fun testInitialStateIsLoading() = runTest {
        assertTrue(
            viewModel.movies.value is ResourceState.Loading
        )
    }

//    @Test
//    fun movieListUpdatedAfterSuccessfulResponse() = runTest {
//        val collectJob1 = launch(UnconfinedTestDispatcher()) {
//            viewModel.movies.collect()
//        }
//        collectJob1.cancel()
//    }
}