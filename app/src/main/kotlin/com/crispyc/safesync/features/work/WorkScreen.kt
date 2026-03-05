package com.crispyc.safesync.features.work

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun WorkScreen(viewModel: WorkViewModel = hiltViewModel()) {
    val gigs by viewModel.gigs.collectAsState()

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Nearby Work Gigs (Mesh)", style = MaterialTheme.typography.headlineSmall)
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Button(onClick = { viewModel.postGig("App Developer", "₹10,000") }) {
            Text("Broadcast a Gig")
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        LazyColumn {
            items(gigs) { gig ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(gig.title, style = MaterialTheme.typography.titleMedium)
                        Text("Budget: ${gig.budget}", style = MaterialTheme.typography.bodyMedium)
                        Text("Match: ${(gig.matchScore * 100).toInt()}%", color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }
    }
}
