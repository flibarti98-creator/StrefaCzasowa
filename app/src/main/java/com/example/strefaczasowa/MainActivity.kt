package com.example.strefaczasowa

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("CyklZycia", "MainActivity -> onCreate")
        setContent {
            MaterialTheme {
                ZoneSelectionScreen()
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ZoneSelectionScreen() {
    val context = LocalContext.current

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
            TopAppBar(title = { Text("Wybierz strefy czasowe") })
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
                onClick = {
                    val intent = Intent(context, ClocksActivity::class.java).apply {
                        putStringArrayListExtra(ClocksActivity.EXTRA_ZONES, ArrayList(selectedZones))
                    }
                    context.startActivity(intent)
                },
                enabled = selectedZones.isNotEmpty(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Pokaż zegary (${selectedZones.size})")
            }
        }
    }
}