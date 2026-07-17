package com.maxton.bad_android.features.community.domain.repositories

import com.maxton.bad_android.features.community.domain.entities.*

interface CommunityRepository {
    suspend fun getAnnouncements(): Result<List<CommunityAnnouncement>>
    suspend fun getActiveMembers(): Result<List<CommunityMember>>
    suspend fun getTopPlayers(): Result<List<CommunityMember>>
    suspend fun getPoll(): Result<CommunityPoll>
    suspend fun getRecentMessages(): Result<List<ChatMessage>>
    suspend fun voteInPoll(optionId: String): Result<CommunityPoll>
    suspend fun sendChatMessage(text: String): Result<ChatMessage>
}
