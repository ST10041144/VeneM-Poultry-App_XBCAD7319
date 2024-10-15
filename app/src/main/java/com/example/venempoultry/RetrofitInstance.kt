package com.example.venempoultry

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    private val interceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(interceptor)
        .build()

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://ranjjsnzhklmubcydccn.supabase.co")  // Replace with your Supabase project URL
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val api: SupabaseApi by lazy {
        retrofit.create(SupabaseApi::class.java)
    }
}
