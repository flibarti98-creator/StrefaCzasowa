package com.example.strefaczasowa

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import kotlinx.serialization.Serializable

// 1. Definicja kluczy tras (ekranów) dla Navigation 3
@Serializable
data object ZoneSelectionKey : NavKey

@Serializable
data class ClocksKey(val selectedZones: List<String>) : NavKey

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("CyklZycia", "MainActivity -> onCreate")
        setContent {
            MaterialTheme {
                AppNavigation()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d("CyklZycia", "MainActivity -> onStart")
    }

    override fun onResume() {
        super.onResume()
        Log.d("CyklZycia", "MainActivity -> onResume")
    }

    override fun onPause() {
        super.onPause()
        Log.d("CyklZycia", "MainActivity -> onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.d("CyklZycia", "MainActivity -> onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("CyklZycia", "MainActivity -> onDestroy")
    }
}

@Composable
fun AppNavigation() {
    // 2. Stos nawigacji zapamiętywany w kompozycji (przetrwa obrót ekranu!)
    val backStack = rememberNavBackStack(ZoneSelectionKey)

    // 3. Komponent wyświetlający bieżący ekran ze stosu
    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryProvider = entryProvider {
            entry<ZoneSelectionKey> {
                ZoneSelectionScreen(
                    onShowClocks = { chosenZones ->
                        // Zamiast Intentu dodajemy klucz kolejnego ekranu na stos
                        backStack.add(ClocksKey(chosenZones))
                    }
                )
            }
            entry<ClocksKey> { key ->
                // Wykorzystujemy gotową funkcję ClocksScreen z Zadania 3!
                ClocksScreen(
                    zones = key.selectedZones,
                    onBackClick = { backStack.removeLastOrNull() }
                )
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ZoneSelectionScreen(
    onShowClocks: (List<String>) -> Unit
) {
    val availableZones = listOf(
        "Europe/Warsaw",
        "America/New_York",
        "Asia/Tokyo",
        "Europe/London",
        "Australia/Sydney"
    )

    var selectedZones by rememberSaveable {
        mutableStateOf(listOf("America/New_York", "Asia/Tokyo", "Europe/London"))
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(stringResource(R.string.select_zones_title)) })
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            availableZones.forEach { zone ->
                val isChecked = zone in selectedZones
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            selectedZones = if (isChecked) {
                                selectedZones - zone
                            } else {
                                selectedZones + zone
                            }
                        }
                        .padding(vertical = 4.dp)
                ) {
                    Checkbox(
                        checked = isChecked,
                        onCheckedChange = { checked ->
                            selectedZones = if (checked) {
                                selectedZones + zone
                            } else {
                                selectedZones - zone
                            }
                        }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = zone, style = MaterialTheme.typography.bodyLarge)
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { onShowClocks(selectedZones) },
                enabled = selectedZones.isNotEmpty(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.show_clocks_btn, selectedZones.size))
            }
        }
    }
}