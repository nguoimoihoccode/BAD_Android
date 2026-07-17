package com.maxton.bad_android.features.match.data.repositories

import com.maxton.bad_android.features.match.domain.entities.MatchItem
import com.maxton.bad_android.features.match.domain.entities.OpponentCandidate
import com.maxton.bad_android.features.match.domain.repositories.MatchRepository
import kotlinx.coroutines.delay

class MatchRepositoryImpl : MatchRepository {
    private val mockOpponents = listOf(
        OpponentCandidate(
            id = "m1",
            fullName = "Trần Nguyễn Tiến",
            username = "tientran",
            avatarUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=150&q=80",
            skillLevel = "Advanced",
            compatibilityScore = 98,
        ),
        OpponentCandidate(
            id = "m3",
            fullName = "Lê Hoài Nam",
            username = "namle",
            avatarUrl = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?auto=format&fit=crop&w=150&q=80",
            skillLevel = "Intermediate",
            compatibilityScore = 85,
        ),
        OpponentCandidate(
            id = "m4",
            fullName = "Phạm Minh Trí",
            username = "tripham",
            avatarUrl = "https://images.unsplash.com/photo-1492562080023-ab3db95bfbce?auto=format&fit=crop&w=150&q=80",
            skillLevel = "Beginner",
            compatibilityScore = 60,
        ),
    )

    private val matches = mutableListOf(
        MatchItem(
            id = "match_init",
            player1Name = "Nguyễn Thức Phúc",
            player2Name = "Trần Nguyễn Tiến",
            player1Avatar = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&w=150&q=80",
            player2Avatar = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=150&q=80",
            setScores = listOf(
                mapOf("p1" to 21, "p2" to 18),
                mapOf("p1" to 15, "p2" to 21),
                mapOf("p1" to 21, "p2" to 19),
            ),
            date = System.currentTimeMillis() - 24 * 60 * 60 * 1000L,
            courtName = "City Arena • Court 2",
            isCompleted = true,
        ),
    )

    override suspend fun findMatchmakingOpponents(): Result<List<OpponentCandidate>> {
        delay(600)
        return Result.success(mockOpponents)
    }

    override suspend fun createMatch(
        opponentId: String,
        courtName: String,
        dateTimeMillis: Long,
    ): Result<MatchItem> {
        delay(500)
        val opp = mockOpponents.firstOrNull { it.id == opponentId }
            ?: return Result.failure(Exception("Opponent not found."))

        val newMatch = MatchItem(
            id = "match_${System.currentTimeMillis()}",
            player1Name = "Nguyễn Thức Phúc",
            player2Name = opp.fullName,
            player1Avatar = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&w=150&q=80",
            player2Avatar = opp.avatarUrl,
            setScores = emptyList(),
            date = dateTimeMillis,
            courtName = courtName,
            isCompleted = false,
        )
        matches.add(newMatch)
        return Result.success(newMatch)
    }

    override suspend fun enterMatchScore(
        matchId: String,
        setScores: List<Map<String, Int>>,
    ): Result<Unit> {
        delay(400)
        val index = matches.indexOfFirst { it.id == matchId }
        if (index == -1) {
            return Result.failure(Exception("Match not found."))
        }
        matches[index] = matches[index].copyWith(
            setScores = setScores,
            isCompleted = true,
        )
        return Result.success(Unit)
    }

    override suspend fun getMatchDetail(matchId: String): Result<MatchItem> {
        delay(300)
        val match = matches.firstOrNull { it.id == matchId } ?: matches.first()
        return Result.success(match)
    }

    override suspend fun getScheduledMatches(): Result<List<MatchItem>> {
        delay(200)
        return Result.success(matches.filter { !it.isCompleted })
    }

    override suspend fun getCompletedMatches(): Result<List<MatchItem>> {
        delay(200)
        return Result.success(matches.filter { it.isCompleted })
    }
}
