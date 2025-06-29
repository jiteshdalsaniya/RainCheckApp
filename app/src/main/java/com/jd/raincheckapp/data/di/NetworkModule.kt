package com.jd.raincheckapp.data.di

import com.jd.raincheckapp.data.api.NominatimApiService
import com.jd.raincheckapp.data.api.OpenWeatherMapApiService
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
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    private const val USER_AGENT = "JiteshDalsaniya/1.0 (jiteshdalsaniya@email.com)"

    @Provides
    @Singleton
    fun provideUserAgentInterceptor(): Interceptor {
        return UserAgentInterceptor(USER_AGENT)
    }

    @Provides
    @Singleton
    fun provideMoshi(): Moshi {
        return Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(httpLoggingInterceptor: HttpLoggingInterceptor,
                            userAgentInterceptor: Interceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(userAgentInterceptor)
            .addInterceptor(httpLoggingInterceptor)
            .build()
    }

    @Provides
    @Singleton
    fun provideNominatimApiService(okHttpClient: OkHttpClient,moshi: Moshi): NominatimApiService {
        return Retrofit.Builder()
            .baseUrl("https://nominatim.openstreetmap.org")
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(NominatimApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideOpenWeatherMapApiService(okHttpClient: OkHttpClient,moshi: Moshi): OpenWeatherMapApiService {
        return Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(OpenWeatherMapApiService::class.java)
    }
}