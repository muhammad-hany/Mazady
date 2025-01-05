package com.example.mazady.di

import com.example.mazady.data.network.RetrofitClient
import com.example.mazady.data.api.MazadyApi
import com.example.mazady.data.datasource.RemoteDataSource
import com.example.mazady.data.datasource.RemoteDataSourceImpl
import com.example.mazady.data.repository.Repository
import com.example.mazady.data.repository.RepositoryImpl
import com.example.mazady.view.CategorySelectionViewModel
import com.squareup.moshi.Moshi
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit

val dataModule = module {
    single<RetrofitClient> {
        RetrofitClient()
    }

    single<Moshi> {
        val client: RetrofitClient = get()
        client.buildMoshiInstance()
    }

    single<Retrofit> {
        val client: RetrofitClient = get()
        val moshi: Moshi = get()
        client.buildRetrofitInstance(moshi)
    }

    single<MazadyApi> {
        val retrofit = get<Retrofit>()
        retrofit.create(MazadyApi::class.java)
    }

    single<RemoteDataSource> {
        RemoteDataSourceImpl(mazadyApi = get())
    }

    single<Repository> {
        RepositoryImpl(remoteDataSource = get())
    }
}

val viewModule = module {
    viewModel {
        CategorySelectionViewModel(repository = get())
    }
}