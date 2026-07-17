package com.maxton.bad_android.features.sessions.data.dto

import com.maxton.bad_android.features.sessions.domain.entities.Participant
import com.maxton.bad_android.features.sessions.domain.entities.Session
import com.maxton.bad_android.features.sessions.domain.entities.SessionFee
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ParticipantDto(
    val name: String,
    val level: String,
    @SerialName("avatar_url") val avatarUrl: String? = null,
    @SerialName("is_host") val isHost: Boolean = false,
) {
    fun toDomain() = Participant(
        name = name,
        level = level,
        avatarUrl = avatarUrl,
        isHost = isHost,
    )
}

@Serializable
data class SessionFeeDto(
    @SerialName("court_fee") val courtFee: Double,
    @SerialName("shuttlecock_fee") val shuttlecockFee: Double,
    @SerialName("total_fee") val totalFee: Double,
) {
    fun toDomain() = SessionFee(
        courtFee = courtFee,
        shuttlecockFee = shuttlecockFee,
        totalFee = totalFee,
    )
}

@Serializable
data class SessionDto(
    val id: String,
    val title: String,
    val date: String,
    val time: String,
    val duration: String,
    val location: String,
    val joined: Boolean,
    @SerialName("participants_count") val participantsCount: Int = 0,
    @SerialName("max_participants") val maxParticipants: Int = 0,
    val participants: List<ParticipantDto> = emptyList(),
    val fee: SessionFeeDto,
    @SerialName("payment_status") val paymentStatus: String = "UNPAID",
) {
    fun toDomain() = Session(
        id = id,
        title = title,
        date = date,
        time = time,
        duration = duration,
        location = location,
        joined = joined,
        participantsCount = participantsCount,
        maxParticipants = maxParticipants,
        participants = participants.map { it.toDomain() },
        fee = fee.toDomain(),
        paymentStatus = paymentStatus,
    )
}

@Serializable
data class SessionListDto(
    val results: List<SessionDto> = emptyList(),
)
