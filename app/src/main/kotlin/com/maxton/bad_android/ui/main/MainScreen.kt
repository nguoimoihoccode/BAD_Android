package com.maxton.bad_android.ui.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Dashboard
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.SportsTennis
import androidx.compose.material.icons.rounded.Payment
import androidx.compose.material.icons.rounded.Forum
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.maxton.bad_android.features.auth.presentation.AuthUiState
import com.maxton.bad_android.features.auth.presentation.AuthViewModel
import com.maxton.bad_android.features.sessions.presentation.DashboardScreen
import com.maxton.bad_android.features.sessions.presentation.SessionsScreen
import com.maxton.bad_android.features.sessions.presentation.SessionsViewModel
import com.maxton.bad_android.features.payments.presentation.PaymentViewModel
import com.maxton.bad_android.features.payments.presentation.PaymentsScreen
import com.maxton.bad_android.features.community.presentation.CommunityViewModel
import com.maxton.bad_android.features.community.presentation.CommunityScreen
import com.maxton.bad_android.features.match.presentation.MatchViewModel
import com.maxton.bad_android.features.notifications.presentation.NotificationsUiState
import com.maxton.bad_android.features.notifications.presentation.NotificationsViewModel
import com.maxton.bad_android.features.profile.presentation.PlayerProfileScreen
import com.maxton.bad_android.theme.KineticGreen

@Composable
fun MainScreen(
    onSessionClick: (String) -> Unit,
    onMatchClick: (String) -> Unit,
    onCreateMatchClick: () -> Unit,
    onMatchmakingClick: () -> Unit = {},
    onLeaderboardClick: () -> Unit = {},
    onPlayerClick: (String) -> Unit = {},
    onNotificationsClick: () -> Unit,
    onNavigateToMemberManagement: () -> Unit,
    onNavigateToJoinRequests: () -> Unit,
    onLogoutSuccess: () -> Unit,
    onNavigateToDeviceSessions: () -> Unit,
    authViewModel: AuthViewModel,
    sessionsViewModel: SessionsViewModel,
    paymentViewModel: PaymentViewModel,
    communityViewModel: CommunityViewModel,
    matchViewModel: MatchViewModel,
    notificationsViewModel: NotificationsViewModel,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val authState by authViewModel.uiState.collectAsState()
    val notificationsState by notificationsViewModel.uiState.collectAsState()
    val userName = when (val state = authState) {
        is AuthUiState.Success -> state.user.fullName?.takeIf { it.isNotBlank() } ?: state.user.username
        else -> "Player"
    }
    val hasUnread = (notificationsState as? NotificationsUiState.Success)
        ?.notifications
        ?.any { !it.isRead }
        ?: false

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = KineticGreen
            ) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(Icons.Rounded.Dashboard, contentDescription = "Dashboard") },
                    label = { Text("Dashboard", fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = KineticGreen, selectedTextColor = KineticGreen)
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Icon(Icons.Rounded.SportsTennis, contentDescription = "Sessions") },
                    label = { Text("Sessions", fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = KineticGreen, selectedTextColor = KineticGreen)
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = { Icon(Icons.Rounded.Payment, contentDescription = "Payments") },
                    label = { Text("Payments", fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = KineticGreen, selectedTextColor = KineticGreen)
                )
                NavigationBarItem(
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    icon = { Icon(Icons.Rounded.Forum, contentDescription = "Community") },
                    label = { Text("Community", fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = KineticGreen, selectedTextColor = KineticGreen)
                )
                NavigationBarItem(
                    selected = selectedTab == 4,
                    onClick = { selectedTab = 4 },
                    icon = { Icon(Icons.Rounded.Person, contentDescription = "Profile") },
                    label = { Text("Profile", fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = KineticGreen, selectedTextColor = KineticGreen)
                )
            }
        },
        modifier = modifier
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (selectedTab) {
                0 -> DashboardScreen(
                    userName = userName,
                    hasUnreadNotifications = hasUnread,
                    onNavigateToSessions = { selectedTab = 1 },
                    onNavigateToProfile = { selectedTab = 4 },
                    onNavigateToMatches = onMatchmakingClick,
                    onNavigateToCommunity = { selectedTab = 3 },
                    onNavigateToPayments = { selectedTab = 2 },
                    onNotificationsClick = onNotificationsClick,
                    onLeaderboardClick = onLeaderboardClick,
                    onMatchmakingClick = onMatchmakingClick,
                    onCreateMatchClick = onCreateMatchClick,
                )
                1 -> SessionsScreen(
                    onSessionClick = onSessionClick,
                    viewModel = sessionsViewModel
                )
                2 -> PaymentsScreen(
                    viewModel = paymentViewModel
                )
                3 -> CommunityScreen(
                    viewModel = communityViewModel,
                    onPlayerClick = onPlayerClick,
                    onLeaderboardClick = onLeaderboardClick,
                    onMembersClick = onNavigateToMemberManagement,
                    onJoinRequestsClick = onNavigateToJoinRequests,
                )
                4 -> PlayerProfileScreen(
                    onNavigateToDeviceSessions = onNavigateToDeviceSessions,
                    onNavigateToMemberManagement = onNavigateToMemberManagement,
                    onNavigateToJoinRequests = onNavigateToJoinRequests,
                    onLogoutSuccess = onLogoutSuccess,
                    authViewModel = authViewModel
                )
            }
        }
    }
}
