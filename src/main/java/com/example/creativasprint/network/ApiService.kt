package com.example.creativasprint.network

import com.example.creativasprint.model.Order
import com.example.creativasprint.model.Product
import com.example.creativasprint.model.User
import com.example.creativasprint.network.requests.*
import com.example.creativasprint.network.responses.ApiResponse
import com.example.creativasprint.network.responses.XanoAuthResponse
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // ============ AUTH ENDPOINTS ============
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<XanoAuthResponse>

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<XanoAuthResponse>

    // ============ PRODUCT ENDPOINTS ============
    @GET("product")
    suspend fun getProducts(): Response<List<Product>>

    @GET("product/{id}")
    suspend fun getProductById(@Path("id") productId: Int): Response<Product>

    @POST("product")
    suspend fun createProduct(@Body product: Product): Response<Product>

    @PUT("product/{id}")
    suspend fun updateProduct(@Path("id") productId: Int, @Body product: Product): Response<Product>

    @DELETE("product/{id}")
    suspend fun deleteProduct(@Path("id") productId: Int): Response<ApiResponse>

    // ============ USER ENDPOINTS (usando tus endpoints reales) ============

    // ✅ Obtener usuarios del equipo
    @GET("account/my_team_members")
    suspend fun getTeamMembers(): Response<List<User>>

    // ✅ Actualizar rol de usuario
    @POST("admin/user_role")
    suspend fun updateUserRole(@Body request: UpdateUserRoleRequest): Response<User>

    // ✅ Editar perfil de usuario (para activar/desactivar)
    @PATCH("user/edit_profile")
    suspend fun updateUserProfile(@Body request: UpdateUserProfileRequest): Response<User>

    // ✅ Obtener detalles de usuario específico
    @GET("account/details")
    suspend fun getAccountDetails(): Response<User>

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
        @Path("id") orderId: Int,
        @Body request: UpdateOrderStatusRequest
    ): Response<Order>
}