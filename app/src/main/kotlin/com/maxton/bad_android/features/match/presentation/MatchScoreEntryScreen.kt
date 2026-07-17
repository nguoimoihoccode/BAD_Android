package com.maxton.bad_android.features.match.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maxton.bad_android.theme.KineticGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchScoreEntryScreen(
    matchId: String,
    onBackClick: () -> Unit,
    onScoreSaved: (String) -> Unit,
    viewModel: MatchViewModel,
    modifier: Modifier = Modifier,
) {
    val detailState by viewModel.matchDetailState.collectAsState()
    val isSubmitting by viewModel.isSubmitting.collectAsState()

    var p1Set1 by remember { mutableIntStateOf(0) }
    var p2Set1 by remember { mutableIntStateOf(0) }
    var p1Set2 by remember { mutableIntStateOf(0) }
    var p2Set2 by remember { mutableIntStateOf(0) }
    var p1Set3 by remember { mutableIntStateOf(0) }
    var p2Set3 by remember { mutableIntStateOf(0) }
    var hasSet3 by remember { mutableStateOf(false) }

    LaunchedEffect(matchId) {
        viewModel.loadMatchDetail(matchId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Enter Match Score", fontWeight = FontWeight.Bold) },
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
        when (val state = detailState) {
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
                    Text(state.message, color = MaterialTheme.colorScheme.error)
                }
            }
            is MatchDetailUiState.Success -> {
                val match = state.match
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                        .padding(innerPadding)
                        .padding(24.dp)
                        .verticalScroll(rememberScrollState()),
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = KineticGreen.copy(alpha = 0.03f)
                        ),
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = match.player1Name,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = KineticGreen,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.weight(1f),
                            )
                            Box(
                                modifier = Modifier
                                    .background(
                                        MaterialTheme.colorScheme.outlineVariant,
                                        RoundedCornerShape(4.dp),
                                    )
                                    .padding(horizontal = 10.dp, vertical = 4.dp),
                            ) {
                                Text("VS", fontSize = 11.sp, fontWeight = FontWeight.Black)
                            }
                            Text(
                                text = match.player2Name,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.weight(1f),
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    SetCounterCard(
                        title = "Set 1",
                        p1 = p1Set1,
                        p2 = p2Set1,
                        onChange = { a, b ->
                            p1Set1 = a
                            p2Set1 = b
                        },
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    SetCounterCard(
                        title = "Set 2",
                        p1 = p1Set2,
                        p2 = p2Set2,
                        onChange = { a, b ->
                            p1Set2 = a
                            p2Set2 = b
                        },
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = hasSet3,
                            onCheckedChange = { hasSet3 = it },
                            colors = CheckboxDefaults.colors(checkedColor = KineticGreen),
                        )
                        Text("Play Set 3 (Tiebreaker)", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    }

                    if (hasSet3) {
                        Spacer(modifier = Modifier.height(8.dp))
                        SetCounterCard(
                            title = "Set 3",
                            p1 = p1Set3,
                            p2 = p2Set3,
                            onChange = { a, b ->
                                p1Set3 = a
                                p2Set3 = b
                            },
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    Button(
                        onClick = {
                            val scores = mutableListOf(
                                mapOf("p1" to p1Set1, "p2" to p2Set1),
                                mapOf("p1" to p1Set2, "p2" to p2Set2),
                            )
                            if (hasSet3) {
                                scores.add(mapOf("p1" to p1Set3, "p2" to p2Set3))
                            }
                            viewModel.enterMatchScore(
                                matchId = matchId,
                                setScores = scores,
                                onSuccess = { onScoreSaved(matchId) },
                            )
                        },
                        enabled = !isSubmitting,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = KineticGreen),
                    ) {
                        if (isSubmitting) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color.White,
                                strokeWidth = 2.dp,
                            )
                        } else {
                            Text("Save Results", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SetCounterCard(
    title: String,
    p1: Int,
    p2: Int,
    onChange: (Int, Int) -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                CounterControls(value = p1, onChanged = { onChange(it, p2) })
                Text("-", fontSize = 20.sp, color = MaterialTheme.colorScheme.outlineVariant)
                CounterControls(value = p2, onChanged = { onChange(p1, it) })
            }
        }
    }
}

@Composable
private fun CounterControls(value: Int, onChanged: (Int) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(
            onClick = { if (value > 0) onChanged(value - 1) },
            enabled = value > 0,
        ) {
            Icon(Icons.Rounded.Remove, null, tint = KineticGreen, modifier = Modifier.size(28.dp))
        }
        Text(
            text = value.toString(),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.width(48.dp),
            textAlign = TextAlign.Center,
        )
        IconButton(onClick = { onChanged(value + 1) }) {
            Icon(Icons.Rounded.Add, null, tint = KineticGreen, modifier = Modifier.size(28.dp))
        }
    }
}
