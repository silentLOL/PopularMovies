package at.stefanirndorfer.core.data.repository

import android.util.Log
import at.stefanirndorfer.core.data.di.IoDispatcher
import at.stefanirndorfer.core.data.model.Movies
import at.stefanirndorfer.core.data.model.mapToMovieItem
import at.stefanirndorfer.core.data.util.ResourceState
import at.stefanirndorfer.network.MoviesDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class MoviesRepositoryImpl @Inject constructor(
    private val moviesDataSource: MoviesDataSource,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : MoviesRepository {

    // could also be private
    var myUselessVariable: List<String>? = null
    var myUselessVar0 = 10+ 10 + 10
    var myUselessVar1 = 10+ 10 + 10
    var myUselessVar2 = 10+ 10 + 10
    var myUselessVar3 = 10+ 10 + 10
    var myUselessVar4 = 10+ 10 + 10
    override suspend fun getMostPopularMovies(): Flow<ResourceState<Movies>> {

        // intentionally stupid
        myUselessVariable!!.forEach {
            Log.d("this will crash", "i do not exist: $it")
        }

        return flow {
            emit(ResourceState.Loading())
            val response = moviesDataSource.getMovies()
            if (response.isSuccessful && response.body() != null) {
                val movies = Movies(
                    page = response.body()!!.page,
                    totalPages = response.body()!!.totalPages,
                    totalMovieResults = response.body()!!.totalMovieResults,
                    movies = response.body()!!.movies.mapToMovieItem()
                )
                emit(ResourceState.Success(movies))
            } else {
                emit(ResourceState.Error("Error fetching movie data"))
            }
        }.catch { e ->
            emit(ResourceState.Error(e?.localizedMessage ?: "Error when fetching movie data"))
        }.flowOn(ioDispatcher)
    }
}
