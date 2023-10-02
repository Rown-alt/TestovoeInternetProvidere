package com.example.domain.di

import com.example.domain.api.AirNetApi
import com.example.domain.repository.Repository
import com.example.domain.repository.RepositoryImpl
import com.example.domain.result.ResultCallAdapterFactory
import com.google.gson.GsonBuilder
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val domainModule = module {
    val gson = GsonBuilder()
        .setLenient()
        .create()
    single{
        Retrofit.Builder()
            .baseUrl("https://stat-api.airnet.ru/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(ResultCallAdapterFactory())
            .build()
            .create(AirNetApi::class.java)
    }

    single<Repository>{
        RepositoryImpl(get())
    }
}