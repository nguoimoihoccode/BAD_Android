package com.maxton.bad_android.features.profile.domain.repositories

import com.maxton.bad_android.features.profile.domain.entities.PlayerProfile

interface PlayerRepository {
    suspend fun getPlayerProfile(playerId: String): Result<PlayerProfile>
    suspend fun ratePlayer(playerId: String, skillRating: Double, fairPlayRating: Double): Result<Unit>
    suspend fun getLeaderboard(): Result<List<PlayerProfile>>
}
