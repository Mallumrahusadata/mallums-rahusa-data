package com.mallum.rahusadata.data.api

import com.mallum.rahusadata.data.model.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // Auth
    @POST("auth/login")
    suspend fun login(@Body body: Map<String, String>): Response<AuthResponse>

    @POST("auth/register")
    suspend fun register(@Body body: Map<String, String>): Response<AuthResponse>

    @GET("auth/me")
    suspend fun getMe(): Response<Map<String, User>>

    @POST("auth/update-pin")
    suspend fun updatePin(@Body body: Map<String, String>): Response<GenericResponse>

    @POST("auth/reset-password")
    suspend fun resetPassword(@Body body: Map<String, String>): Response<GenericResponse>

    // Services
    @GET("services/data/plans")
    suspend fun getDataPlans(@Query("network") network: String? = null): Response<DataPlansResponse>

    @POST("services/data/buy")
    suspend fun buyData(@Body body: Map<String, String>): Response<ServicePurchaseResponse>

    @POST("services/airtime/buy")
    suspend fun buyAirtime(@Body body: Map<String, Any>): Response<ServicePurchaseResponse>

    @GET("services/electricity/discos")
    suspend fun getElectricityDiscos(): Response<DiscosResponse>

    @POST("services/electricity/verify")
    suspend fun verifyMeter(@Body body: Map<String, String>): Response<MeterVerifyResponse>

    @POST("services/electricity/pay")
    suspend fun payElectricity(@Body body: Map<String, Any>): Response<ServicePurchaseResponse>

    @GET("services/cable/packages")
    suspend fun getCablePackages(): Response<CablePackagesResponse>

    @POST("services/cable/verify")
    suspend fun verifyCable(@Body body: Map<String, String>): Response<CableVerifyResponse>

    @POST("services/cable/pay")
    suspend fun payCable(@Body body: Map<String, String>): Response<ServicePurchaseResponse>

    // Wallet Funding
    @POST("wallet/fund/simulate-transfer")
    suspend fun simulateBankTransfer(@Body body: Map<String, Any>): Response<ServicePurchaseResponse>

    @POST("wallet/fund/card")
    suspend fun fundWithCard(@Body body: Map<String, Any>): Response<ServicePurchaseResponse>

    // Transactions & Settings
    @GET("transactions")
    suspend fun getTransactions(
        @Query("userId") userId: String? = null,
        @Query("service") service: String? = null,
        @Query("status") status: String? = null,
        @Query("search") search: String? = null
    ): Response<TransactionsResponse>

    @GET("notifications")
    suspend fun getNotifications(): Response<NotificationsResponse>

    @GET("settings")
    suspend fun getSettings(): Response<SettingsResponse>

    // Admin Endpoints
    @GET("admin/stats")
    suspend fun getAdminStats(): Response<AdminStats>

    @GET("admin/users")
    suspend fun getAdminUsers(): Response<Map<String, List<User>>>

    @POST("admin/users/{id}/adjust-wallet")
    suspend fun adjustUserWallet(
        @Path("id") userId: String,
        @Body body: Map<String, Any>
    ): Response<GenericResponse>

    @POST("admin/plans")
    suspend fun createPlan(@Body body: Map<String, Any>): Response<GenericResponse>

    @PUT("admin/plans/{id}")
    suspend fun updatePlan(
        @Path("id") planId: String,
        @Body body: Map<String, Any>
    ): Response<GenericResponse>

    @DELETE("admin/plans/{id}")
    suspend fun deletePlan(@Path("id") planId: String): Response<GenericResponse>

    @POST("admin/broadcast")
    suspend fun broadcastNotification(@Body body: Map<String, String>): Response<GenericResponse>

    @POST("admin/transactions/{id}/refund")
    suspend fun refundTransaction(@Path("id") txId: String): Response<GenericResponse>
}
