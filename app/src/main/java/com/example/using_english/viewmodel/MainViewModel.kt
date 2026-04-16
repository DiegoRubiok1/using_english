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

    fun loadExercise(id: String) {
        viewModelScope.launch {
            val exercise = repository.getExerciseById(id)
            _currentExercise.value = exercise
            _nextExerciseId.value = null 
            
            exercise?.let {
                val levelPrefix = it.exercise.takeWhile { char -> char != '-' }
                // Look specifically for the next unresolved exercise FORWARD in the list
                val next = repository.getNextUnresolvedExercise(levelPrefix, it.exercise)
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
            // Always refresh based on current exercise position
            val next = repository.getNextUnresolvedExercise(levelPrefix, exercise.exercise)
            _nextExerciseId.value = next?.exercise
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
