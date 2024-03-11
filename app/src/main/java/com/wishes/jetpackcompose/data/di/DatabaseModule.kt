package com.green.china.di

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import com.wishes.jetpackcompose.data.LocalDataSource
import com.wishes.jetpackcompose.data.WallDatabase
import com.wishes.jetpackcompose.data.interfaces.ImageInterface
import com.wishes.jetpackcompose.data.repositories.RemoteDataSource
import com.wishes.jetpackcompose.repo.ImagesRepo
import com.wishes.jetpackcompose.repo.SettingRepository
import com.wishes.jetpackcompose.utlis.ClientApiManager
import com.wishes.jetpackcompose.utlis.DataStoreManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.features.HttpTimeout
import io.ktor.client.features.cache.HttpCache
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.features.logging.Logging
import io.ktor.client.features.websocket.WebSockets
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context) = WallDatabase.getDatabase(context)

    @Singleton
    @Provides
    fun provideDao(database: WallDatabase) = database.imageDao()

    @Provides
    @Singleton
    fun provideProfileImpl(
        apiManager: ClientApiManager
    ): ImageInterface {
        return RemoteDataSource(apiManager)
    }


    @Provides
    @Singleton
    fun provideImageRepo(
        remoteDataSource: RemoteDataSource,
        localDataSource: LocalDataSource
    ): ImagesRepo {
        return ImagesRepo(remoteDataSource, localDataSource)
    }


    @Provides
    @Singleton
    fun provideSettingRepo(dataStoreManager: DataStoreManager): SettingRepository {
        return SettingRepository(dataStoreManager)
    }

    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext appContext: Context) = DataStoreManager(appContext)

    @RequiresApi(Build.VERSION_CODES.O)
    @Provides
    @Singleton
    fun provideHttpClient(): HttpClient {
        return HttpClient(Android) {
            install(Logging)
            install(HttpCache)
            install(WebSockets)
            install(JsonFeature) {
                serializer = KotlinxSerializer()
            }
            install(HttpTimeout) { // Installing the HttpTimeout feature
                requestTimeoutMillis = 30_000 // Setting request timeout to 30 seconds
                connectTimeoutMillis = 30_000 // Setting connection timeout to 30 seconds
                socketTimeoutMillis = 30_000  // Setting socket timeout to 30 seconds
            }
        }

    }

}