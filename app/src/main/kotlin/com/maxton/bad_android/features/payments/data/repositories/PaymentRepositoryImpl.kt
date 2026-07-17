package com.maxton.bad_android.features.payments.data.repositories

import com.maxton.bad_android.features.payments.domain.entities.Invoice
import com.maxton.bad_android.features.payments.domain.repositories.PaymentRepository

class PaymentRepositoryImpl : PaymentRepository {
    private val outstanding = mutableListOf(
        Invoice("invoice-1", "Tuesday Night Lights", "Oct 24", "Court 3 • Advanced", 150000.0, "PENDING")
    )

    private val history = mutableListOf(
        Invoice("invoice-2", "Weekend Smashfest", "Oct 21, 2023", "Court 1 • Intermediate", 125000.0, "PAID"),
        Invoice("invoice-3", "Drill Session: Backhand", "Oct 18, 2023", "Court 2 • All Levels", 200000.0, "PAID"),
        Invoice("invoice-4", "Mixed Doubles Open", "Oct 14, 2023", "Court 4 • Mixed", 150000.0, "PAID")
    )

    override suspend fun getOutstandingFees(): Result<List<Invoice>> {
        return Result.success(outstanding.toList())
    }

    override suspend fun getPaymentHistory(): Result<List<Invoice>> {
        return Result.success(history.toList())
    }

    override suspend fun payInvoice(invoiceId: String): Result<Unit> {
        val index = outstanding.indexOfFirst { it.id == invoiceId }
        if (index != -1) {
            val invoice = outstanding.removeAt(index)
            val paidInvoice = invoice.copy(
                status = "PAID",
                date = "Today"
            )
            history.add(0, paidInvoice)
            return Result.success(Unit)
        }
        return Result.failure(Exception("Invoice not found"))
    }
}
