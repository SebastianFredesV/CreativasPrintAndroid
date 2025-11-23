package com.example.creativasprint.network

import com.example.creativasprint.model.Order
import com.example.creativasprint.model.Product
import com.example.creativasprint.model.User
import com.example.creativasprint.network.requests.CreateOrderRequest
import com.example.creativasprint.network.requests.LoginRequest
import com.example.creativasprint.network.requests.RegisterRequest
import com.example.creativasprint.network.requests.UpdateOrderStatusRequest
import com.example.creativasprint.network.responses.ApiResponse
import com.example.creativasprint.network.responses.AuthResponse
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // ============ AUTH ENDPOINTS ============
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>

    // ============ PRODUCT ENDPOINTS ============
    @GET("products")
    suspend fun getProducts(): Response<List<Product>>

    @GET("products/{id}")
    suspend fun getProductById(@Path("id") productId: String): Response<Product>

    @POST("products")
    suspend fun createProduct(@Body product: Product): Response<Product>

    @PUT("products/{id}")
    suspend fun updateProduct(@Path("id") productId: String, @Body product: Product): Response<Product>

    @DELETE("products/{id}")
    suspend fun deleteProduct(@Path("id") productId: String): Response<ApiResponse>

    // ============ USER ENDPOINTS (Admin only) ============
    @GET("admin/users")
    suspend fun getUsers(): Response<List<User>>

    @PUT("admin/users/{id}/block")
    suspend fun blockUser(@Path("id") userId: String): Response<User>

    @PUT("admin/users/{id}/unblock")
    suspend fun unblockUser(@Path("id") userId: String): Response<User>

    // ============ ORDER ENDPOINTS ============
    @GET("orders")
    suspend fun getOrders(): Response<List<Order>>

    @POST("orders")
    suspend fun createOrder(@Body request: CreateOrderRequest): Response<Order>

    // Admin orders
    @GET("admin/orders")
    suspend fun getAdminOrders(): Response<List<Order>>

    @PUT("admin/orders/{id}/status")
    suspend fun updateOrderStatus(
        @Path("id") orderId: String,
        @Body request: UpdateOrderStatusRequest
    ): Response<Order>
}