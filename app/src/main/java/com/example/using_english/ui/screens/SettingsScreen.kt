package com.example.using_english.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Brightness4
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.using_english.ui.theme.Using_englishTheme
import com.example.using_english.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(viewModel: MainViewModel) {
    val isBlackTheme by viewModel.isBlackTheme.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Settings") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Brightness4, contentDescription = null)
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                text = "Pure Black Theme",
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Text(
                                text = "Use true black for dark mode",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                    Switch(
                        checked = isBlackTheme,
                        onCheckedChange = { viewModel.setBlackTheme(it) }
                    )
                }
            }
        }
    }
}
