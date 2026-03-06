package com.crispyc.safesync.features.wellness

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.crispyc.safesync.core.db.entities.JournalEntryEntity
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun WellnessScreen(viewModel: WellnessViewModel = hiltViewModel()) {
    val entries by viewModel.journalEntries.collectAsState()
    var text by remember { mutableStateOf("") }
    val sdf = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault())

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Personal Wellness Journal", style = MaterialTheme.typography.headlineSmall)
        Text("Encrypted & Offline", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.secondary)
        
        Spacer(modifier = Modifier.height(16.dp))
        
        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            label = { Text("How are you feeling?") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
        )
        
        Button(
            onClick = { 
                if (text.isNotBlank()) {
                    viewModel.addJournalEntry(text)
                    text = ""
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save Encrypted Entry")
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        LazyColumn {
            items(entries) { entry ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(entry.content, style = MaterialTheme.typography.bodyLarge)
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(sdf.format(Date(entry.timestamp)), style = MaterialTheme.typography.labelSmall)
                            Text(entry.moodTag, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }
        }
    }
}
