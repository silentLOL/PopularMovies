package at.stefanirndorfer.core.data.di

import at.stefanirndorfer.network.RemoteMoviesDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [RemoteMoviesDataSource::class]
)
abstract class MovieRepositoryTestModule {

    @Singleton
    @Binds
    abstract fun bindMoviesRepository(
        fakeMoviesRepository: FakeMoviesDataSource
    ): DataModule
}