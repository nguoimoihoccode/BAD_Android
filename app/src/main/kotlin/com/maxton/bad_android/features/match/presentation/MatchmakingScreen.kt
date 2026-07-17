package com.maxton.bad_android.features.match.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material.icons.rounded.SportsTennis
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maxton.bad_android.features.match.domain.entities.OpponentCandidate
import com.maxton.bad_android.theme.KineticGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchmakingScreen(
    onBackClick: () -> Unit,
    onChallengeClick: (String) -> Unit,
    viewModel: MatchViewModel,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.matchmakingState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadMatchmakingCandidates()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Matchmaking", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Rounded.ArrowBackIosNew, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                ),
            )
        },
        modifier = modifier,
    ) { innerPadding ->
        when (val uiState = state) {
            MatchmakingUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(color = KineticGreen)
                }
            }
            is MatchmakingUiState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(16.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(uiState.message, color = MaterialTheme.colorScheme.error)
                }
            }
            is MatchmakingUiState.Success -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                        .padding(innerPadding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(70.dp)
                                    .clip(CircleShape)
                                    .background(KineticGreen),
                                contentAlignment = Alignment.Center,
                            ) {
                                Icon(
                                    Icons.Rounded.SportsTennis,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(32.dp),
                                )
                            }
                        }
                    }
                    item {
                        Text(
                            text = "Recommended Opponents",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                    items(uiState.candidates) { opp ->
                        OpponentCard(
                            opponent = opp,
                            onChallenge = { onChallengeClick(opp.id) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun OpponentCard(
    opponent: OpponentCandidate,
    onChallenge: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val isCompatible = opponent.compatibilityScore >= 80
    val compatColor = if (isCompatible) KineticGreen else MaterialTheme.colorScheme.secondary

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(KineticGreen.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = opponent.fullName.firstOrNull()?.uppercase() ?: "?",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = KineticGreen,
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = opponent.fullName,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = "@${opponent.username} • ${opponent.skillLevel}",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    LinearProgressIndicator(
                        progress = { opponent.compatibilityScore / 100f },
                        modifier = Modifier
                            .width(80.dp)
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = compatColor,
                        trackColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f),
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${opponent.compatibilityScore}% match",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = compatColor,
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = onChallenge,
                colors = ButtonDefaults.buttonColors(containerColor = KineticGreen),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                modifier = Modifier.height(36.dp),
            ) {
                Text("Challenge", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }
}
