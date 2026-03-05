package com.crispyc.safesync.features.safety

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.crispyc.safesync.features.safety.SafetyViewModel

@Composable
fun SafetyScreen(viewModel: SafetyViewModel = hiltViewModel()) {
    val isSosActive by viewModel.isSosActive.collectAsState()
    val countdown by viewModel.countdown.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        // Map Placeholder
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Offline Maps Component (MapLibre)")
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { /* Toggle AR Overlay */ }) {
                Text("Open AR View")
            }
        }

        if (isSosActive) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Red.copy(alpha = 0.8f)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("SOS TRIGGERED", color = Color.White, fontSize = 24.sp)
                    Text("Broadcasting in $countdown seconds", color = Color.White)
                    Spacer(modifier = Modifier.height(32.dp))
                    Button(
                        onClick = { viewModel.cancelSos() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Red)
                    ) {
                        Text("CANCEL")
                    }
                }
            }
        }
    }
}
