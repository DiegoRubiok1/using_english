package com.example.using_english.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.using_english.ui.theme.Using_englishTheme

data class LevelItem(val level: String, val color: Color, val enabled: Boolean = false)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExercisesScreen(onLevelSelected: (String) -> Unit) {
    val levels = listOf(
        LevelItem("A1", Color(0xFF4CAF50)),
        LevelItem("A2", Color(0xFF8BC34A)),
        LevelItem("B1", Color(0xFFFFC107)),
        LevelItem("B2", Color(0xFFFF9800)),
        LevelItem("C1", Color(0xFFF44336), enabled = true),
        LevelItem("C2", Color(0xFFB71C1C))
    )

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Select Level") })
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(levels) { levelItem ->
                LevelCard(levelItem, onLevelSelected)
            }
        }
    }
}

@Composable
fun LevelCard(levelItem: LevelItem, onLevelSelected: (String) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = levelItem.enabled) { onLevelSelected(levelItem.level) },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (levelItem.enabled) MaterialTheme.colorScheme.surfaceVariant 
                             else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(levelItem.color, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = levelItem.level,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleLarge
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "Level ${levelItem.level}",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = if (levelItem.enabled) MaterialTheme.colorScheme.onSurface 
                        else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ExercisesScreenPreview() {
    Using_englishTheme {
        ExercisesScreen(onLevelSelected = {})
    }
}
