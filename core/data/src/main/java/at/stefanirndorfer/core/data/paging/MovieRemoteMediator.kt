package at.stefanirndorfer.core.data.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import at.stefanirndorfer.core.data.util.mapMovieItemResponseToMovieEntity
import at.stefanirndorfer.database.PMDatabase
import at.stefanirndorfer.database.model.MovieEntity
import at.stefanirndorfer.network.RemoteMoviesDataSource
import retrofit2.HttpException
import java.io.IOException

@OptIn(ExperimentalPagingApi::class)
class MovieRemoteMediator(
    private val movieDb: PMDatabase,
    private val movieApi: RemoteMoviesDataSource
) : RemoteMediator<Int, MovieEntity>() {
    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, MovieEntity>
    ): MediatorResult {
        return try {
            val loadKey = when (loadType) {
                LoadType.REFRESH -> 1
                LoadType.PREPEND -> return MediatorResult.Success(
                    endOfPaginationReached = true
                )

                LoadType.APPEND -> {
                    val lastItem = state.lastItemOrNull()
                    if (lastItem == null) {
                        1
                    } else {
                        lastItem.page + 1
                    }
                }
            }

            val moviesResponse = movieApi.getMovies(
                page = loadKey
            ).body()!!

            movieDb.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    movieDb.dao.clearAll()
                }
                val movieEntities = moviesResponse.movies.mapMovieItemResponseToMovieEntity(loadKey)
                movieDb.dao.upsertAll(movieEntities)
            }
            MediatorResult.Success(
                endOfPaginationReached = loadKey == moviesResponse.totalPages
            )
        } catch (e: IOException) {
            MediatorResult.Error(e)
        } catch (e: HttpException) {
            MediatorResult.Error(e)
        }
    }
}