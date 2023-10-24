package at.stefanirndorfer.core.data.di


import at.stefanirndorfer.core.data.remote.ApiConstants
import at.stefanirndorfer.core.data.remote.RemoteMoviesApi
import at.stefanirndorfer.core.data.repository.MoviesRepositoryImpl
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun provideLoggingInterceptor() = HttpLoggingInterceptor()
                .apply {
                    level = HttpLoggingInterceptor.Level.BODY
                }

    @Provides
    @Singleton
    fun provideHttpClient(loggingInterceptor: HttpLoggingInterceptor) = OkHttpClient().newBuilder().apply {
        addInterceptor(loggingInterceptor)
        readTimeout(50, TimeUnit.SECONDS)
    }.build()

    @Provides
    @Singleton
    fun provideMoshi(): Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()


    @Provides
    @Singleton
    fun provideRemoteMoviesApi(moshi: Moshi, httpClient: OkHttpClient): RemoteMoviesApi {
        return Retrofit.Builder()
            .baseUrl(ApiConstants.BASE_URL)
            .client(httpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(RemoteMoviesApi::class.java)
    }


}