package com.maxton.bad_android.features.sessions.data.repositories

import com.maxton.bad_android.features.sessions.data.SessionApi
import com.maxton.bad_android.features.sessions.domain.entities.Session
import com.maxton.bad_android.features.sessions.domain.repositories.SessionRepository

class SessionRepositoryImpl(
    private val api: SessionApi,
) : SessionRepository {

    override suspend fun getUpcomingSessions(): Result<List<Session>> = try {
        Result.success(api.getSessions(status = "upcoming").results.map { it.toDomain() })
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun getPastSessions(): Result<List<Session>> = try {
        Result.success(api.getSessions(status = "past").results.map { it.toDomain() })
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun getSessionDetails(sessionId: String): Result<Session> = try {
        Result.success(api.getSessionDetails(sessionId).toDomain())
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun joinSession(sessionId: String): Result<Session> = try {
        Result.success(api.joinSession(sessionId).toDomain())
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun leaveSession(sessionId: String): Result<Session> = try {
        Result.success(api.leaveSession(sessionId).toDomain())
    } catch (e: Exception) {
        Result.failure(e)
    }
}
