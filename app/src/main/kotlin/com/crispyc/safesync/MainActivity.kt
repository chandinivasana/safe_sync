package com.crispyc.safesync

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import com.crispyc.safesync.features.wellness.WellnessViewModel
import com.crispyc.safesync.features.work.WorkScreen
import dagger.hilt.android.AndroidEntryPoint

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
    val items = listOf("Safety", "Work", "Wellness")
    
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
            startDestination = "Safety",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("Safety") { SafetyScreen() }
            composable("Work") { WorkScreen() }
            composable("Wellness") { WellnessScreen() }
        }
    }
}
