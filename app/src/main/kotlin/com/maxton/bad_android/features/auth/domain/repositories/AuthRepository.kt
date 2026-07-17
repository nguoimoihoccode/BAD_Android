package com.maxton.bad_android.features.auth.domain.repositories

import com.maxton.bad_android.features.auth.domain.entities.User
import com.maxton.bad_android.features.auth.domain.entities.DeviceSession

interface AuthRepository {
    suspend fun login(email: String, password: String): Result<User>
    suspend fun register(email: String, username: String, password: String, fullName: String?): Result<User>
    suspend fun logout(): Result<Unit>
    suspend fun getMe(): Result<User>
    suspend fun getDeviceSessions(): Result<List<DeviceSession>>
    suspend fun revokeSession(sessionId: String): Result<Unit>
    suspend fun isAuthenticated(): Boolean
}
