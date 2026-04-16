package com.example.using_english.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.using_english.data.ExerciseEntity
import com.example.using_english.data.UserStatsEntity
import com.example.using_english.repository.ExerciseRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MainViewModel(private val repository: ExerciseRepository) : ViewModel() {

    val userStats: StateFlow<UserStatsEntity?> = repository.userStats
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    val isBlackTheme: StateFlow<Boolean> = repository.userStats
        .map { it?.isBlackTheme ?: false }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

    fun setBlackTheme(enabled: Boolean) {
        viewModelScope.launch {
            val currentStats = userStats.value ?: UserStatsEntity()
            repository.updateUserStats(currentStats.copy(isBlackTheme = enabled))
        }
    }

    fun getExercises(level: String, category: String): Flow<List<ExerciseEntity>> {
        return repository.getExercisesByLevelAndCategory(level, category)
    }

    private val _currentExercise = MutableStateFlow<ExerciseEntity?>(null)
    val currentExercise: StateFlow<ExerciseEntity?> = _currentExercise.asStateFlow()

    private val _nextExerciseId = MutableStateFlow<String?>(null)
    val nextExerciseId: StateFlow<String?> = _nextExerciseId.asStateFlow()

    private val _sessionSummary = MutableStateFlow<SessionSummary?>(null)
    val sessionSummary: StateFlow<SessionSummary?> = _sessionSummary.asStateFlow()

    fun clearSessionSummary() {
        _sessionSummary.value = null
    }

    data class SessionSummary(
        val correct: Int,
        val total: Int,
        val level: String,
        val score: Int,
        val grade: String,
        val questions: List<QuestionResult>
    )

    data class QuestionResult(
        val questionNumber: Int,
        val isCorrect: Boolean,
        val userAnswer: String,
        val solution: String
    )

    fun loadExercise(id: String) {
        viewModelScope.launch {
            val exercise = repository.getExerciseById(id)
            _currentExercise.value = exercise
            _nextExerciseId.value = null 
            
            exercise?.let {
                val levelPrefix = it.exercise.takeWhile { char -> char != '-' }
                // Buscamos la siguiente pregunta NO RESUELTA con un questionNumber estrictamente superior
                val next = repository.getNextUnresolvedExerciseInPart(levelPrefix, it.exerciseNumber, it.questionNumber)
                _nextExerciseId.value = next?.exercise
            }
        }
    }

    fun submitAnswer(exercise: ExerciseEntity, answer: String) {
        viewModelScope.launch {
            val solutions = exercise.solution.split("/").map { it.trim() }
            val isCorrect = solutions.any { it.equals(answer.trim(), ignoreCase = true) }
            val updatedExercise = exercise.copy(
                isResolved = isCorrect,
                lastAttemptedAnswer = answer
            )
            repository.updateExercise(updatedExercise)
            
            if (isCorrect) {
                repository.markExerciseAsResolved(updatedExercise)
            }
            
            val levelPrefix = exercise.exercise.takeWhile { char -> char != '-' }
            val next = repository.getNextUnresolvedExerciseInPart(levelPrefix, exercise.exerciseNumber, exercise.questionNumber)
            _nextExerciseId.value = next?.exercise
            
            // Si no hay siguiente, calculamos el resumen de la parte
            if (next == null) {
                calculateSessionSummary(exercise.level, exercise.exerciseNumber)
            }
        }
    }

    private fun calculateSessionSummary(level: String, exerciseNumber: Int) {
        viewModelScope.launch {
            val allQuestionsInPart = repository.getExercisesByPart(level, exerciseNumber)
            val correct = allQuestionsInPart.count { it.isResolved }
            val total = allQuestionsInPart.size
            
            val questionResults = allQuestionsInPart.map {
                QuestionResult(
                    questionNumber = it.questionNumber,
                    isCorrect = it.isResolved,
                    userAnswer = it.lastAttemptedAnswer ?: "-",
                    solution = it.solution
                )
            }
            
            val percentage = (correct.toFloat() / total.toFloat() * 100).toInt()
            
            // Cambridge English Scale mapping (approximate for Use of English)
            val (score, grade) = when (level) {
                "C1" -> when {
                    percentage >= 80 -> (190 + (percentage - 80) * 1) to "Grade A (C2 Level)"
                    percentage >= 75 -> (180 + (percentage - 75) * 2) to "Grade B (C1 Level)"
                    percentage >= 60 -> (160 + (percentage - 60) * 1.3).toInt() to "Grade C (C1 Level)"
                    percentage >= 45 -> (142 + (percentage - 45) * 1.2).toInt() to "B2 Level"
                    else -> (120 + percentage) to "Below B2"
                }
                else -> when { // B2
                    percentage >= 80 -> (180 + (percentage - 80) * 1) to "Grade A (C1 Level)"
                    percentage >= 75 -> (173 + (percentage - 75) * 1.4).toInt() to "Grade B (B2 Level)"
                    percentage >= 60 -> (160 + (percentage - 60) * 0.8).toInt() to "Grade C (B2 Level)"
                    percentage >= 45 -> (140 + (percentage - 45) * 1.3).toInt() to "B1 Level"
                    else -> (120 + percentage) to "Below B1"
                }
            }

            _sessionSummary.value = SessionSummary(correct, total, level, score, grade, questionResults)
        }
    }

    init {
        viewModelScope.launch {
            try {
                android.util.Log.d("MainViewModel", "Starting database prepopulation")
                repository.checkAndPrepopulateDatabase()
                android.util.Log.d("MainViewModel", "Database prepopulation finished")
            } catch (e: Exception) {
                android.util.Log.e("MainViewModel", "Error prepopulating database", e)
            }
        }
    }
}

class MainViewModelFactory(private val repository: ExerciseRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
