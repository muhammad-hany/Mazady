package com.example.mazady.data.network

import com.example.mazady.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response

class ApiKeyInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val request = originalRequest
            .newBuilder()
            .addHeader("private-key", BuildConfig.API_KEY)
            .build()
        return chain.proceed(request)
    }
}