package com.maxton.bad_android.features.auth.data

import com.maxton.bad_android.features.auth.data.dto.AuthResponseDto
import com.maxton.bad_android.features.auth.data.dto.DeviceSessionListDto
import com.maxton.bad_android.features.auth.data.dto.LoginRequest
import com.maxton.bad_android.features.auth.data.dto.LogoutRequest
import com.maxton.bad_android.features.auth.data.dto.RegisterRequest
import com.maxton.bad_android.features.auth.data.dto.UserDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface AuthApi {
    @POST("auth/login")
    suspend fun login(@Body body: LoginRequest): AuthResponseDto

    @POST("auth/register")
    suspend fun register(@Body body: RegisterRequest): AuthResponseDto

    @POST("auth/logout")
    suspend fun logout(@Body body: LogoutRequest)

    @GET("me")
    suspend fun getMe(): UserDto

    @GET("me/sessions")
    suspend fun getDeviceSessions(): DeviceSessionListDto

    @DELETE("me/sessions/{id}")
    suspend fun revokeSession(@Path("id") id: String)
}
