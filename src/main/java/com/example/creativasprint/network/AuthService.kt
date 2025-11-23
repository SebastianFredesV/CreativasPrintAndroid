package com.example.creativasprint.network

import com.example.creativasprint.model.User
import com.example.creativasprint.network.requests.LoginRequest
import com.example.creativasprint.network.requests.RegisterRequest
import com.example.creativasprint.network.responses.AuthResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthService {

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>
}