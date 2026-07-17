package com.maxton.bad_android.features.auth.data.repositories

import com.maxton.bad_android.core.network.AuthTokenStore
import com.maxton.bad_android.features.auth.data.AuthApi
import com.maxton.bad_android.features.auth.data.dto.LoginRequest
import com.maxton.bad_android.features.auth.data.dto.LogoutRequest
import com.maxton.bad_android.features.auth.data.dto.RegisterRequest
import com.maxton.bad_android.features.auth.domain.entities.DeviceSession
import com.maxton.bad_android.features.auth.domain.entities.User
import com.maxton.bad_android.features.auth.domain.repositories.AuthRepository

class AuthRepositoryImpl(
    private val api: AuthApi,
    private val tokenStore: AuthTokenStore,
) : AuthRepository {

    override suspend fun login(email: String, password: String): Result<User> = try {
        val res = api.login(LoginRequest(email = email, password = password))
        tokenStore.saveTokens(res.access, res.refresh)
        Result.success(res.user.toDomain())
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun register(
        email: String,
        username: String,
        password: String,
        fullName: String?,
    ): Result<User> = try {
        val res = api.register(
            RegisterRequest(
                email = email,
                username = username,
                password = password,
                fullName = fullName,
            ),
        )
        tokenStore.saveTokens(res.access, res.refresh)
        Result.success(res.user.toDomain())
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun logout(): Result<Unit> {
        return try {
            val refresh = tokenStore.getRefreshToken().orEmpty()
            if (refresh.isNotBlank()) {
                api.logout(LogoutRequest(refresh = refresh))
            }
            tokenStore.clear()
            Result.success(Unit)
        } catch (e: Exception) {
            tokenStore.clear()
            Result.failure(e)
        }
    }

    override suspend fun getMe(): Result<User> = try {
        Result.success(api.getMe().toDomain())
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun getDeviceSessions(): Result<List<DeviceSession>> = try {
        Result.success(api.getDeviceSessions().results.map { it.toDomain() })
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun revokeSession(sessionId: String): Result<Unit> = try {
        api.revokeSession(sessionId)
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun isAuthenticated(): Boolean = tokenStore.hasTokens()
}
