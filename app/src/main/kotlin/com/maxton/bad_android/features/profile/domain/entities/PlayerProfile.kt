package com.maxton.bad_android.features.profile.domain.entities

data class PlayerProfile(
    val id: String,
    val fullName: String,
    val username: String,
    val avatarUrl: String? = null,
    val skillLevel: String,
    val bio: String,
    val matchesPlayed: Int,
    val wins: Int,
    val losses: Int,
    val fairPlayRating: Double,
    val skillRating: Double,
    val email: String,
    val phone: String,
)
