package com.carealls.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitService {
    var BASE_URL = "http://stageofproject.com"

    /*------------Use For Retrofit----------------------*/
    val aPIService: RestApi
        get() {
            /*------------Use For Retrofit----------------------*/
            val interceptor = HttpLoggingInterceptor()
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
            val client =
                OkHttpClient.Builder().addInterceptor(interceptor).build()
            client.readTimeoutMillis
            // Change base URL to your upload server URL.
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(RestApi::class.java)
        }
}
