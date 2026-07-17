package com.maxton.bad_android.features.sessions.data

import com.maxton.bad_android.features.sessions.data.dto.SessionDto
import com.maxton.bad_android.features.sessions.data.dto.SessionListDto
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface SessionApi {
    @GET("sessions")
    suspend fun getSessions(@Query("status") status: String): SessionListDto

    @GET("sessions/{id}")
    suspend fun getSessionDetails(@Path("id") id: String): SessionDto

    @POST("sessions/{id}/join")
    suspend fun joinSession(@Path("id") id: String): SessionDto

    @POST("sessions/{id}/leave")
    suspend fun leaveSession(@Path("id") id: String): SessionDto
}
