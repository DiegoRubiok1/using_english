package com.example.using_english.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.using_english.data.UserStatsEntity
import com.example.using_english.ui.theme.Using_englishTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(userStats: UserStatsEntity?) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Welcome!",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "${userStats?.streak ?: 0}",
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = "Day streak",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            StatCard(
                title = "Resolved",
                value = "${userStats?.totalResolved ?: 0}",
                modifier = Modifier.weight(1f)
            )
            StatCard(
                title = "Level",
                value = "C1",
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun StatCard(title: String, value: String, modifier: Modifier = Modifier) {
    ElevatedCard(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    Using_englishTheme {
        HomeScreen(UserStatsEntity(streak = 5, totalResolved = 120))
    }
}
