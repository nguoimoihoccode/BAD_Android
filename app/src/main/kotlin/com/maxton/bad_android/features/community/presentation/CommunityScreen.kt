package com.maxton.bad_android.features.community.presentation

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maxton.bad_android.features.community.domain.entities.ChatMessage
import com.maxton.bad_android.features.community.domain.entities.CommunityAnnouncement
import com.maxton.bad_android.features.community.domain.entities.CommunityMember
import com.maxton.bad_android.features.community.domain.entities.CommunityPoll
import com.maxton.bad_android.theme.Accent
import com.maxton.bad_android.theme.Error
import com.maxton.bad_android.theme.ErrorContainer
import com.maxton.bad_android.theme.KineticGreen
import com.maxton.bad_android.theme.Primary
import com.maxton.bad_android.theme.PrimaryContainer
import com.maxton.bad_android.theme.Tertiary

@Composable
fun CommunityScreen(
    viewModel: CommunityViewModel,
    onPlayerClick: (String) -> Unit = {},
    onLeaderboardClick: () -> Unit = {},
    onMembersClick: () -> Unit = {},
    onJoinRequestsClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        when (val uiState = state) {
            CommunityUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = KineticGreen)
                }
            }
            is CommunityUiState.Error -> {
                Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
                    Text(uiState.message, color = MaterialTheme.colorScheme.error)
                }
            }
            is CommunityUiState.Success -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    item {
                        AdminConsoleStrip(
                            onMembersClick = onMembersClick,
                            onJoinRequestsClick = onJoinRequestsClick,
                        )
                    }

                    if (uiState.announcements.isNotEmpty()) {
                        item {
                            AnnouncementCard(uiState.announcements[0])
                        }
                    }

                    item {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Active Now", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                                val onlineCount = uiState.activeMembers.count { it.isOnline }
                                Text(
                                    "$onlineCount Online",
                                    fontSize = 12.sp,
                                    color = KineticGreen,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                items(uiState.activeMembers) { member ->
                                    ActiveMemberAvatar(
                                        member = member,
                                        onClick = { onPlayerClick(member.id) },
                                    )
                                }
                            }
                        }
                    }

                    item {
                        PollCard(
                            poll = uiState.poll,
                            onOptionClick = { viewModel.voteInPoll(it) }
                        )
                    }

                    if (uiState.messages.isNotEmpty()) {
                        item {
                            GroupChatPreviewCard(messages = uiState.messages)
                        }
                    }

                    item {
                        Column {
                            Text("Top Players", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(12.dp))
                            uiState.topPlayers.forEachIndexed { index, player ->
                                TopPlayerCard(
                                    player = player,
                                    rank = index + 1,
                                    onClick = { onPlayerClick(player.id) },
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                            OutlinedButton(
                                onClick = onLeaderboardClick,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Primary),
                            ) {
                                Text("View Full Ranking", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    }

                    item { Spacer(modifier = Modifier.height(40.dp)) }
                }
            }
        }
    }
}

@Composable
private fun AdminConsoleStrip(
    onMembersClick: () -> Unit,
    onJoinRequestsClick: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Primary.copy(alpha = 0.05f)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Primary.copy(alpha = 0.15f)),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Rounded.Shield, null, tint = Primary, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Admin Control Panel",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Primary,
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                OutlinedButton(
                    onClick = onMembersClick,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Primary),
                ) {
                    Icon(Icons.Rounded.People, null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Members", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                OutlinedButton(
                    onClick = onJoinRequestsClick,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Primary),
                ) {
                    Icon(Icons.Rounded.Email, null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Requests", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun AnnouncementCard(announcement: CommunityAnnouncement) {
    val hasImage = announcement.imageUrl.isNotBlank()
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    if (hasImage) {
                        Brush.linearGradient(
                            colors = listOf(Primary, Color(0xFF004D34), PrimaryContainer.copy(alpha = 0.7f))
                        )
                    } else {
                        Brush.linearGradient(colors = listOf(Primary, Color(0xFF004D34)))
                    }
                )
        ) {
            if (hasImage) {
                Icon(
                    imageVector = Icons.Rounded.SportsTennis,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.15f),
                    modifier = Modifier
                        .size(120.dp)
                        .align(Alignment.CenterEnd)
                        .padding(end = 8.dp),
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f))
                        )
                    )
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Bottom
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(PrimaryContainer)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = announcement.tag.uppercase(),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        letterSpacing = 0.8.sp
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = announcement.title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun ActiveMemberAvatar(
    member: CommunityMember,
    onClick: () -> Unit = {},
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick),
    ) {
        Box(modifier = Modifier.size(54.dp)) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .border(
                        width = if (member.isOnline) 2.dp else 0.dp,
                        color = if (member.isOnline) PrimaryContainer else Color.Transparent,
                        shape = CircleShape,
                    )
                    .background(KineticGreen.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = member.name.firstOrNull()?.uppercase() ?: "?",
                    fontWeight = FontWeight.Bold,
                    color = KineticGreen,
                    fontSize = 16.sp,
                )
            }
            if (member.isOnline) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .align(Alignment.BottomEnd),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(PrimaryContainer)
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(member.name, fontSize = 11.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun PollCard(
    poll: CommunityPoll,
    onOptionClick: (String) -> Unit
) {
    val hasVoted = poll.selectedOptionId != null

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Rounded.Campaign, null, tint = Tertiary, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Community Poll", fontSize = 15.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(poll.question, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))

            poll.options.forEach { option ->
                val isSelected = option.id == poll.selectedOptionId
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .border(
                            width = 2.dp,
                            color = if (isSelected) PrimaryContainer else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(8.dp),
                        )
                        .background(
                            if (isSelected) KineticGreen.copy(alpha = 0.08f)
                            else MaterialTheme.colorScheme.background
                        )
                        .clickable(enabled = !hasVoted) { onOptionClick(option.id) }
                        .padding(12.dp)
                ) {
                    if (hasVoted) {
                        val animatedProgress by animateFloatAsState(
                            targetValue = (option.votesPercent / 100f).toFloat(),
                            label = "poll",
                        )
                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .clip(RoundedCornerShape(4.dp))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .fillMaxWidth(animatedProgress)
                                    .background(PrimaryContainer.copy(alpha = 0.12f))
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(option.text, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        if (hasVoted) {
                            Text(
                                "${option.votesPercent.toInt()}%",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = KineticGreen
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "${poll.totalVotes} votes • ${poll.daysLeft} days left",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.align(Alignment.CenterHorizontally),
            )
        }
    }
}

@Composable
private fun GroupChatPreviewCard(messages: List<ChatMessage>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Rounded.Forum, null, tint = Primary, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Court 4 Chat", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(ErrorContainer)
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text("3 NEW", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Error)
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            messages.take(3).forEach { message ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    verticalAlignment = Alignment.Top,
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(KineticGreen.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = message.senderName.firstOrNull()?.uppercase() ?: "?",
                            fontWeight = FontWeight.Bold,
                            color = KineticGreen,
                            fontSize = 14.sp,
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clip(
                                RoundedCornerShape(
                                    topStart = 0.dp,
                                    topEnd = 12.dp,
                                    bottomEnd = 12.dp,
                                    bottomStart = 12.dp,
                                )
                            )
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                            .padding(10.dp)
                    ) {
                        Text(message.senderName, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Primary)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            message.text,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
            Button(
                onClick = {},
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Primary),
            ) {
                Text("Join Conversation", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.White)
                Spacer(modifier = Modifier.width(4.dp))
                Icon(Icons.Rounded.ArrowForward, null, tint = Color.White, modifier = Modifier.size(16.dp))
            }
        }
    }
}

@Composable
private fun TopPlayerCard(
    player: CommunityMember,
    rank: Int,
    onClick: () -> Unit,
) {
    val rankColor = when (rank) {
        1 -> Accent
        2 -> Color(0xFFBDBDBD)
        3 -> Color(0xFFFFCC80)
        else -> Color.Gray
    }
    val rankTextColor = when (rank) {
        2 -> Color(0xFF616161)
        3 -> Color(0xFFE65100)
        else -> Color.White
    }
    val badgeBg = if (player.badge == "Gold") Color(0xFFFFF9C4) else Color(0xFFF5F5F5)
    val badgeFg = if (player.badge == "Gold") Color(0xFFF9A825) else Color(0xFF757575)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(KineticGreen.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = player.name.firstOrNull()?.uppercase() ?: "?",
                        fontWeight = FontWeight.Bold,
                        color = KineticGreen,
                    )
                }
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .offset(x = (-4).dp, y = (-4).dp)
                        .clip(CircleShape)
                        .background(rankColor),
                    contentAlignment = Alignment.Center,
                ) {
                    Text("$rank", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = rankTextColor)
                }
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(player.name, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(badgeBg)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            player.badge ?: "Member",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = badgeFg,
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        player.activity ?: "",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${player.points ?: 0}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Primary,
                )
                Text(
                    "PTS",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    letterSpacing = 0.6.sp,
                )
            }
        }
    }
}

@Composable
fun PlayerRankRow(
    player: CommunityMember,
    rank: Int,
    onClick: () -> Unit = {},
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "$rank",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = if (rank == 1) KineticGreen else Color.Gray,
                modifier = Modifier.width(24.dp)
            )
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(KineticGreen.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = player.name.firstOrNull()?.uppercase() ?: "?",
                    fontWeight = FontWeight.Bold,
                    color = KineticGreen,
                    fontSize = 14.sp,
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(player.name, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Text(player.activity ?: "", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }

        Text(
            text = "${player.points} pts",
            fontSize = 14.sp,
            fontWeight = FontWeight.Black,
            color = KineticGreen
        )
    }
}
