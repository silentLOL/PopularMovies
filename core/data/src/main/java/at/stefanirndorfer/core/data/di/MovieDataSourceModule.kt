package at.stefanirndorfer.core.data.di

import at.stefanirndorfer.network.MoviesDataSource
import at.stefanirndorfer.network.RemoteMoviesDataSourceImpl
import at.stefanirndorfer.core.data.repository.MoviesRepository
import at.stefanirndorfer.core.data.repository.MoviesRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class MovieDataSourceModule {

    @Binds
    @Singleton
    abstract fun bindMoviesDataSource(
        moviesDataSource: RemoteMoviesDataSourceImpl
    ): MoviesDataSource

    @Binds
    @Singleton
    abstract fun bindMoviesRepository(
        moviesRepository: MoviesRepositoryImpl
    ): MoviesRepository

}