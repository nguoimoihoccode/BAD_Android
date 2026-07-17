package com.maxton.bad_android.features.match.presentation

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccessTime
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material.icons.rounded.CalendarToday
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maxton.bad_android.theme.KineticGreen
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchCreatorScreen(
    preselectedOpponentId: String? = null,
    onBackClick: () -> Unit,
    onMatchCreated: (String) -> Unit,
    viewModel: MatchViewModel,
    modifier: Modifier = Modifier,
) {
    val isSubmitting by viewModel.isSubmitting.collectAsState()
    val context = LocalContext.current

    val opponentsList = listOf(
        "m1" to "Trần Nguyễn Tiến (Advanced)",
        "m3" to "Lê Hoài Nam (Intermediate)",
        "m4" to "Phạm Minh Trí (Beginner)",
    )
    val courtsList = listOf(
        "City Arena • Court 1",
        "City Arena • Court 2",
        "City Arena • Court 3",
        "Standard Club • Court 5",
    )

    var selectedOpponentId by remember {
        mutableStateOf(preselectedOpponentId?.takeIf { id -> opponentsList.any { it.first == id } } ?: "m1")
    }
    var selectedCourt by remember { mutableStateOf("City Arena • Court 2") }
    var expandedOpponent by remember { mutableStateOf(false) }
    var expandedCourt by remember { mutableStateOf(false) }

    val calendar = remember {
        Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 18)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
    }
    var selectedDateMillis by remember { mutableLongStateOf(calendar.timeInMillis) }
    var hour by remember { mutableIntStateOf(18) }
    var minute by remember { mutableIntStateOf(0) }

    val dateLabel = remember(selectedDateMillis) {
        val c = Calendar.getInstance().apply { timeInMillis = selectedDateMillis }
        "${c.get(Calendar.DAY_OF_MONTH)}/${c.get(Calendar.MONTH) + 1}/${c.get(Calendar.YEAR)}"
    }
    val timeLabel = remember(hour, minute) {
        String.format("%02d:%02d", hour, minute)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create Singles Match", fontWeight = FontWeight.Bold) },
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
        ) {
            Text("Select Opponent", fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = opponentsList.firstOrNull { it.first == selectedOpponentId }?.second ?: "",
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { expandedOpponent = true },
                    shape = RoundedCornerShape(8.dp),
                    trailingIcon = {
                        IconButton(onClick = { expandedOpponent = !expandedOpponent }) {
                            Icon(Icons.Rounded.ChevronRight, null)
                        }
                    },
                )
                DropdownMenu(
                    expanded = expandedOpponent,
                    onDismissRequest = { expandedOpponent = false },
                    modifier = Modifier.fillMaxWidth(0.9f),
                ) {
                    opponentsList.forEach { (id, name) ->
                        DropdownMenuItem(
                            text = { Text(name) },
                            onClick = {
                                selectedOpponentId = id
                                expandedOpponent = false
                            },
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text("Select Court Venue", fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = selectedCourt,
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { expandedCourt = true },
                    shape = RoundedCornerShape(8.dp),
                    trailingIcon = {
                        IconButton(onClick = { expandedCourt = !expandedCourt }) {
                            Icon(Icons.Rounded.ChevronRight, null)
                        }
                    },
                )
                DropdownMenu(
                    expanded = expandedCourt,
                    onDismissRequest = { expandedCourt = false },
                    modifier = Modifier.fillMaxWidth(0.9f),
                ) {
                    courtsList.forEach { court ->
                        DropdownMenuItem(
                            text = { Text(court) },
                            onClick = {
                                selectedCourt = court
                                expandedCourt = false
                            },
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Date", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedCard(
                        onClick = {
                            val c = Calendar.getInstance().apply { timeInMillis = selectedDateMillis }
                            DatePickerDialog(
                                context,
                                { _, year, month, day ->
                                    val updated = Calendar.getInstance().apply {
                                        set(year, month, day, hour, minute, 0)
                                        set(Calendar.MILLISECOND, 0)
                                    }
                                    selectedDateMillis = updated.timeInMillis
                                },
                                c.get(Calendar.YEAR),
                                c.get(Calendar.MONTH),
                                c.get(Calendar.DAY_OF_MONTH),
                            ).show()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(Icons.Rounded.CalendarToday, null, tint = KineticGreen, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(dateLabel, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                        }
                    }
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text("Time", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedCard(
                        onClick = {
                            TimePickerDialog(
                                context,
                                { _, h, m ->
                                    hour = h
                                    minute = m
                                    val updated = Calendar.getInstance().apply {
                                        timeInMillis = selectedDateMillis
                                        set(Calendar.HOUR_OF_DAY, h)
                                        set(Calendar.MINUTE, m)
                                        set(Calendar.SECOND, 0)
                                        set(Calendar.MILLISECOND, 0)
                                    }
                                    selectedDateMillis = updated.timeInMillis
                                },
                                hour,
                                minute,
                                true,
                            ).show()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(Icons.Rounded.AccessTime, null, tint = KineticGreen, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(timeLabel, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    val dateTime = Calendar.getInstance().apply {
                        timeInMillis = selectedDateMillis
                        set(Calendar.HOUR_OF_DAY, hour)
                        set(Calendar.MINUTE, minute)
                        set(Calendar.SECOND, 0)
                        set(Calendar.MILLISECOND, 0)
                    }.timeInMillis
                    viewModel.createMatch(
                        opponentId = selectedOpponentId,
                        courtName = selectedCourt,
                        dateTimeMillis = dateTime,
                        onSuccess = { match -> onMatchCreated(match.id) },
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
                    Text("Create & Enter Score", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
                }
            }
        }
    }
}
