package com.maxton.bad_android.features.auth.domain.entities

data class User(
    val id: String,
    val shortId: String,
    val email: String,
    val username: String,
    val fullName: String? = null,
    val phone: String? = null,
    val avatarUrl: String? = null,
)
