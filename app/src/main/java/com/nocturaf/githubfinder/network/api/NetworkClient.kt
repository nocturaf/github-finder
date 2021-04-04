package com.nocturaf.githubfinder.network.api

import com.nocturaf.githubfinder.network.api.ApiConstant.BASE_URL
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class NetworkClient {

    companion object {

        val client: Retrofit by lazy {
            val httpLogging = HttpLoggingInterceptor().apply {
                setLevel(HttpLoggingInterceptor.Level.BODY)
            }
            val httpClient = OkHttpClient.Builder()
                .addInterceptor(httpLogging)
                .build()
            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient)
                .build()
        }

    }

}