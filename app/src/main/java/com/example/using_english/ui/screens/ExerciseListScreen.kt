package com.example.using_english.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import nl.dionsegijn.konfetti.compose.KonfettiView
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.emitter.Emitter
import java.util.concurrent.TimeUnit

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
    val sessionSummary by viewModel.sessionSummary.collectAsState()

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
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when {
                exercises.isEmpty() -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator()
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Loading exercises...")
                        }
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(exercises) { exercise ->
                            ExerciseCard(exercise, onExerciseSelected)
                        }
                    }
                }
            }

            if (sessionSummary != null) {
                Box(modifier = Modifier.fillMaxSize()) {
                    SessionSummaryDialog(
                        summary = sessionSummary!!,
                        onDismiss = { viewModel.clearSessionSummary() }
                    )
                    
                    if (sessionSummary!!.grade.contains("Grade A")) {
                        KonfettiView(
                            modifier = Modifier.fillMaxSize(),
                            parties = listOf(
                                Party(
                                    speed = 0f,
                                    maxSpeed = 30f,
                                    damping = 0.9f,
                                    spread = 360,
                                    colors = listOf(0xfce18a, 0xff726d, 0xf42e33, 0x1fb711),
                                    position = Position.Relative(0.5, 0.3),
                                    emitter = Emitter(duration = 100, TimeUnit.MILLISECONDS).max(100)
                                )
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SessionSummaryDialog(
    summary: MainViewModel.SessionSummary,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = { Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = Color(0xFFFFD700), modifier = Modifier.size(48.dp)) },
        title = { 
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Study Session Complete!", style = MaterialTheme.typography.headlineSmall)
                Text("Test Results", style = MaterialTheme.typography.bodyMedium)
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    ScoreStat(label = "Correct", value = "${summary.correct}/${summary.total}", color = Color(0xFF4CAF50))
                    ScoreStat(label = "Cambridge Score", value = summary.score.toString(), color = MaterialTheme.colorScheme.primary)
                }

                HorizontalDivider()

                Text(
                    text = summary.grade,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.secondary
                )
                
                Text(
                    text = "Based on Cambridge English Scale standards.",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {
            Button(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) {
                Text("Great!")
            }
        }
    )
}

@Composable
fun ScoreStat(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, style = MaterialTheme.typography.labelMedium)
        Text(text = value, style = MaterialTheme.typography.headlineMedium, color = color, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun ExerciseCard(exercise: ExerciseEntity, onExerciseSelected: (String) -> Unit) {
    // Extraemos Test y Part del ID del ejercicio (ej: C1A4-T1-P2-Q1 -> T1, P2)
    val idParts = exercise.exercise.split("-")
    val testName = idParts.getOrNull(1) ?: "T?"
    val partName = idParts.getOrNull(2) ?: "P?"

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
                    text = "Test ${testName.removePrefix("T")}, Part ${partName.removePrefix("P")}, Question ${exercise.questionNumber}",
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
