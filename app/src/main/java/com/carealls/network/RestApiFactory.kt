package com.carealls.network

import android.util.Log
import androidx.databinding.library.BuildConfig
import com.carealls.App
import com.carealls.BuildConfig.SERVER_PATH
import com.carealls.utils.SessionManager
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RestApiFactory {

    fun create(): RestApi {
        val sessionManager: SessionManager = SessionManager.getInstance(App.singleton)
        val httpClient = OkHttpClient.Builder()

        val logLevel = HttpLoggingInterceptor.Level.BODY
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = logLevel
        httpClient.addInterceptor { chain ->
            val request = chain.request().newBuilder().addHeader("auth", sessionManager.authToken).build()

            if (BuildConfig.DEBUG)
                Log.e("authToken", sessionManager.authToken)
            chain.proceed(request)
        }.addInterceptor(interceptor)
        httpClient.connectTimeout(5, TimeUnit.MINUTES)// connect timeout
        httpClient.readTimeout(5,TimeUnit.MINUTES)// socket timeout
        httpClient.writeTimeout(5, TimeUnit.MINUTES) // write timeout

        val retrofit = Retrofit.Builder().baseUrl(SERVER_PATH)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(httpClient.build())
            .build()
        return retrofit.create(RestApi::class.java)
    }
}