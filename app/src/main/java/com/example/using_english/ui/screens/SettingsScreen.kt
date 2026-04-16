package com.example.using_english.ui.screens

import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.example.using_english.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(viewModel: MainViewModel) {
    val isBlackTheme by viewModel.isBlackTheme.collectAsState()
    val context = LocalContext.current

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
            Text(
                text = "Appearance",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

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
                    Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Brightness4, contentDescription = null)
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                text = "Pure Black Theme",
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Text(
                                text = "Use true black for dark mode (OLED)",
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

            Text(
                text = "About & Updates",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW,
                        "https://github.com/DiegoRubiok1/using-english/releases".toUri())
                    context.startActivity(intent)
                }
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.SystemUpdate, contentDescription = null)
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = "Check for Updates",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = "Download the latest version from GitHub",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW,
                        "https://github.com/DiegoRubiok1/using-english".toUri())
                    context.startActivity(intent)
                }
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Code, contentDescription = null)
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = "Project Repository",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = "View source code on GitHub",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Info, contentDescription = null)
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = "Version",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = "1.2.0-beta (C1/B2 Support)",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }
}
