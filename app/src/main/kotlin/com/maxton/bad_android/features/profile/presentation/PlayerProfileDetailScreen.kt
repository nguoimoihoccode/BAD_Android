package com.maxton.bad_android.features.profile.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.PhoneIphone
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.StarBorder
import androidx.compose.material.icons.rounded.StarHalf
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maxton.bad_android.features.profile.domain.entities.PlayerProfile
import com.maxton.bad_android.theme.KineticGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerProfileDetailScreen(
    playerId: String,
    onBackClick: () -> Unit,
    onRateClick: (String) -> Unit,
    viewModel: PlayerProfileViewModel,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(playerId) {
        viewModel.loadPlayerProfile(playerId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Player Profile", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Rounded.ArrowBackIosNew, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                ),
            )
        },
        modifier = modifier,
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding),
        ) {
            when (val uiState = state) {
                PlayerProfileUiState.Loading,
                PlayerProfileUiState.RatingSuccess,
                -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = KineticGreen)
                    }
                }
                is PlayerProfileUiState.Error -> {
                    Box(
                        modifier = Modifier.fillMaxSize().padding(16.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(uiState.message, color = MaterialTheme.colorScheme.error)
                    }
                }
                is PlayerProfileUiState.ProfileLoaded -> {
                    ProfileBody(
                        profile = uiState.profile,
                        onRateClick = { onRateClick(uiState.profile.id) },
                    )
                }
                is PlayerProfileUiState.LeaderboardLoaded -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = KineticGreen)
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfileBody(
    profile: PlayerProfile,
    onRateClick: () -> Unit,
) {
    val winRate = if (profile.matchesPlayed > 0) {
        ((profile.wins.toDouble() / profile.matchesPlayed) * 100).toInt()
    } else {
        0
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .size(96.dp)
                .clip(CircleShape)
                .background(KineticGreen.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = profile.fullName.firstOrNull()?.uppercase() ?: "?",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = KineticGreen,
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text(profile.fullName, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            "@${profile.username}",
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Surface(
            shape = RoundedCornerShape(50),
            color = KineticGreen.copy(alpha = 0.1f),
        ) {
            Text(
                text = profile.skillLevel.uppercase(),
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = KineticGreen,
                letterSpacing = 0.5.sp,
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceAround,
            ) {
                StatBox("Matches", profile.matchesPlayed.toString())
                StatBox("Wins", profile.wins.toString(), KineticGreen)
                StatBox("Losses", profile.losses.toString(), MaterialTheme.colorScheme.error)
                StatBox("Win Rate", "$winRate%")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        SectionTitle("Community Ratings")
        Spacer(modifier = Modifier.height(8.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                RatingRow("Skill Rating", profile.skillRating)
                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                RatingRow("Fairplay Rating", profile.fairPlayRating)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        SectionTitle("Bio")
        Spacer(modifier = Modifier.height(8.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        ) {
            Text(
                text = profile.bio,
                modifier = Modifier.padding(16.dp),
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 18.sp,
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
        SectionTitle("Contact Information")
        Spacer(modifier = Modifier.height(8.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                ContactRow(Icons.Rounded.Email, profile.email)
                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                ContactRow(Icons.Rounded.PhoneIphone, profile.phone)
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = onRateClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = KineticGreen),
            shape = RoundedCornerShape(12.dp),
        ) {
            Text("Rate Player Performance", fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        modifier = Modifier.fillMaxWidth(),
        fontSize = 15.sp,
        fontWeight = FontWeight.Bold,
    )
}

@Composable
private fun StatBox(label: String, value: String, color: Color = MaterialTheme.colorScheme.onSurface) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = color)
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun RatingRow(label: String, rating: Double) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(label, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.weight(1f))
        repeat(5) { index ->
            val remaining = rating - index
            val icon = when {
                remaining >= 1 -> Icons.Rounded.Star
                remaining > 0 -> Icons.Rounded.StarHalf
                else -> Icons.Rounded.StarBorder
            }
            Icon(icon, contentDescription = null, tint = Color(0xFFFFC107), modifier = Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.width(6.dp))
        Text(rating.toString(), fontSize = 13.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun ContactRow(icon: androidx.compose.ui.graphics.vector.ImageVector, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = KineticGreen, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Text(value, fontSize = 13.sp, fontWeight = FontWeight.Medium)
    }
}
