package com.mallum.rahusadata.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mallum.rahusadata.data.model.*
import com.mallum.rahusadata.data.repository.RahusaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class UiState(
    val user: User? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val transactions: List<Transaction> = emptyList(),
    val dataPlans: List<DataPlan> = emptyList(),
    val activeReceipt: Transaction? = null,
    val settings: AppSettings? = null,
    val adminStats: AdminStats? = null,
    val adminUsers: List<User> = emptyList()
)

class RahusaViewModel(
    private val repository: RahusaRepository = RahusaRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        // Auto-login default demo customer for immediate usability
        loginCustomerDemo()
        loadDataPlans()
        loadSettings()
    }

    fun login(email: String, pass: String, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            val res = repository.login(email, pass)
            res.onSuccess { auth ->
                _uiState.value = _uiState.value.copy(
                    user = auth.user,
                    isLoading = false
                )
                loadTransactions()
                onSuccess()
            }.onFailure { err ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = err.message
                )
            }
        }
    }

    fun register(name: String, email: String, phone: String, pass: String, refCode: String?, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            val res = repository.register(name, email, phone, pass, refCode)
            res.onSuccess { auth ->
                _uiState.value = _uiState.value.copy(
                    user = auth.user,
                    isLoading = false
                )
                loadTransactions()
                onSuccess()
            }.onFailure { err ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = err.message
                )
            }
        }
    }

    fun loginCustomerDemo() {
        login("customer@rahusadata.com", "password123")
    }

    fun loginAdminDemo() {
        login("abubakarisahmallum@gmail.com", "password123")
    }

    fun logout() {
        _uiState.value = _uiState.value.copy(user = null, transactions = emptyList())
    }

    fun clearMessages() {
        _uiState.value = _uiState.value.copy(errorMessage = null, successMessage = null)
    }

    fun dismissReceipt() {
        _uiState.value = _uiState.value.copy(activeReceipt = null)
    }

    fun showReceipt(tx: Transaction) {
        _uiState.value = _uiState.value.copy(activeReceipt = tx)
    }

    fun loadTransactions() {
        viewModelScope.launch {
            val userId = _uiState.value.user?.id
            val res = repository.getTransactions(userId = userId)
            res.onSuccess { list ->
                _uiState.value = _uiState.value.copy(transactions = list)
            }
        }
    }

    fun loadDataPlans(network: String? = null) {
        viewModelScope.launch {
            val res = repository.getDataPlans(network)
            res.onSuccess { plans ->
                _uiState.value = _uiState.value.copy(dataPlans = plans)
            }
        }
    }

    fun loadSettings() {
        viewModelScope.launch {
            val res = repository.getSettings()
            res.onSuccess { settings ->
                _uiState.value = _uiState.value.copy(settings = settings)
            }
        }
    }

    fun buyData(planId: String, phone: String, pin: String, onSuccess: () -> Unit) {
        val user = _uiState.value.user ?: return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            val res = repository.buyData(user.id, planId, phone, pin)
            res.onSuccess { r ->
                _uiState.value = _uiState.value.copy(
                    user = user.copy(balance = r.newBalance),
                    isLoading = false,
                    activeReceipt = r.transaction,
                    successMessage = r.message
                )
                loadTransactions()
                onSuccess()
            }.onFailure { err ->
                _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = err.message)
            }
        }
    }

    fun buyAirtime(network: String, amount: Double, phone: String, pin: String, onSuccess: () -> Unit) {
        val user = _uiState.value.user ?: return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            val res = repository.buyAirtime(user.id, network, amount, phone, pin)
            res.onSuccess { r ->
                _uiState.value = _uiState.value.copy(
                    user = user.copy(balance = r.newBalance),
                    isLoading = false,
                    activeReceipt = r.transaction,
                    successMessage = r.message
                )
                loadTransactions()
                onSuccess()
            }.onFailure { err ->
                _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = err.message)
            }
        }
    }

    fun payElectricity(
        discoId: String,
        meterNumber: String,
        meterType: String,
        amount: Double,
        phone: String,
        name: String,
        pin: String,
        onSuccess: () -> Unit
    ) {
        val user = _uiState.value.user ?: return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            val res = repository.payElectricity(user.id, discoId, meterNumber, meterType, amount, phone, name, pin)
            res.onSuccess { r ->
                _uiState.value = _uiState.value.copy(
                    user = user.copy(balance = r.newBalance),
                    isLoading = false,
                    activeReceipt = r.transaction,
                    successMessage = r.message
                )
                loadTransactions()
                onSuccess()
            }.onFailure { err ->
                _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = err.message)
            }
        }
    }

    fun payCable(
        packageId: String,
        smartcard: String,
        name: String,
        pin: String,
        onSuccess: () -> Unit
    ) {
        val user = _uiState.value.user ?: return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            val res = repository.payCable(user.id, packageId, smartcard, name, pin)
            res.onSuccess { r ->
                _uiState.value = _uiState.value.copy(
                    user = user.copy(balance = r.newBalance),
                    isLoading = false,
                    activeReceipt = r.transaction,
                    successMessage = r.message
                )
                loadTransactions()
                onSuccess()
            }.onFailure { err ->
                _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = err.message)
            }
        }
    }

    fun simulateBankInflow(amount: Double, bank: String, onSuccess: () -> Unit) {
        val user = _uiState.value.user ?: return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            val res = repository.simulateBankTransfer(user.id, amount, bank)
            res.onSuccess { r ->
                _uiState.value = _uiState.value.copy(
                    user = user.copy(balance = r.newBalance),
                    isLoading = false,
                    activeReceipt = r.transaction,
                    successMessage = r.message
                )
                loadTransactions()
                onSuccess()
            }.onFailure { err ->
                _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = err.message)
            }
        }
    }

    fun loadAdminData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val statsRes = repository.getAdminStats()
            val usersRes = repository.getAdminUsers()
            statsRes.onSuccess { stats ->
                _uiState.value = _uiState.value.copy(adminStats = stats)
            }
            usersRes.onSuccess { list ->
                _uiState.value = _uiState.value.copy(adminUsers = list)
            }
            _uiState.value = _uiState.value.copy(isLoading = false)
        }
    }

    fun adjustUserWallet(targetUserId: String, type: String, amount: Double, reason: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val res = repository.adjustUserWallet(targetUserId, type, amount, reason)
            res.onSuccess { msg ->
                _uiState.value = _uiState.value.copy(successMessage = msg)
                loadAdminData()
            }.onFailure { err ->
                _uiState.value = _uiState.value.copy(errorMessage = err.message)
            }
            _uiState.value = _uiState.value.copy(isLoading = false)
        }
    }

    fun refundTransaction(txId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val res = repository.refundTransaction(txId)
            res.onSuccess { msg ->
                _uiState.value = _uiState.value.copy(successMessage = msg)
                loadTransactions()
                loadAdminData()
            }.onFailure { err ->
                _uiState.value = _uiState.value.copy(errorMessage = err.message)
            }
            _uiState.value = _uiState.value.copy(isLoading = false)
        }
    }
}
