package com.maxton.bad_android.features.payments.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maxton.bad_android.core.di.ServiceLocator
import com.maxton.bad_android.features.payments.domain.entities.Invoice
import com.maxton.bad_android.features.payments.domain.repositories.PaymentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed interface PaymentUiState {
    object Loading : PaymentUiState
    data class Success(
        val outstandingInvoices: List<Invoice>,
        val paymentHistory: List<Invoice>,
        val totalBalance: Double
    ) : PaymentUiState
    data class Error(val message: String) : PaymentUiState
}

class PaymentViewModel(
    private val paymentRepository: PaymentRepository = ServiceLocator.paymentRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<PaymentUiState>(PaymentUiState.Loading)
    val uiState: StateFlow<PaymentUiState> = _uiState

    init {
        loadPayments()
    }

    fun loadPayments() {
        viewModelScope.launch {
            _uiState.value = PaymentUiState.Loading
            val outstandingResult = paymentRepository.getOutstandingFees()
            val historyResult = paymentRepository.getPaymentHistory()

            if (outstandingResult.isSuccess && historyResult.isSuccess) {
                val outstanding = outstandingResult.getOrThrow()
                val history = historyResult.getOrThrow()
                val balance = outstanding.sumOf { it.amount }
                _uiState.value = PaymentUiState.Success(outstanding, history, balance)
            } else {
                val errMsg = outstandingResult.exceptionOrNull()?.message ?: historyResult.exceptionOrNull()?.message ?: "Failed to load payments"
                _uiState.value = PaymentUiState.Error(errMsg)
            }
        }
    }

    fun payInvoice(invoiceId: String) {
        viewModelScope.launch {
            _uiState.value = PaymentUiState.Loading
            paymentRepository.payInvoice(invoiceId).fold(
                onSuccess = { loadPayments() },
                onFailure = { _uiState.value = PaymentUiState.Error(it.message ?: "Payment failed") }
            )
        }
    }
}
