package com.maxton.bad_android.features.sessions.domain.entities

data class Participant(
    val name: String,
    val level: String,
    val avatarUrl: String?,
    val isHost: Boolean
)

data class SessionFee(
    val courtFee: Double,
    val shuttlecockFee: Double,
    val totalFee: Double
)

data class Session(
    val id: String,
    val title: String,
    val date: String,
    val time: String,
    val duration: String,
    val location: String,
    val joined: Boolean,
    val participantsCount: Int,
    val maxParticipants: Int,
    val participants: List<Participant>,
    val fee: SessionFee,
    val paymentStatus: String
)
