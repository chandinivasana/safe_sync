package com.crispyc.safesync.features.safety

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.crispyc.safesync.features.safety.SafetyViewModel

@Composable
fun SafetyScreen(viewModel: SafetyViewModel = hiltViewModel()) {
    val isSosActive by viewModel.isSosActive.collectAsState()
    val incomingSosAlert by viewModel.incomingSosAlert.collectAsState()
    val isArViewActive by viewModel.isArViewActive.collectAsState()
    val countdown by viewModel.countdown.collectAsState()
    val context = LocalContext.current

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.toggleArView()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (isArViewActive) {
            ArViewComponent()
            
            // Overlay safety info
            Column(
                modifier = Modifier.align(Alignment.TopStart).padding(16.dp).background(Color.Black.copy(alpha = 0.5f)).padding(8.dp)
            ) {
                Text("AR SAFETY OVERLAY", color = Color.Green, fontSize = 12.sp)
                Text("Mesh Nodes: 12", color = Color.White, fontSize = 10.sp)
                Text("Signal Strength: High", color = Color.White, fontSize = 10.sp)
            }

            Button(
                onClick = { viewModel.toggleArView() },
                modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 32.dp)
            ) {
                Text("Close AR View")
            }
        } else {
            // Map Placeholder
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Offline Maps Component (MapLibre)", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(16.dp))
                
                Button(onClick = { viewModel.setHomeZone(12.9716, 77.5946) }) {
                    Text("Set Current Location as Home Zone (A-02)")
                }
                
                Spacer(modifier = Modifier.height(8.dp))

                Button(onClick = {
                    val permissionCheckResult = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                    if (permissionCheckResult == PackageManager.PERMISSION_GRANTED) {
                        viewModel.toggleArView()
                    } else {
                        permissionLauncher.launch(Manifest.permission.CAMERA)
                    }
                }) {
                    Text("Open AR View")
                }
            }
        }

        // SOS BROADCAST UI (C-01)
        if (isSosActive) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Red.copy(alpha = 0.9f)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("SOS TRIGGERED", color = Color.White, fontSize = 32.sp)
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

        // SOS ALERT RECEIVER UI (C-04)
        incomingSosAlert?.let { alert ->
            AlertDialog(
                onDismissRequest = { viewModel.dismissAlert() },
                title = { Text("EMERGENCY ALERT", color = Color.Red) },
                text = { Text("Incoming SOS from node: ${alert.senderId}\nTimestamp: ${alert.timestamp}") },
                confirmButton = {
                    Button(onClick = { viewModel.dismissAlert() }) {
                        Text("Acknowledge")
                    }
                }
            )
        }
    }
}

@Composable
fun ArViewComponent() {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }

    DisposableEffect(lifecycleOwner) {
        onDispose {
            try {
                cameraProviderFuture.get().unbindAll()
            } catch (e: Exception) {
                // Ignore if not initialized
            }
        }
    }

    AndroidView(
        factory = { ctx ->
            val previewView = PreviewView(ctx)
            val executor = ContextCompat.getMainExecutor(ctx)
            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()
                val preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }

                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                try {
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        cameraSelector,
                        preview
                    )
                } catch (e: Exception) {
                    // Handle failure
                }
            }, executor)
            previewView
        },
        modifier = Modifier.fillMaxSize()
    )
}
