package com.jd.raincheckapp.data.di

import okhttp3.Interceptor
import okhttp3.Response

class UserAgentInterceptor(
    private val userAgent: String
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val requestWithUserAgent = original.newBuilder()
            .header("User-Agent", userAgent)
            .build()
        return chain.proceed(requestWithUserAgent)
    }
}