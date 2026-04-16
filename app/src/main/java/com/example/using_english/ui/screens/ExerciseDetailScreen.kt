package com.example.using_english.ui.screens

import com.example.using_english.data.ExerciseEntity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.using_english.viewmodel.MainViewModel
import nl.dionsegijn.konfetti.compose.KonfettiView
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.emitter.Emitter
import java.util.concurrent.TimeUnit
import androidx.compose.ui.window.Dialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseDetailScreen(
    exerciseId: String,
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    val exercise by viewModel.currentExercise.collectAsState()
    val nextExerciseId by viewModel.nextExerciseId.collectAsState()
    var userAnswer by remember { mutableStateOf("") }
    var showResultDialog by remember { mutableStateOf(false) }
    var isCorrect by remember { mutableStateOf(false) }

    LaunchedEffect(exerciseId) {
        viewModel.loadExercise(exerciseId)
    }

    // Reset state when exercise changes
    LaunchedEffect(exercise) {
        userAnswer = exercise?.lastAttemptedAnswer ?: ""
        showResultDialog = false
    }

    // Extract Gap and Transformation information from prompt and clean it
    val gapRegex = remember { """\n\n(Gap\s+\d+\.?)$""".toRegex(RegexOption.IGNORE_CASE) }
    val transformRegex = remember { """\n\nWord to transform:\s*(.*)\s*\((Gap\s+\d+)\)""".toRegex(RegexOption.IGNORE_CASE) }
    
    val gapMatch = remember(exercise?.prompt) { exercise?.prompt?.let { gapRegex.find(it) } }
    val transformMatch = remember(exercise?.prompt) { exercise?.prompt?.let { transformRegex.find(it) } }
    
    val gapText = gapMatch?.groupValues?.get(1) ?: transformMatch?.groupValues?.get(2)
    val transformWord = transformMatch?.groupValues?.get(1)

    val cleanedPrompt = remember(exercise?.prompt, gapMatch, transformMatch) {
        exercise?.prompt?.let { prompt ->
            var cleaned = prompt
            transformMatch?.let { cleaned = cleaned.removeRange(it.range).trim() }
            gapMatch?.let { cleaned = cleaned.removeRange(it.range).trim() }
            cleaned
        } ?: ""
    }

    Scaffold(
        topBar = {
            val title = if (gapText != null) "${formatExerciseTitle(exercise)} ($gapText)" else formatExerciseTitle(exercise)

            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        exercise?.let { ex ->
            Box(modifier = Modifier.fillMaxSize().padding(padding)) {
                Column(modifier = Modifier.fillMaxSize()) {
                    // 70% Height - Content
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(0.7f)
                            .padding(16.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        ExerciseContent(cleanedPrompt)
                    }

                    HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)

                    // 30% Height - Input/Options
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(0.3f)
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            if (transformWord != null) {
                                Card(
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                                    )
                                ) {
                                    Column(
                                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = "Word to transform:",
                                            style = MaterialTheme.typography.labelMedium,
                                            color = MaterialTheme.colorScheme.onSecondaryContainer
                                        )
                                        Text(
                                            text = transformWord,
                                            style = MaterialTheme.typography.headlineSmall,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = MaterialTheme.colorScheme.onSecondaryContainer
                                        )
                                    }
                                }
                            }

                            if (ex.options.isNotEmpty()) {
                                OptionsGrid(
                                    options = ex.options,
                                    onOptionSelected = { selected ->
                                        userAnswer = selected
                                        val solutions = ex.solution.split("/").map { it.trim() }
                                        isCorrect = solutions.any { it.equals(selected.trim(), ignoreCase = true) }
                                        viewModel.submitAnswer(ex, selected)
                                        showResultDialog = true
                                    }
                                )
                            } else {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    OutlinedTextField(
                                        value = userAnswer,
                                        onValueChange = { userAnswer = it },
                                        label = { Text("Your Answer") },
                                        modifier = Modifier.weight(1f),
                                        singleLine = true
                                    )
                                    Button(
                                        onClick = {
                                            val solutions = ex.solution.split("/").map { it.trim() }
                                            isCorrect = solutions.any { it.equals(userAnswer.trim(), ignoreCase = true) }
                                            viewModel.submitAnswer(ex, userAnswer)
                                            showResultDialog = true
                                        },
                                        enabled = userAnswer.isNotBlank()
                                    ) {
                                        Text("Submit")
                                    }
                                }
                            }
                        }
                    }
                }

                if (showResultDialog) {
                    ResultDialog(
                        isCorrect = isCorrect,
                        solution = ex.solution,
                        nextExerciseId = nextExerciseId,
                        onDismiss = { 
                            showResultDialog = false 
                            // Si es la última pregunta de la parte, volvemos a la lista
                            if (nextExerciseId == null) {
                                onBack()
                            }
                        },
                        onNextExercise = { id ->
                            viewModel.loadExercise(id)
                            showResultDialog = false
                        }
                    )
                    
                    if (isCorrect) {
                        KonfettiView(
                            modifier = Modifier.fillMaxSize(),
                            parties = listOf(
                                Party(
                                    speed = 0f,
                                    maxSpeed = 30f,
                                    damping = 0.9f,
                                    spread = 360,
                                    colors = listOf(0xfce18a, 0xff726d, 0xf4133, 0x1fbb11),
                                    position = Position.Relative(0.5, 0.3),
                                    emitter = Emitter(duration = 100, TimeUnit.MILLISECONDS).max(100)
                                )
                            )
                        )
                    }
                }
            }
        } ?: Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }
}

@Composable
fun OptionsGrid(
    options: List<String>,
    onOptionSelected: (String) -> Unit
) {
    val optionLabels = listOf("A", "B", "C", "D")
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        // 2x2 Grid
        for (i in 0 until 2) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                for (j in 0 until 2) {
                    val index = i * 2 + j
                    if (index < options.size) {
                        val label = optionLabels[index]
                        Button(
                            onClick = { onOptionSelected(label) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("($label) ${options[index]}")
                        }
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
fun ResultDialog(
    isCorrect: Boolean,
    solution: String,
    nextExerciseId: String? = null,
    onDismiss: () -> Unit,
    onNextExercise: (String) -> Unit = {}
) {
    val isDark = isSystemInDarkTheme()

    // Theme-aware color selection
    val backgroundColor = if (isCorrect) {
        if (isDark) Color(0xFF00390A) else Color(0xFFE8F5E9)
    } else {
        if (isDark) Color(0xFF410002) else Color(0xFFFFEBEE)
    }

    val contentColor = if (isCorrect) {
        if (isDark) Color(0xFFB1F4AF) else Color(0xFF2E7D32)
    } else {
        if (isDark) Color(0xFFFFDAD6) else Color(0xFFC62828)
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = backgroundColor,
                contentColor = contentColor
            )
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = if (isCorrect) "¡Correct!" else "Incorrect",
                    style = MaterialTheme.typography.headlineSmall,
                    color = contentColor,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (nextExerciseId != null) {
                        // If correct, show Close to allow staying on screen, otherwise only show Next
                        if (isCorrect) {
                            OutlinedButton(
                                onClick = onDismiss,
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = contentColor)
                            ) {
                                Text("Close")
                            }
                        }
                        
                        Button(
                            onClick = { onNextExercise(nextExerciseId) },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = contentColor,
                                contentColor = backgroundColor
                            )
                        ) {
                            Text("Next")
                        }
                    } else {
                        // Last exercise
                        Button(
                            onClick = { onDismiss() },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = contentColor,
                                contentColor = backgroundColor
                            )
                        ) {
                            Text("Finish Test")
                        }
                    }
                }
            }
        }
    }
}


fun formatExerciseTitle(exercise: ExerciseEntity?): String {
    if (exercise == null) return "Loading..."
    val idParts = exercise.exercise.split("-")
    val testName = idParts.getOrNull(1)?.removePrefix("T") ?: "?"
    val partName = idParts.getOrNull(2)?.removePrefix("P") ?: "?"
    return "Test $testName, Part $partName, Question ${exercise.questionNumber}"
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
