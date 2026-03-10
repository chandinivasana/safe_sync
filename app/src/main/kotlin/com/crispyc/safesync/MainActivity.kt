package com.crispyc.safesync

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.crispyc.safesync.features.safety.SafetyScreen
import com.crispyc.safesync.features.wellness.WellnessScreen
import com.crispyc.safesync.features.work.WorkScreen
import dagger.hilt.android.AndroidEntryPoint

import androidx.compose.material.icons.filled.Settings
import com.crispyc.safesync.features.settings.SettingsScreen

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SafeSyncMainScreen()
                }
            }
        }
    }
}

@Composable
fun SafeSyncMainScreen() {
    val navController = rememberNavController()
    val items = listOf("Safety", "Work", "Wellness", "Settings")
    
    val permissions = remember {
        mutableListOf(
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ).apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                add(Manifest.permission.BLUETOOTH_SCAN)
                add(Manifest.permission.BLUETOOTH_ADVERTISE)
                add(Manifest.permission.BLUETOOTH_CONNECT)
            }
        }.toTypedArray()
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissionsMap ->
        // Handle permission results if needed
    }

    LaunchedEffect(Unit) {
        permissionLauncher.launch(permissions)
    }
    
    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route
                
                items.forEach { screen ->
                    val icon = when (screen) {
                        "Safety" -> Icons.Default.Home
                        "Work" -> Icons.Default.Person
                        "Wellness" -> Icons.Default.Favorite
                        "Settings" -> Icons.Default.Settings
                        else -> Icons.Default.Home
                    }
                    NavigationBarItem(
                        icon = { Icon(icon, contentDescription = screen) },
                        label = { Text(screen) },
                        selected = currentRoute == screen,
                        onClick = {
                            navController.navigate(screen) {
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "Onboarding",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("Onboarding") {
                com.crispyc.safesync.features.onboarding.OnboardingScreen {
                    navController.navigate("Safety") {
                        popUpTo("Onboarding") { inclusive = true }
                    }
                }
            }
            composable("Safety") { SafetyScreen() }
            composable("Work") { WorkScreen() }
            composable("Wellness") { WellnessScreen() }
            composable("Settings") { SettingsScreen() }
        }
    }
}
