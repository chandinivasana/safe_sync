package com.crispyc.safesync.features.onboarding

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun OnboardingScreen(
    viewModel: OnboardingViewModel = hiltViewModel(),
    onOnboardingComplete: () -> Unit
) {
    var currentStep by remember { mutableStateOf(0) }
    
    when (currentStep) {
        0 -> ConsentScreen { currentStep++ }
        1 -> ProfileSetupScreen { name, lang -> 
            viewModel.saveProfile(name, lang)
            currentStep++ 
        }
        2 -> WalletGenerationScreen(viewModel) { onOnboardingComplete() }
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
fun ProfileSetupScreen(onNext: (String, String) -> Unit) {
    var name by remember { mutableStateOf("") }
    var selectedLang by remember { mutableStateOf("Kannada") }
    
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
        Text("Select Language:")
        Row {
            RadioButton(selected = selectedLang == "Kannada", onClick = { selectedLang = "Kannada" })
            Text("Kannada", modifier = Modifier.padding(top = 12.dp))
            Spacer(modifier = Modifier.width(16.dp))
            RadioButton(selected = selectedLang == "Hindi", onClick = { selectedLang = "Hindi" })
            Text("Hindi", modifier = Modifier.padding(top = 12.dp))
        }
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = { onNext(name, selectedLang) }, modifier = Modifier.fillMaxWidth()) {
            Text("Continue")
        }
    }
}

@Composable
fun WalletGenerationScreen(viewModel: OnboardingViewModel, onComplete: () -> Unit) {
    val mnemonic by viewModel.mnemonic.collectAsState()
    
    LaunchedEffect(Unit) {
        viewModel.generateWallet()
    }

    Column(modifier = Modifier.padding(16.dp).fillMaxSize(), verticalArrangement = Arrangement.Center) {
        Text("HD Wallet Auto-Generation", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        Text("Generating BIP-39 mnemonic...")
        if (mnemonic.isNotEmpty()) {
            Text("""Please write this down: 

$mnemonic""")
        } else {
            CircularProgressIndicator()
        }
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onComplete, modifier = Modifier.fillMaxWidth(), enabled = mnemonic.isNotEmpty()) {
            Text("I've saved my seed phrase")
        }
    }
}
