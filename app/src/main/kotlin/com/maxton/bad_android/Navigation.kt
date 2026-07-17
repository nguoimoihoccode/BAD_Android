package com.maxton.bad_android

import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.maxton.bad_android.features.auth.presentation.AuthUiState
import com.maxton.bad_android.features.auth.presentation.AuthViewModel
import com.maxton.bad_android.features.auth.presentation.LoginScreen
import com.maxton.bad_android.features.auth.presentation.OnboardingScreen
import com.maxton.bad_android.features.auth.presentation.RegisterScreen
import com.maxton.bad_android.features.auth.presentation.WelcomeScreen
import com.maxton.bad_android.features.sessions.presentation.SessionDetailsScreen
import com.maxton.bad_android.features.sessions.presentation.SessionsViewModel
import com.maxton.bad_android.features.payments.presentation.PaymentViewModel
import com.maxton.bad_android.features.community.presentation.CommunityViewModel
import com.maxton.bad_android.features.match.presentation.MatchViewModel
import com.maxton.bad_android.features.match.presentation.MatchCreatorScreen
import com.maxton.bad_android.features.match.presentation.MatchScoreEntryScreen
import com.maxton.bad_android.features.match.presentation.MatchmakingScreen
import com.maxton.bad_android.features.match.presentation.MatchResultScreen
import com.maxton.bad_android.features.notifications.presentation.NotificationsViewModel
import com.maxton.bad_android.features.notifications.presentation.NotificationsScreen
import com.maxton.bad_android.features.admin.presentation.AdminViewModel
import com.maxton.bad_android.features.admin.presentation.MemberManagementScreen
import com.maxton.bad_android.features.admin.presentation.JoinRequestsScreen
import com.maxton.bad_android.features.profile.presentation.DeviceSessionsScreen
import com.maxton.bad_android.features.profile.presentation.DeviceSessionsViewModel
import com.maxton.bad_android.features.profile.presentation.LeaderboardScreen
import com.maxton.bad_android.features.profile.presentation.PlayerProfileDetailScreen
import com.maxton.bad_android.features.profile.presentation.PlayerProfileViewModel
import com.maxton.bad_android.features.profile.presentation.RatePlayerScreen
import com.maxton.bad_android.core.ui.ResponsiveWrapper
import com.maxton.bad_android.ui.main.MainScreen

@Composable
fun MainNavigation() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val sharedPrefs = remember { context.getSharedPreferences("bad_prefs", Context.MODE_PRIVATE) }
    val isFirstTime = remember { sharedPrefs.getBoolean("is_first_time", true) }

    val authViewModel: AuthViewModel = viewModel()
    val sessionsViewModel: SessionsViewModel = viewModel()
    val deviceSessionsViewModel: DeviceSessionsViewModel = viewModel()
    val paymentViewModel: PaymentViewModel = viewModel()
    val communityViewModel: CommunityViewModel = viewModel()
    val matchViewModel: MatchViewModel = viewModel()
    val notificationsViewModel: NotificationsViewModel = viewModel()
    val adminViewModel: AdminViewModel = viewModel()
    val playerProfileViewModel: PlayerProfileViewModel = viewModel()

    val authState by authViewModel.uiState.collectAsState()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    LaunchedEffect(authState, currentRoute, isFirstTime) {
        when (authState) {
            is AuthUiState.Success -> {
                val onAuthEntry = currentRoute == Routes.WELCOME ||
                    currentRoute == Routes.LOGIN ||
                    currentRoute == Routes.REGISTER ||
                    currentRoute == null
                if (!isFirstTime && onAuthEntry) {
                    navController.navigate(Routes.MAIN) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            }
            is AuthUiState.Initial -> {
                val onProtected = currentRoute != null &&
                    currentRoute != Routes.ONBOARDING &&
                    currentRoute != Routes.WELCOME &&
                    currentRoute != Routes.LOGIN &&
                    currentRoute != Routes.REGISTER
                if (onProtected) {
                    navController.navigate(Routes.WELCOME) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            }
            else -> Unit
        }
    }

    if (authState is AuthUiState.Loading && currentRoute == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            CircularProgressIndicator()
        }
        return
    }

    NavHost(
        navController = navController,
        startDestination = if (isFirstTime) Routes.ONBOARDING else Routes.WELCOME
    ) {
        composable(Routes.ONBOARDING) {
            OnboardingScreen(
                onGetStarted = {
                    sharedPrefs.edit().putBoolean("is_first_time", false).apply()
                    navController.navigate(Routes.WELCOME) {
                        popUpTo(Routes.ONBOARDING) { inclusive = true }
                    }
                },
                modifier = Modifier.safeDrawingPadding()
            )
        }
        composable(Routes.WELCOME) {
            WelcomeScreen(
                onSignInClick = { navController.navigate(Routes.LOGIN) },
                onCreateAccountClick = { navController.navigate(Routes.REGISTER) },
                modifier = Modifier.safeDrawingPadding()
            )
        }
        composable(Routes.LOGIN) {
            LoginScreen(
                onBackClick = { navController.popBackStack() },
                onLoginSuccess = {
                    navController.navigate(Routes.MAIN) {
                        popUpTo(Routes.WELCOME) { inclusive = true }
                    }
                },
                viewModel = authViewModel,
                modifier = Modifier.safeDrawingPadding()
            )
        }
        composable(Routes.REGISTER) {
            RegisterScreen(
                onBackClick = { navController.popBackStack() },
                onRegisterSuccess = {
                    navController.navigate(Routes.MAIN) {
                        popUpTo(Routes.WELCOME) { inclusive = true }
                    }
                },
                viewModel = authViewModel,
                modifier = Modifier.safeDrawingPadding()
            )
        }
        composable(Routes.MAIN) {
            ResponsiveWrapper {
                MainScreen(
                    onSessionClick = { sessionId -> navController.navigate(Routes.sessionDetails(sessionId)) },
                    onMatchClick = { matchId -> navController.navigate(Routes.matchScoreEntry(matchId)) },
                    onCreateMatchClick = { navController.navigate(Routes.matchCreator()) },
                    onMatchmakingClick = { navController.navigate(Routes.MATCHMAKING) },
                    onLeaderboardClick = { navController.navigate(Routes.LEADERBOARD) },
                    onPlayerClick = { playerId -> navController.navigate(Routes.playerProfile(playerId)) },
                    onNotificationsClick = { navController.navigate(Routes.NOTIFICATIONS) },
                    onNavigateToMemberManagement = { navController.navigate(Routes.MEMBER_MANAGEMENT) },
                    onNavigateToJoinRequests = { navController.navigate(Routes.JOIN_REQUESTS) },
                    onLogoutSuccess = {
                        navController.navigate(Routes.WELCOME) {
                            popUpTo(Routes.MAIN) { inclusive = true }
                        }
                    },
                    onNavigateToDeviceSessions = { navController.navigate(Routes.DEVICE_SESSIONS) },
                    authViewModel = authViewModel,
                    sessionsViewModel = sessionsViewModel,
                    paymentViewModel = paymentViewModel,
                    communityViewModel = communityViewModel,
                    matchViewModel = matchViewModel,
                    notificationsViewModel = notificationsViewModel,
                    modifier = Modifier.safeDrawingPadding()
                )
            }
        }
        composable(
            route = Routes.SESSION_DETAILS,
            arguments = listOf(navArgument("sessionId") { type = NavType.StringType })
        ) { backStackEntry ->
            val sessionId = backStackEntry.arguments?.getString("sessionId") ?: ""
            SessionDetailsScreen(
                sessionId = sessionId,
                onBackClick = { navController.popBackStack() },
                viewModel = sessionsViewModel,
                modifier = Modifier.safeDrawingPadding()
            )
        }
        composable(Routes.DEVICE_SESSIONS) {
            DeviceSessionsScreen(
                onBackClick = { navController.popBackStack() },
                viewModel = deviceSessionsViewModel,
                modifier = Modifier.safeDrawingPadding()
            )
        }
        composable(
            route = Routes.MATCH_CREATOR,
            arguments = listOf(
                navArgument("opponentId") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val opponentId = backStackEntry.arguments?.getString("opponentId")
            MatchCreatorScreen(
                preselectedOpponentId = opponentId,
                onBackClick = { navController.popBackStack() },
                onMatchCreated = { matchId ->
                    navController.navigate(Routes.matchScoreEntry(matchId)) {
                        popUpTo(Routes.MAIN)
                    }
                },
                viewModel = matchViewModel,
                modifier = Modifier.safeDrawingPadding()
            )
        }
        composable(
            route = Routes.MATCH_SCORE_ENTRY,
            arguments = listOf(navArgument("matchId") { type = NavType.StringType })
        ) { backStackEntry ->
            val matchId = backStackEntry.arguments?.getString("matchId") ?: ""
            MatchScoreEntryScreen(
                matchId = matchId,
                onBackClick = { navController.popBackStack() },
                onScoreSaved = { id ->
                    navController.navigate(Routes.matchResult(id)) {
                        popUpTo(Routes.MAIN)
                    }
                },
                viewModel = matchViewModel,
                modifier = Modifier.safeDrawingPadding()
            )
        }
        composable(Routes.MATCHMAKING) {
            MatchmakingScreen(
                onBackClick = { navController.popBackStack() },
                onChallengeClick = { opponentId ->
                    navController.navigate(Routes.matchCreator(opponentId))
                },
                viewModel = matchViewModel,
                modifier = Modifier.safeDrawingPadding()
            )
        }
        composable(
            route = Routes.MATCH_RESULT,
            arguments = listOf(navArgument("matchId") { type = NavType.StringType })
        ) { backStackEntry ->
            val matchId = backStackEntry.arguments?.getString("matchId") ?: ""
            MatchResultScreen(
                matchId = matchId,
                onBackClick = { navController.popBackStack() },
                onBackHome = {
                    navController.navigate(Routes.MAIN) {
                        popUpTo(Routes.MAIN) { inclusive = true }
                    }
                },
                viewModel = matchViewModel,
                modifier = Modifier.safeDrawingPadding()
            )
        }
        composable(
            route = Routes.PLAYER_PROFILE,
            arguments = listOf(navArgument("playerId") { type = NavType.StringType })
        ) { backStackEntry ->
            val playerId = backStackEntry.arguments?.getString("playerId") ?: ""
            PlayerProfileDetailScreen(
                playerId = playerId,
                onBackClick = { navController.popBackStack() },
                onRateClick = { id -> navController.navigate(Routes.ratePlayer(id)) },
                viewModel = playerProfileViewModel,
                modifier = Modifier.safeDrawingPadding()
            )
        }
        composable(
            route = Routes.RATE_PLAYER,
            arguments = listOf(navArgument("playerId") { type = NavType.StringType })
        ) { backStackEntry ->
            val playerId = backStackEntry.arguments?.getString("playerId") ?: ""
            RatePlayerScreen(
                playerId = playerId,
                onBackClick = { navController.popBackStack() },
                viewModel = playerProfileViewModel,
                modifier = Modifier.safeDrawingPadding()
            )
        }
        composable(Routes.LEADERBOARD) {
            LeaderboardScreen(
                onBackClick = { navController.popBackStack() },
                onPlayerClick = { playerId -> navController.navigate(Routes.playerProfile(playerId)) },
                viewModel = playerProfileViewModel,
                modifier = Modifier.safeDrawingPadding()
            )
        }
        composable(Routes.NOTIFICATIONS) {
            NotificationsScreen(
                onBackClick = { navController.popBackStack() },
                viewModel = notificationsViewModel,
                modifier = Modifier.safeDrawingPadding()
            )
        }
        composable(Routes.MEMBER_MANAGEMENT) {
            MemberManagementScreen(
                onBackClick = { navController.popBackStack() },
                viewModel = adminViewModel,
                modifier = Modifier.safeDrawingPadding()
            )
        }
        composable(Routes.JOIN_REQUESTS) {
            JoinRequestsScreen(
                onBackClick = { navController.popBackStack() },
                viewModel = adminViewModel,
                modifier = Modifier.safeDrawingPadding()
            )
        }
    }
}
