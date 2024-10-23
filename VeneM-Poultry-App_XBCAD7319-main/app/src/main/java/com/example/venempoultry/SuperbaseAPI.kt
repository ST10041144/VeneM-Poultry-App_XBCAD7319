package com.example.venempoultry

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface SupabaseApi {

    @POST("auth/v1/signup")
    suspend fun signUp(@Body requestBody: Map<String, String>): Response<SignUpResponse>

    @POST("auth/v1/token?grant_type=password")
    suspend fun signIn(@Body requestBody: Map<String, String>): Response<SignInResponse>

    @GET("rest/v1/flocks")
    suspend fun getFlocks(@Header("Authorization") token: String): Response<List<Flock>>

    @POST("rest/v1/flocks")
    suspend fun createFlock(@Header("Authorization") token: String, @Body flock: Flock): Response<Flock>

    @PUT("rest/v1/flocks/{id}")
    suspend fun updateFlock(@Header("Authorization") token: String, @Path("id") id: Int, @Body flock: Flock): Response<Flock>

    @DELETE("rest/v1/flocks/{id}")
    suspend fun deleteFlock(@Header("Authorization") token: String, @Path("id") id: Int): Response<Unit>
}
