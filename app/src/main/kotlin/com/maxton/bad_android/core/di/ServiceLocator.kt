package com.maxton.bad_android.core.di

import android.content.Context
import com.maxton.bad_android.core.network.ApiModule
import com.maxton.bad_android.core.network.AuthTokenStore
import com.maxton.bad_android.features.auth.data.AuthApi
import com.maxton.bad_android.features.auth.data.repositories.AuthRepositoryImpl
import com.maxton.bad_android.features.auth.domain.repositories.AuthRepository
import com.maxton.bad_android.features.sessions.data.SessionApi
import com.maxton.bad_android.features.sessions.data.repositories.SessionRepositoryImpl
import com.maxton.bad_android.features.sessions.domain.repositories.SessionRepository

object ServiceLocator {
    private lateinit var appContext: Context

    fun init(context: Context) {
        appContext = context.applicationContext
    }

    private val tokenStore: AuthTokenStore by lazy {
        ApiModule.createTokenStore(appContext)
    }

    private val retrofit by lazy {
        ApiModule.createRetrofit(ApiModule.createOkHttpClient(tokenStore))
    }

    private val authApi: AuthApi by lazy {
        retrofit.create(AuthApi::class.java)
    }

    private val sessionApi: SessionApi by lazy {
        retrofit.create(SessionApi::class.java)
    }

    val authRepository: AuthRepository by lazy {
        AuthRepositoryImpl(authApi, tokenStore)
    }

    val sessionRepository: SessionRepository by lazy {
        SessionRepositoryImpl(sessionApi)
    }

    val paymentRepository: com.maxton.bad_android.features.payments.domain.repositories.PaymentRepository by lazy {
        com.maxton.bad_android.features.payments.data.repositories.PaymentRepositoryImpl()
    }

    val communityRepository: com.maxton.bad_android.features.community.domain.repositories.CommunityRepository by lazy {
        com.maxton.bad_android.features.community.data.repositories.CommunityRepositoryImpl()
    }

    val matchRepository: com.maxton.bad_android.features.match.domain.repositories.MatchRepository by lazy {
        com.maxton.bad_android.features.match.data.repositories.MatchRepositoryImpl()
    }

    val notificationRepository: com.maxton.bad_android.features.notifications.domain.repositories.NotificationRepository by lazy {
        com.maxton.bad_android.features.notifications.data.repositories.NotificationRepositoryImpl()
    }

    val adminRepository: com.maxton.bad_android.features.admin.domain.repositories.AdminRepository by lazy {
        com.maxton.bad_android.features.admin.data.repositories.AdminRepositoryImpl()
    }

    val playerRepository: com.maxton.bad_android.features.profile.domain.repositories.PlayerRepository by lazy {
        com.maxton.bad_android.features.profile.data.repositories.PlayerRepositoryImpl()
    }
}
