package com.example.strefaczasowa

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.delay
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class ClocksActivity : ComponentActivity() {

    companion object {
        const val EXTRA_ZONES = "com.example.strefaczasowa.EXTRA_ZONES"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("CyklZycia", "ClocksActivity -> onCreate")

        val receivedZones = intent.getStringArrayListExtra(EXTRA_ZONES) ?: emptyList()

        setContent {
            MaterialTheme {
                ClocksScreen(
                    zones = receivedZones,
                    onBackClick = { finish() }
                )
            }
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d("CyklZycia", "ClocksActivity -> onStart")
    }

    override fun onResume() {
        super.onResume()
        Log.d("CyklZycia", "ClocksActivity -> onResume")
    }

    override fun onPause() {
        super.onPause()
        Log.d("CyklZycia", "ClocksActivity -> onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.d("CyklZycia", "ClocksActivity -> onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("CyklZycia", "ClocksActivity -> onDestroy")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClocksScreen(
    zones: List<String>,
    onBackClick: () -> Unit
) {
    val timeFormatter = remember { DateTimeFormatter.ofPattern("HH:mm:ss") }
    val dateFormatter = remember { DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy", Locale.getDefault()) }

    // 1. Stan przechowujący aktualną chwilę w czasie (odświeżany co sekundę)
    var currentInstant by remember { mutableStateOf(Instant.now()) }
    val lifecycleOwner = LocalLifecycleOwner.current

    // 2. Efekt powiązany z czasem życia kompozycji i stanem STARTED ekranu
    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            while (true) {
                val now = Instant.now()
                currentInstant = now
                Log.d("ZegarTick", "Odświeżono czas: $now")

                // Wyliczenie czasu do najbliższej pełnej sekundy (eliminacja dryfu)
                val millisInCurrentSecond = now.toEpochMilli() % 1000
                val delayToNextSecond = 1000L - millisInCurrentSecond
                delay(delayToNextSecond)
            }
        }
    }

    val localZone = ZoneId.systemDefault()
    val localTime = ZonedDateTime.ofInstant(currentInstant, localZone)
    val sameTimeText = stringResource(R.string.same_time)

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(stringResource(R.string.world_clocks_title)) })
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = stringResource(R.string.local_time_label, localZone.id),
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = localTime.format(timeFormatter),
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = localTime.format(dateFormatter),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            if (zones.isEmpty()) {
                Text(stringResource(R.string.no_zones_selected))
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(zones) { zoneIdString ->
                        val targetZone = ZoneId.of(zoneIdString)
                        val targetTime = ZonedDateTime.ofInstant(currentInstant, targetZone)
                        val diffText = formatTimeDifference(localZone, targetZone, currentInstant, sameTimeText)

                        Card(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = zoneIdString,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = targetTime.format(dateFormatter),
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                    Text(
                                        text = stringResource(R.string.difference_label, diffText),
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                                Text(
                                    text = targetTime.format(timeFormatter),
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            OutlinedButton(
                onClick = onBackClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.back_to_selection_btn))
            }
        }
    }
}

fun formatTimeDifference(
    localZone: ZoneId,
    targetZone: ZoneId,
    instant: Instant,
    sameTimeText: String
): String {
    val localOffsetSeconds = localZone.rules.getOffset(instant).totalSeconds
    val targetOffsetSeconds = targetZone.rules.getOffset(instant).totalSeconds
    val diffSeconds = targetOffsetSeconds - localOffsetSeconds

    if (diffSeconds == 0) return sameTimeText

    val hours = diffSeconds / 3600
    val minutes = Math.abs((diffSeconds % 3600) / 60)
    val sign = if (diffSeconds > 0) "+" else ""

    return if (minutes == 0) {
        "$sign$hours h"
    } else {
        "$sign$hours h $minutes min"
    }
}