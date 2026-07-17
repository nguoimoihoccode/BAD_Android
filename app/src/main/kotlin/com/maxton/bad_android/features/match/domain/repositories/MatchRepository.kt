package com.maxton.bad_android.features.match.domain.repositories

import com.maxton.bad_android.features.match.domain.entities.MatchItem
import com.maxton.bad_android.features.match.domain.entities.OpponentCandidate

interface MatchRepository {
    suspend fun findMatchmakingOpponents(): Result<List<OpponentCandidate>>
    suspend fun createMatch(
        opponentId: String,
        courtName: String,
        dateTimeMillis: Long,
    ): Result<MatchItem>
    suspend fun enterMatchScore(
        matchId: String,
        setScores: List<Map<String, Int>>,
    ): Result<Unit>
    suspend fun getMatchDetail(matchId: String): Result<MatchItem>
    suspend fun getScheduledMatches(): Result<List<MatchItem>>
    suspend fun getCompletedMatches(): Result<List<MatchItem>>
}
