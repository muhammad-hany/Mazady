package com.example.mazady.data.network

import com.example.mazady.BuildConfig
import com.squareup.moshi.Moshi
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

class RetrofitClient{
    fun buildRetrofitInstance(moshi: Moshi): Retrofit {
        val logging = httpLoggingInterceptor()
        val httpClient = okhttpBuilder(logging)
        return Retrofit
            .Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(
                MoshiConverterFactory.create(moshi)
            ).client(httpClient.build())
            .build()
    }

    fun buildMoshiInstance(): Moshi {
        return Moshi
            .Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }

    private fun httpLoggingInterceptor(): HttpLoggingInterceptor {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY
        return logging
    }

    private fun okhttpBuilder(logging: HttpLoggingInterceptor): OkHttpClient.Builder {
        return OkHttpClient
            .Builder()
            .addInterceptor(logging)
            .addInterceptor(ApiKeyInterceptor())
    }

}