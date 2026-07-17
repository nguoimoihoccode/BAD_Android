package com.maxton.bad_android.features.payments.domain.entities

data class Invoice(
    val id: String,
    val title: String,
    val date: String,
    val courtInfo: String,
    val amount: Double,
    val status: String // "PENDING", "PAID"
)
