package com.maxton.bad_android.features.community.domain.entities

data class CommunityAnnouncement(
    val id: String,
    val title: String,
    val imageUrl: String,
    val tag: String
)

data class CommunityMember(
    val id: String,
    val name: String,
    val avatarUrl: String,
    val isOnline: Boolean,
    val badge: String? = null,
    val activity: String? = null,
    val points: Int? = null
)

data class PollOption(
    val id: String,
    val text: String,
    val votesPercent: Double
)

data class CommunityPoll(
    val id: String,
    val question: String,
    val options: List<PollOption>,
    val totalVotes: Int,
    val daysLeft: Int,
    val selectedOptionId: String? = null
)

data class ChatMessage(
    val id: String,
    val senderName: String,
    val senderAvatarUrl: String,
    val text: String
)
