package com.maxton.bad_android.features.sessions.domain.repositories

import com.maxton.bad_android.features.sessions.domain.entities.Session

interface SessionRepository {
    suspend fun getUpcomingSessions(): Result<List<Session>>
    suspend fun getPastSessions(): Result<List<Session>>
    suspend fun getSessionDetails(sessionId: String): Result<Session>
    suspend fun joinSession(sessionId: String): Result<Session>
    suspend fun leaveSession(sessionId: String): Result<Session>
}
