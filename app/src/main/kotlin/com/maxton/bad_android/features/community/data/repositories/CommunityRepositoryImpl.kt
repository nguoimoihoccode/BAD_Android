package com.maxton.bad_android.features.community.data.repositories

import com.maxton.bad_android.features.community.domain.entities.*
import com.maxton.bad_android.features.community.domain.repositories.CommunityRepository

class CommunityRepositoryImpl : CommunityRepository {
    private val announcements = listOf(
        CommunityAnnouncement(
            id = "announce-1",
            title = "Summer Finals: Registration now open!",
            imageUrl = "https://images.unsplash.com/photo-1560089000-7433a4ebbd64",
            tag = "Tournament"
        )
    )

    private val members = listOf(
        CommunityMember("m1", "Sarah", "https://example.com/a.png", true, "Gold", "Elite Active", 1200),
        CommunityMember("m2", "Marcus", "https://example.com/b.png", true, "Silver", "Expert Player", 980),
        CommunityMember("m3", "Trần Anh Tuấn", "https://example.com/c.png", true, "Gold", "Champion", 1500),
        CommunityMember("m4", "Lê Minh Hạnh", "https://example.com/d.png", false, "Bronze", "Intermediate", 600),
        CommunityMember("m5", "Nguyễn Quốc Cường", "https://example.com/e.png", false, null, "Regular", 450)
    )

    private var poll = CommunityPoll(
        id = "poll-1",
        question = "Which venue for the summer tournament?",
        options = listOf(
            PollOption("opt-1", "City Arena", 38.0),
            PollOption("opt-2", "Eastside Sports Center", 42.0),
            PollOption("opt-3", "Kinetic Badminton Court", 20.0)
        ),
        totalVotes = 128,
        daysLeft = 2,
        selectedOptionId = null
    )

    private val messages = mutableListOf(
        ChatMessage("msg-1", "Sarah", "https://example.com/a.png", "Who is up for a session this Tuesday?"),
        ChatMessage("msg-2", "Marcus", "https://example.com/b.png", "I'm in! Let's book Court 3.")
    )

    override suspend fun getAnnouncements(): Result<List<CommunityAnnouncement>> = Result.success(announcements)

    override suspend fun getActiveMembers(): Result<List<CommunityMember>> = Result.success(members.filter { it.isOnline })

    override suspend fun getTopPlayers(): Result<List<CommunityMember>> = Result.success(members.sortedByDescending { it.points ?: 0 })

    override suspend fun getPoll(): Result<CommunityPoll> = Result.success(poll)

    override suspend fun getRecentMessages(): Result<List<ChatMessage>> = Result.success(messages.toList())

    override suspend fun voteInPoll(optionId: String): Result<CommunityPoll> {
        if (poll.selectedOptionId != null) return Result.success(poll)
        
        val updatedOptions = poll.options.map { option ->
            if (option.id == optionId) {
                option.copy(votesPercent = option.votesPercent + 1.0)
            } else {
                option
            }
        }
        
        poll = poll.copy(
            options = updatedOptions,
            totalVotes = poll.totalVotes + 1,
            selectedOptionId = optionId
        )
        return Result.success(poll)
    }

    override suspend fun sendChatMessage(text: String): Result<ChatMessage> {
        val newMsg = ChatMessage(
            id = "msg-${System.currentTimeMillis()}",
            senderName = "CurrentUser",
            senderAvatarUrl = "https://example.com/avatar.png",
            text = text
        )
        messages.add(newMsg)
        return Result.success(newMsg)
    }
}
