package com.maxton.bad_android.features.match.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material.icons.rounded.CalendarToday
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.BarChart
import androidx.compose.material.icons.rounded.Trophy
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maxton.bad_android.features.match.domain.entities.MatchItem
import com.maxton.bad_android.features.match.domain.entities.p1
import com.maxton.bad_android.features.match.domain.entities.p2
import com.maxton.bad_android.theme.KineticGreen
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchResultScreen(
    matchId: String,
    onBackClick: () -> Unit,
    onBackHome: () -> Unit,
    viewModel: MatchViewModel,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.matchDetailState.collectAsState()

    LaunchedEffect(matchId) {
        viewModel.loadMatchDetail(matchId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Match Results", fontWeight = FontWeight.Bold) },
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
            MatchDetailUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(color = KineticGreen)
                }
            }
            is MatchDetailUiState.Error -> {
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
            is MatchDetailUiState.Success -> {
                MatchResultContent(
                    match = uiState.match,
                    onBackHome = onBackHome,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                )
            }
        }
    }
}

@Composable
private fun MatchResultContent(
    match: MatchItem,
    onBackHome: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var p1SetsWon = 0
    var p2SetsWon = 0
    var p1TotalPoints = 0
    var p2TotalPoints = 0

    match.setScores.forEach { set ->
        val s1 = set.p1()
        val s2 = set.p2()
        p1TotalPoints += s1
        p2TotalPoints += s2
        when {
            s1 > s2 -> p1SetsWon++
            s2 > s1 -> p2SetsWon++
        }
    }

    val p1Wins = p1SetsWon > p2SetsWon
    val winnerName = if (p1Wins) match.player1Name else match.player2Name
    val dateLabel = remember(match.date) {
        SimpleDateFormat("d/M/yyyy", Locale.getDefault()).format(Date(match.date))
    }

    Column(
        modifier = modifier
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(KineticGreen.copy(alpha = 0.08f))
                .border(1.dp, KineticGreen.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(
                Icons.Rounded.Trophy,
                contentDescription = null,
                tint = KineticGreen,
                modifier = Modifier.size(56.dp),
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "MATCH COMPLETED",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = KineticGreen,
                letterSpacing = 1.5.sp,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "$winnerName wins the match!",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
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
                    .padding(24.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                PlayerColumn(
                    name = match.player1Name,
                    isWinner = p1Wins,
                    modifier = Modifier.weight(1f),
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f))
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                ) {
                    Text(
                        text = "$p1SetsWon - $p2SetsWon",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
                PlayerColumn(
                    name = match.player2Name,
                    isWinner = !p1Wins,
                    modifier = Modifier.weight(1f),
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Sets Scoreboard",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
        )
        Spacer(modifier = Modifier.height(8.dp))

        match.setScores.forEachIndexed { index, set ->
            SetScoreRow(setNum = index + 1, p1 = set.p1(), p2 = set.p2())
            Spacer(modifier = Modifier.height(8.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                DetailRow(Icons.Rounded.LocationOn, "Venue", match.courtName)
                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                DetailRow(Icons.Rounded.CalendarToday, "Played Date", dateLabel)
                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                DetailRow(Icons.Rounded.BarChart, "Total Points Won", "$p1TotalPoints - $p2TotalPoints")
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onBackHome,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = KineticGreen),
        ) {
            Text("Back to Dashboard", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
        }
    }
}

@Composable
private fun PlayerColumn(
    name: String,
    isWinner: Boolean,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(CircleShape)
                .background(KineticGreen.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = name.firstOrNull()?.uppercase() ?: "?",
                fontWeight = FontWeight.Bold,
                color = KineticGreen,
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = name,
            fontSize = 13.sp,
            fontWeight = if (isWinner) FontWeight.Bold else FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun SetScoreRow(setNum: Int, p1: Int, p2: Int) {
    val p1Wins = p1 > p2
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Set $setNum",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = p1.toString(),
                fontSize = 16.sp,
                fontWeight = if (p1Wins) FontWeight.Bold else FontWeight.Medium,
                color = if (p1Wins) KineticGreen else MaterialTheme.colorScheme.onSurface,
            )
            Spacer(modifier = Modifier.width(24.dp))
            Text(
                text = "vs",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.outlineVariant,
            )
            Spacer(modifier = Modifier.width(24.dp))
            Text(
                text = p2.toString(),
                fontSize = 16.sp,
                fontWeight = if (!p1Wins) FontWeight.Bold else FontWeight.Medium,
                color = if (!p1Wins) KineticGreen else MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}

@Composable
private fun DetailRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = KineticGreen, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = value,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}
