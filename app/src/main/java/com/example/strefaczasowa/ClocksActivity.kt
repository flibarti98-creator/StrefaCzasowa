package com.example.strefaczasowa

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

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
                ClocksPlaceholderScreen(
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
fun ClocksPlaceholderScreen(
    zones: List<String>,
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Wybrane strefy") }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (zones.isEmpty()) {
                Text("Nie wybrano żadnych stref czasowych.")
            } else {
                zones.forEach { zone ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = zone,
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            OutlinedButton(
                onClick = onBackClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Wróć do wyboru stref")
            }
        }
    }
}