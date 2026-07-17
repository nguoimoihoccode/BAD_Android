package com.maxton.bad_android.features.payments.domain.repositories

import com.maxton.bad_android.features.payments.domain.entities.Invoice

interface PaymentRepository {
    suspend fun getOutstandingFees(): Result<List<Invoice>>
    suspend fun getPaymentHistory(): Result<List<Invoice>>
    suspend fun payInvoice(invoiceId: String): Result<Unit>
}
