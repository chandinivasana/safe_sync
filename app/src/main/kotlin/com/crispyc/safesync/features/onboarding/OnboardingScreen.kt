package com.crispyc.safesync.features.onboarding

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun OnboardingScreen(onOnboardingComplete: () -> Unit) {
    var currentStep by remember { mutableStateOf(0) }
    
    when (currentStep) {
        0 -> ConsentScreen { currentStep++ }
        1 -> ProfileSetupScreen { currentStep++ }
        2 -> WalletGenerationScreen { onOnboardingComplete() }
    }
}

@Composable
fun ConsentScreen(onAccepted: () -> Unit) {
    Column(modifier = Modifier.padding(16.dp).fillMaxSize(), verticalArrangement = Arrangement.Center) {
        Text("DPDP Consent Flow", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        Text("We collect minimal data. Your safety data is stored on-device and blockchain gets hashes only.")
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onAccepted, modifier = Modifier.fillMaxWidth()) {
            Text("I Agree & Accept")
        }
    }
}

@Composable
fun ProfileSetupScreen(onNext: () -> Unit) {
    var name by remember { mutableStateOf("") }
    
    Column(modifier = Modifier.padding(16.dp).fillMaxSize(), verticalArrangement = Arrangement.Center) {
        Text("Profile Setup", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Your Name") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text("Language: Kannada / Hindi (Phase 1)")
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onNext, modifier = Modifier.fillMaxWidth()) {
            Text("Continue")
        }
    }
}

@Composable
fun WalletGenerationScreen(onComplete: () -> Unit) {
    Column(modifier = Modifier.padding(16.dp).fillMaxSize(), verticalArrangement = Arrangement.Center) {
        Text("HD Wallet Auto-Generation", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        Text("Generating BIP-39 mnemonic...")
        Text("Please write this down: 

apple zebra train ghost magic valid ...")
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onComplete, modifier = Modifier.fillMaxWidth()) {
            Text("I've saved my seed phrase")
        }
    }
}
