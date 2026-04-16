package com.example.using_english.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.using_english.data.ExerciseEntity
import com.example.using_english.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseDetailScreen(
    exerciseId: String,
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    val exercise by viewModel.currentExercise.collectAsState()
    var userAnswer by remember { mutableStateOf("") }
    var showResult by remember { mutableStateOf(false) }

    LaunchedEffect(exerciseId) {
        viewModel.loadExercise(exerciseId)
    }

    // Reset state when exercise changes
    LaunchedEffect(exercise) {
        userAnswer = exercise?.lastAttemptedAnswer ?: ""
        showResult = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(exercise?.exercise ?: "Loading...") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        exercise?.let { ex ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Instructions & Prompt
                ExerciseContent(ex.prompt)

                Spacer(modifier = Modifier.height(8.dp))

                if (ex.options.isNotEmpty()) {
                    OptionsSelector(
                        options = ex.options,
                        selectedOption = userAnswer,
                        onOptionSelected = { userAnswer = it }
                    )
                } else {
                    OutlinedTextField(
                        value = userAnswer,
                        onValueChange = { userAnswer = it },
                        label = { Text("Your Answer") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }

                Button(
                    onClick = {
                        viewModel.submitAnswer(ex, userAnswer)
                        showResult = true
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = userAnswer.isNotBlank()
                ) {
                    Text("Submit")
                }

                if (showResult || ex.isResolved) {
                    val isCorrect = userAnswer.equals(ex.solution, ignoreCase = true)
                    val resultColor = if (isCorrect) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                    
                    Card(
                        colors = CardDefaults.cardColors(containerColor = resultColor.copy(alpha = 0.1f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = if (isCorrect) "Correct!" else "Incorrect",
                                style = MaterialTheme.typography.titleMedium,
                                color = resultColor,
                                fontWeight = FontWeight.Bold
                            )
                            if (!isCorrect) {
                                Text(
                                    text = "Correct solution: ${ex.solution}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }
            }
        } ?: Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }
}

@Composable
fun ExerciseContent(prompt: String) {
    // Simple split for now, can be improved to separate instructions from text
    val parts = prompt.split("\n\n")
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        parts.forEachIndexed { index, part ->
            Text(
                text = part.trim(),
                style = if (index == 0) MaterialTheme.typography.bodyMedium else MaterialTheme.typography.bodyLarge,
                fontWeight = if (index == 0) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}

@Composable
fun OptionsSelector(
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    val optionLabels = listOf("A", "B", "C", "D")
    Column(modifier = Modifier.selectableGroup(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        options.forEachIndexed { index, option ->
            val label = if (index < optionLabels.size) optionLabels[index] else ""
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .selectable(
                        selected = (label == selectedOption),
                        onClick = { onOptionSelected(label) },
                        role = Role.RadioButton
                    ),
                shape = RoundedCornerShape(8.dp),
                color = if (label == selectedOption) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = (label == selectedOption),
                        onClick = null // null because Surface handles click
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "($label) $option")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ExerciseContentPreview() {
    MaterialTheme {
        ExerciseContent(
            prompt = "For questions 1 – 8, read the text below and decide which answer (A, B, C or D) best fits each gap. \n\nCanoeist discovers unknown waterfall\nWe live in an age in which (0) …….. the entire planet has been documented and mapped."
        )
    }
}

@Preview(showBackground = true)
@Composable
fun OptionsSelectorPreview() {
    MaterialTheme {
        OptionsSelector(
            options = listOf("falling short of", "missing out on", "cutting down on", "running out of"),
            selectedOption = "A",
            onOptionSelected = {}
        )
    }
}
