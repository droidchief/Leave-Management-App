package com.victorloveday.leavemanager.api

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitInstance {

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .build()

    val api : LeaveApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://steamledge.com")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(LeaveApi::class.java)
    }
}