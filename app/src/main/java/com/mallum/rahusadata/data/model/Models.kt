package com.mallum.rahusadata.data.model

data class VirtualAccount(
    val bankName: String,
    val accountNumber: String,
    val accountName: String
)

data class User(
    val id: String,
    val fullName: String,
    val email: String,
    val phoneNumber: String,
    val role: String, // "admin" or "customer"
    val balance: Double,
    val pin: String? = null,
    val referralCode: String,
    val referredBy: String? = null,
    val referralBonus: Double = 0.0,
    val referralCount: Int = 0,
    val virtualAccounts: List<VirtualAccount> = emptyList(),
    val createdAt: String
)

data class AuthResponse(
    val user: User,
    val token: String
)

data class DataPlan(
    val id: String,
    val network: String, // "MTN", "Airtel", "Glo", "9mobile"
    val type: String,    // "SME", "Corporate Gifting", "Gifting"
    val name: String,
    val size: String,
    val validity: String,
    val costPrice: Double,
    val price: Double,
    val active: Boolean = true
)

data class DataPlansResponse(
    val plans: List<DataPlan>
)

data class Transaction(
    val id: String,
    val userId: String,
    val userEmail: String,
    val userName: String,
    val service: String, // "DATA", "AIRTIME", "ELECTRICITY", "CABLE", "FUNDING", "ADMIN_ADJUSTMENT"
    val description: String,
    val amount: Double,
    val faceValue: Double? = null,
    val costPrice: Double? = null,
    val beneficiary: String,
    val network: String,
    val status: String, // "SUCCESSFUL", "PENDING", "FAILED", "REFUNDED"
    val reference: String,
    val token: String? = null,
    val units: String? = null,
    val apiProvider: String? = null,
    val createdAt: String
)

data class TransactionsResponse(
    val transactions: List<Transaction>
)

data class ServicePurchaseResponse(
    val message: String,
    val transaction: Transaction,
    val newBalance: Double
)

data class ElectricityDisco(
    val id: String,
    val name: String,
    val state: String
)

data class DiscosResponse(
    val discos: List<ElectricityDisco>
)

data class MeterVerifyResponse(
    val valid: Boolean,
    val customerName: String,
    val meterNumber: String,
    val address: String,
    val disco: String
)

data class CablePackage(
    val id: String,
    val provider: String,
    val name: String,
    val price: Double
)

data class CablePackagesResponse(
    val packages: List<CablePackage>
)

data class CableVerifyResponse(
    val valid: Boolean,
    val customerName: String,
    val smartcardNumber: String,
    val provider: String,
    val dueDate: String
)

data class NotificationItem(
    val id: String,
    val title: String,
    val message: String,
    val type: String,
    val createdAt: String
)

data class NotificationsResponse(
    val notifications: List<NotificationItem>
)

data class AdminStats(
    val totalUsers: Int,
    val totalBalance: Double,
    val totalTransactions: Int,
    val todaySales: Double,
    val totalSales: Double,
    val totalProfit: Double,
    val apiBalance: Double
)

data class AppSettings(
    val appName: String,
    val tagline: String,
    val currency: String,
    val adminEmail: String,
    val supportPhone: String,
    val supportWhatsapp: String,
    val airtimeDiscountPercent: Double,
    val apiProvider: String,
    val apiMode: String,
    val apiBalance: Double,
    val announcementBanner: String
)

data class SettingsResponse(
    val settings: AppSettings
)

data class GenericResponse(
    val message: String
)
