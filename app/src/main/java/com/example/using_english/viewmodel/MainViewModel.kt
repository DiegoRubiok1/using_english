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

    fun loadExercise(id: String) {
        viewModelScope.launch {
            _currentExercise.value = repository.getExerciseById(id)
        }
    }

    fun submitAnswer(exercise: ExerciseEntity, answer: String) {
        viewModelScope.launch {
            val isCorrect = answer.equals(exercise.solution, ignoreCase = true)
            val updatedExercise = exercise.copy(
                isResolved = isCorrect,
                lastAttemptedAnswer = answer
            )
            repository.updateExercise(updatedExercise)
            
            if (isCorrect) {
                // If newly resolved, we could update stats here too if needed, 
                // but markExerciseAsResolved already does it.
                // Let's use the existing repository method for consistency if we want to update stats.
                repository.markExerciseAsResolved(updatedExercise)
            }
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
