package com.maxton.bad_android.features.profile.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.StarBorder
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
fun RatePlayerScreen(
    playerId: String,
    onBackClick: () -> Unit,
    viewModel: PlayerProfileViewModel,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.uiState.collectAsState()
    val isSubmitting by viewModel.isSubmitting.collectAsState()
    var skillRating by remember { mutableIntStateOf(5) }
    var fairPlayRating by remember { mutableIntStateOf(5) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state) {
        if (state is PlayerProfileUiState.RatingSuccess) {
            snackbarHostState.showSnackbar("Thank you for rating!")
            viewModel.loadPlayerProfile(playerId)
            onBackClick()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Rate Player", fontWeight = FontWeight.Bold) },
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
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = modifier,
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                "Share your feedback",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Your ratings help keep the matches fair and standard in our community.",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(24.dp))
            Text(
                "Skill level rating",
                modifier = Modifier.fillMaxWidth(),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
            )
            Spacer(modifier = Modifier.height(8.dp))
            RatingSelectorCard(
                rating = skillRating,
                label = skillRatingText(skillRating),
                onRatingChange = { skillRating = it },
            )

            Spacer(modifier = Modifier.height(24.dp))
            Text(
                "Fairplay & Sportsmanship",
                modifier = Modifier.fillMaxWidth(),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
            )
            Spacer(modifier = Modifier.height(8.dp))
            RatingSelectorCard(
                rating = fairPlayRating,
                label = fairPlayRatingText(fairPlayRating),
                onRatingChange = { fairPlayRating = it },
            )

            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = {
                    viewModel.submitPlayerRating(
                        playerId = playerId,
                        skillRating = skillRating.toDouble(),
                        fairPlayRating = fairPlayRating.toDouble(),
                    )
                },
                enabled = !isSubmitting,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = KineticGreen),
                shape = RoundedCornerShape(12.dp),
            ) {
                if (isSubmitting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White,
                        strokeWidth = 2.dp,
                    )
                } else {
                    Text("Submit Ratings", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun RatingSelectorCard(
    rating: Int,
    label: String,
    onRatingChange: (Int) -> Unit,
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
            Row(horizontalArrangement = Arrangement.Center) {
                repeat(5) { index ->
                    val starValue = index + 1
                    IconButton(onClick = { onRatingChange(starValue) }) {
                        Icon(
                            imageVector = if (rating >= starValue) Icons.Rounded.Star else Icons.Rounded.StarBorder,
                            contentDescription = "$starValue stars",
                            tint = Color(0xFFFFC107),
                            modifier = Modifier.size(36.dp),
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(label, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = KineticGreen)
        }
    }
}

private fun skillRatingText(score: Int): String = when (score) {
    1 -> "Novice / Beginner"
    2 -> "Advanced Beginner"
    3 -> "Competent Intermediate"
    4 -> "Advanced / High-level"
    else -> "Expert / Professional"
}

private fun fairPlayRatingText(score: Int): String = when (score) {
    1 -> "Poor Sportsmanship"
    2 -> "Somewhat Impatient"
    3 -> "Fair / Standard"
    4 -> "Friendly & Courteous"
    else -> "Exceptional Fairplay"
}
