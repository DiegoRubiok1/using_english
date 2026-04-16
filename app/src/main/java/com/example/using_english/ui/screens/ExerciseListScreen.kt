package com.example.using_english.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.using_english.data.ExerciseEntity
import com.example.using_english.ui.theme.Using_englishTheme
import com.example.using_english.viewmodel.MainViewModel
import kotlinx.coroutines.flow.flowOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseListScreen(
    level: String,
    category: String,
    viewModel: MainViewModel,
    onExerciseSelected: (String) -> Unit,
    onBack: () -> Unit
) {
    val exercises by viewModel.getExercises(level, category).collectAsState(initial = emptyList())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("$level - $category") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        if (exercises.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(exercises) { exercise ->
                    ExerciseCard(exercise, onExerciseSelected)
                }
            }
        }
    }
}

@Composable
fun ExerciseCard(exercise: ExerciseEntity, onExerciseSelected: (String) -> Unit) {
    val statusColor = when {
        exercise.isResolved -> Color(0xFF4CAF50) // Positive - Solved
        exercise.lastAttemptedAnswer != null -> Color(0xFFF44336) // Negative - Attempted
        else -> MaterialTheme.colorScheme.surfaceVariant // Standard - Not attempted
    }

    val contentColor = when {
        exercise.isResolved || exercise.lastAttemptedAnswer != null -> Color.White
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onExerciseSelected(exercise.exercise) },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = statusColor,
            contentColor = contentColor
        )
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = exercise.exercise,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = exercise.exercise_type,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            
            if (exercise.isResolved) {
                Icon(Icons.Default.CheckCircle, contentDescription = "Solved")
            } else if (exercise.lastAttemptedAnswer != null) {
                Icon(Icons.Default.Warning, contentDescription = "Attempted")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ExerciseCardPreview() {
    Using_englishTheme {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            ExerciseCard(
                exercise = ExerciseEntity(
                    exercise = "C1A4-T1-P1-Q1",
                    prompt = "Sample prompt",
                    options = listOf("A", "B", "C", "D"),
                    solution = "D",
                    source_file = "file.pdf",
                    page = 10,
                    exercise_type = "Multiple-choice cloze",
                    confidence = 1.0,
                    isResolved = false
                ),
                onExerciseSelected = {}
            )
            ExerciseCard(
                exercise = ExerciseEntity(
                    exercise = "C1A4-T1-P1-Q2",
                    prompt = "Sample prompt",
                    options = listOf("A", "B", "C", "D"),
                    solution = "A",
                    source_file = "file.pdf",
                    page = 10,
                    exercise_type = "Multiple-choice cloze",
                    confidence = 1.0,
                    isResolved = true
                ),
                onExerciseSelected = {}
            )
            ExerciseCard(
                exercise = ExerciseEntity(
                    exercise = "C1A4-T1-P1-Q3",
                    prompt = "Sample prompt",
                    options = listOf("A", "B", "C", "D"),
                    solution = "B",
                    source_file = "file.pdf",
                    page = 10,
                    exercise_type = "Multiple-choice cloze",
                    confidence = 1.0,
                    isResolved = false,
                    lastAttemptedAnswer = "C"
                ),
                onExerciseSelected = {}
            )
        }
    }
}
