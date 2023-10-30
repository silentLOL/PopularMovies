package at.stefanirndorfer.core.data.di

import at.stefanirndorfer.core.data.repository.MoviesRepository
import at.stefanirndorfer.core.data.repository.MoviesRepositoryImpl
import at.stefanirndorfer.network.RemoteMoviesDataSource
import at.stefanirndorfer.network.RemoteMoviesDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface MovieDataSourceModule {

    @Binds
    @Singleton
    fun bindMoviesDataSource(
        moviesDataSource: RemoteMoviesDataSourceImpl
    ): RemoteMoviesDataSource

    @Binds
    @Singleton
    fun bindMoviesRepository(
        moviesRepository: MoviesRepositoryImpl
    ): MoviesRepository

}