package com.jd.raincheckapp.data.di

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class NominatimRetrofit

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class OpenWeatherMapRetrofit