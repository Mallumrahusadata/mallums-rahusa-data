package com.mallum.rahusadata.data.repository

import com.mallum.rahusadata.data.api.ApiClient
import com.mallum.rahusadata.data.model.*
import org.json.JSONObject

class RahusaRepository {
    private val api = ApiClient.service

    suspend fun login(email: String, pass: String): Result<AuthResponse> = runCatching {
        val res = api.login(mapOf("email" to email, "password" to pass))
        if (res.isSuccessful && res.body() != null) {
            val auth = res.body()!!
            ApiClient.setAuthToken(auth.token)
            auth
        } else {
            throw Exception(parseError(res.errorBody()?.string()))
        }
    }

    suspend fun register(fullName: String, email: String, phone: String, pass: String, refCode: String?): Result<AuthResponse> = runCatching {
        val params = mutableMapOf(
            "fullName" to fullName,
            "email" to email,
            "phoneNumber" to phone,
            "password" to pass
        )
        if (!refCode.isNullOrBlank()) {
            params["referralCode"] = refCode
        }
        val res = api.register(params)
        if (res.isSuccessful && res.body() != null) {
            val auth = res.body()!!
            ApiClient.setAuthToken(auth.token)
            auth
        } else {
            throw Exception(parseError(res.errorBody()?.string()))
        }
    }

    suspend fun getMe(): Result<User> = runCatching {
        val res = api.getMe()
        if (res.isSuccessful && res.body() != null) {
            res.body()!!["user"] ?: throw Exception("User not found")
        } else {
            throw Exception(parseError(res.errorBody()?.string()))
        }
    }

    suspend fun getDataPlans(network: String? = null): Result<List<DataPlan>> = runCatching {
        val res = api.getDataPlans(network)
        if (res.isSuccessful && res.body() != null) {
            res.body()!!.plans
        } else {
            throw Exception(parseError(res.errorBody()?.string()))
        }
    }

    suspend fun buyData(userId: String, planId: String, phone: String, pin: String): Result<ServicePurchaseResponse> = runCatching {
        val res = api.buyData(mapOf(
            "userId" to userId,
            "planId" to planId,
            "phoneNumber" to phone,
            "pin" to pin
        ))
        if (res.isSuccessful && res.body() != null) {
            res.body()!!
        } else {
            throw Exception(parseError(res.errorBody()?.string()))
        }
    }

    suspend fun buyAirtime(userId: String, network: String, amount: Double, phone: String, pin: String): Result<ServicePurchaseResponse> = runCatching {
        val res = api.buyAirtime(mapOf(
            "userId" to userId,
            "network" to network,
            "amount" to amount,
            "phoneNumber" to phone,
            "pin" to pin
        ))
        if (res.isSuccessful && res.body() != null) {
            res.body()!!
        } else {
            throw Exception(parseError(res.errorBody()?.string()))
        }
    }

    suspend fun getElectricityDiscos(): Result<List<ElectricityDisco>> = runCatching {
        val res = api.getElectricityDiscos()
        if (res.isSuccessful && res.body() != null) {
            res.body()!!.discos
        } else {
            throw Exception(parseError(res.errorBody()?.string()))
        }
    }

    suspend fun verifyMeter(discoId: String, meterNumber: String, meterType: String): Result<MeterVerifyResponse> = runCatching {
        val res = api.verifyMeter(mapOf(
            "discoId" to discoId,
            "meterNumber" to meterNumber,
            "meterType" to meterType
        ))
        if (res.isSuccessful && res.body() != null) {
            res.body()!!
        } else {
            throw Exception(parseError(res.errorBody()?.string()))
        }
    }

    suspend fun payElectricity(
        userId: String,
        discoId: String,
        meterNumber: String,
        meterType: String,
        amount: Double,
        phone: String,
        name: String,
        pin: String
    ): Result<ServicePurchaseResponse> = runCatching {
        val res = api.payElectricity(mapOf(
            "userId" to userId,
            "discoId" to discoId,
            "meterNumber" to meterNumber,
            "meterType" to meterType,
            "amount" to amount,
            "customerPhone" to phone,
            "customerName" to name,
            "pin" to pin
        ))
        if (res.isSuccessful && res.body() != null) {
            res.body()!!
        } else {
            throw Exception(parseError(res.errorBody()?.string()))
        }
    }

    suspend fun getCablePackages(): Result<List<CablePackage>> = runCatching {
        val res = api.getCablePackages()
        if (res.isSuccessful && res.body() != null) {
            res.body()!!.packages
        } else {
            throw Exception(parseError(res.errorBody()?.string()))
        }
    }

    suspend fun verifyCable(provider: String, smartcard: String): Result<CableVerifyResponse> = runCatching {
        val res = api.verifyCable(mapOf("provider" to provider, "smartcardNumber" to smartcard))
        if (res.isSuccessful && res.body() != null) {
            res.body()!!
        } else {
            throw Exception(parseError(res.errorBody()?.string()))
        }
    }

    suspend fun payCable(userId: String, pkgId: String, smartcard: String, name: String, pin: String): Result<ServicePurchaseResponse> = runCatching {
        val res = api.payCable(mapOf(
            "userId" to userId,
            "packageId" to pkgId,
            "smartcardNumber" to smartcard,
            "customerName" to name,
            "pin" to pin
        ))
        if (res.isSuccessful && res.body() != null) {
            res.body()!!
        } else {
            throw Exception(parseError(res.errorBody()?.string()))
        }
    }

    suspend fun simulateBankTransfer(userId: String, amount: Double, bankName: String): Result<ServicePurchaseResponse> = runCatching {
        val res = api.simulateBankTransfer(mapOf(
            "userId" to userId,
            "amount" to amount,
            "bankName" to bankName
        ))
        if (res.isSuccessful && res.body() != null) {
            res.body()!!
        } else {
            throw Exception(parseError(res.errorBody()?.string()))
        }
    }

    suspend fun fundWithCard(userId: String, amount: Double): Result<ServicePurchaseResponse> = runCatching {
        val res = api.fundWithCard(mapOf(
            "userId" to userId,
            "amount" to amount
        ))
        if (res.isSuccessful && res.body() != null) {
            res.body()!!
        } else {
            throw Exception(parseError(res.errorBody()?.string()))
        }
    }

    suspend fun getTransactions(userId: String? = null, service: String? = null, status: String? = null): Result<List<Transaction>> = runCatching {
        val res = api.getTransactions(userId, service, status)
        if (res.isSuccessful && res.body() != null) {
            res.body()!!.transactions
        } else {
            throw Exception(parseError(res.errorBody()?.string()))
        }
    }

    suspend fun getSettings(): Result<AppSettings> = runCatching {
        val res = api.getSettings()
        if (res.isSuccessful && res.body() != null) {
            res.body()!!.settings
        } else {
            throw Exception(parseError(res.errorBody()?.string()))
        }
    }

    suspend fun getAdminStats(): Result<AdminStats> = runCatching {
        val res = api.getAdminStats()
        if (res.isSuccessful && res.body() != null) {
            res.body()!!
        } else {
            throw Exception(parseError(res.errorBody()?.string()))
        }
    }

    suspend fun getAdminUsers(): Result<List<User>> = runCatching {
        val res = api.getAdminUsers()
        if (res.isSuccessful && res.body() != null) {
            res.body()!!["users"] ?: emptyList()
        } else {
            throw Exception(parseError(res.errorBody()?.string()))
        }
    }

    suspend fun adjustUserWallet(userId: String, type: String, amount: Double, reason: String): Result<String> = runCatching {
        val res = api.adjustUserWallet(userId, mapOf(
            "type" to type,
            "amount" to amount,
            "reason" to reason
        ))
        if (res.isSuccessful && res.body() != null) {
            res.body()!!.message
        } else {
            throw Exception(parseError(res.errorBody()?.string()))
        }
    }

    suspend fun refundTransaction(txId: String): Result<String> = runCatching {
        val res = api.refundTransaction(txId)
        if (res.isSuccessful && res.body() != null) {
            res.body()!!.message
        } else {
            throw Exception(parseError(res.errorBody()?.string()))
        }
    }

    private fun parseError(json: String?): String {
        return try {
            if (json != null) {
                val obj = JSONObject(json)
                obj.optString("error", "An error occurred")
            } else {
                "Network request failed"
            }
        } catch (_: Exception) {
            "An unexpected error occurred"
        }
    }
}
