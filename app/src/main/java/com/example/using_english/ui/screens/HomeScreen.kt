package com.example.using_english.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.using_english.data.UserStatsEntity
import com.example.using_english.ui.theme.Using_englishTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(userStats: UserStatsEntity?) {
    var selectedGuide by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Welcome!",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // ... (Statistics Section - No changes needed inside these cards)
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

        // --- GUIDES SECTION ---
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Guides & Tips",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.Start)
        )

        GuideCard(
            title = "Mastering Use of English",
            description = "Learn how the scoring works and tips for C1/B2 exams.",
            icon = Icons.Default.AutoStories,
            onClick = { selectedGuide = "use_of_english" }
        )

        GuideCard(
            title = "Study Strategy",
            description = "How to use this app effectively for sequential learning.",
            icon = Icons.Default.Lightbulb,
            onClick = { selectedGuide = "strategy" }
        )
    }

    if (selectedGuide == "use_of_english") {
        GuideDialog(
            onDismiss = { selectedGuide = null },
            title = "Use of English Guide",
            content = {
                UseOfEnglishContent()
            }
        )
    } else if (selectedGuide == "strategy") {
        GuideDialog(
            onDismiss = { selectedGuide = null },
            title = "Sequential Learning",
            content = {
                Text("This app follows a forward-only sequential path within each Part. Once you start a Part (e.g., Test 1, Part 1), you should complete all questions in order to get your Cambridge Scale result.")
            }
        )
    }
}

@Composable
fun GuideCard(title: String, description: String, icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(text = description, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
fun GuideDialog(onDismiss: () -> Unit, title: String, content: @Composable () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Got it") }
        },
        title = { Text(title) },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                content()
            }
        }
    )
}

@Composable
fun UseOfEnglishContent() {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("What is Use of English?", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        Text("It's the part of the Cambridge exam that tests your grammar and vocabulary knowledge in context.")
        
        Text("The Scoring System", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        Text("Cambridge uses a scale from 120 to 210. In this app, we calculate your score based on the official percentages for C1 Advanced and B2 First.")
        
        Text("Parts in this App", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        Text("Each 'Part' you select represents a full block from an actual exam (e.g., all 8 questions of Part 1). We recommend finishing the entire block to get an accurate score.")

        Text("Pro Tip", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        Text("Don't just guess. If you fail, look at the correct answer and try to understand the 'collocation' or grammatical rule behind it.")
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
