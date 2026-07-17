package com.maxton.bad_android.features.match.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maxton.bad_android.features.match.domain.entities.MatchItem
import com.maxton.bad_android.features.match.domain.entities.p1
import com.maxton.bad_android.features.match.domain.entities.p2
import com.maxton.bad_android.theme.KineticGreen
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun MatchesScreen(
    onMatchClick: (String) -> Unit,
    onCreateMatchClick: () -> Unit,
    viewModel: MatchViewModel,
    modifier: Modifier = Modifier,
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val uiState by viewModel.uiState.collectAsState()
    val tabs = listOf("Scheduled", "Results")

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onCreateMatchClick,
                containerColor = KineticGreen,
                contentColor = Color.White,
            ) {
                Icon(Icons.Rounded.Add, contentDescription = "Create Match")
            }
        },
        modifier = modifier,
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding),
        ) {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = KineticGreen,
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title, fontWeight = FontWeight.Bold) },
                    )
                }
            }

            when (val state = uiState) {
                MatchUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = KineticGreen)
                    }
                }
                is MatchUiState.Error -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(state.message, color = MaterialTheme.colorScheme.error)
                    }
                }
                is MatchUiState.Success -> {
                    val matches =
                        if (selectedTab == 0) state.scheduledMatches else state.completedMatches

                    if (matches.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("No matches found", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            items(matches) { match ->
                                MatchItemCard(
                                    match = match,
                                    onClick = {
                                        if (!match.isCompleted) {
                                            onMatchClick(match.id)
                                        }
                                    },
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MatchItemCard(
    match: MatchItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val dateLabel = remember(match.date) {
        SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(Date(match.date))
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = match.courtName,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = KineticGreen,
                )
                Text(
                    text = dateLabel,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column {
                    Text(match.player1Name, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(match.player2Name, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                }

                if (match.isCompleted) {
                    Column(horizontalAlignment = Alignment.End) {
                        match.setScores.forEach { set ->
                            Text(
                                text = "${set.p1()} - ${set.p2()}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black,
                                color = if (set.p1() > set.p2()) KineticGreen else Color.Gray,
                            )
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color.LightGray.copy(alpha = 0.15f))
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                    ) {
                        Text(
                            text = "Record Score",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Gray,
                        )
                    }
                }
            }
        }
    }
}
