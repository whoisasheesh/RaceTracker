package com.entain.racetracker.di

import android.content.Context
import androidx.room.Room
import com.entain.racetracker.data.local.RaceSummaryDao
import com.entain.racetracker.data.local.RaceSummaryDatabase
import com.entain.racetracker.data.network.NetworkConnectivityService
import com.entain.racetracker.data.remote.ApiService
import com.entain.racetracker.data.repository.RaceRepositoryImpl
import com.entain.racetracker.domain.repository.RaceRepository
import com.entain.racetracker.utils.Constants.Companion.BASE_URL
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun providesContext(@ApplicationContext context: Context): Context {
        return context
    }

    @Provides
    fun providesBaseUrl(): String {
        return BASE_URL
    }

    @Provides
    fun providesLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
    }

    @Provides
    fun providesOkHttpClient(loggingInterceptor: HttpLoggingInterceptor): OkHttpClient {
        return OkHttpClient().newBuilder()
            .callTimeout(40, TimeUnit.SECONDS)
            .connectTimeout(40, TimeUnit.SECONDS)
            .readTimeout(40, TimeUnit.SECONDS)
            .writeTimeout(40, TimeUnit.SECONDS)
            .addInterceptor(loggingInterceptor).build()
    }

    @Provides
    fun providesConverterFactory(): Converter.Factory {
        return GsonConverterFactory.create()
    }

    @Provides
    fun providesRetrofitClient(
        okHttpClient: OkHttpClient,
        baseUrl: String,
        converterFactory: Converter.Factory
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(converterFactory)
            .build()
    }

    @Provides
    fun providesApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }

    @Provides
    fun providesRaceRepository(
        apiService: ApiService,
        raceSummaryDao: RaceSummaryDao
    ): RaceRepository {
        return RaceRepositoryImpl(apiService, raceSummaryDao)
    }

    @Provides
    @Singleton
    fun providesLocalDatabase(@ApplicationContext context: Context): RaceSummaryDatabase =
        Room.databaseBuilder(context, RaceSummaryDatabase::class.java, "race_summary")
            .fallbackToDestructiveMigration().build()

    @Provides
    fun providesMovieDao(raceSummaryDatabase: RaceSummaryDatabase) =
        raceSummaryDatabase.raceSummaryDao()
}