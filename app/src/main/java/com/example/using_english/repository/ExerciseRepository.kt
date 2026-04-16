package com.example.using_english.repository

import android.content.Context
import com.example.using_english.data.ExerciseDao
import com.example.using_english.data.ExerciseEntity
import com.example.using_english.data.UserStatsEntity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

class ExerciseRepository(
    private val context: Context,
    private val exerciseDao: ExerciseDao
) {
    val allExercises: Flow<List<ExerciseEntity>> = exerciseDao.getAllExercises()
    val userStats: Flow<UserStatsEntity?> = exerciseDao.getUserStats()
    
    fun getExercisesByLevelAndCategory(level: String, category: String): Flow<List<ExerciseEntity>> {
        val categories = if (category == "Use of English") {
            listOf("Multiple-choice cloze", "Open cloze", "Word formation")
        } else {
            listOf(category)
        }
        return exerciseDao.getExercisesByLevelAndCategories(level, categories)
    }

    suspend fun getExerciseById(id: String): ExerciseEntity? = withContext(Dispatchers.IO) {
        exerciseDao.getExerciseById(id)
    }

    suspend fun updateExercise(exercise: ExerciseEntity) = withContext(Dispatchers.IO) {
        exerciseDao.updateExercise(exercise)
    }

    suspend fun updateUserStats(stats: UserStatsEntity) = withContext(Dispatchers.IO) {
        exerciseDao.insertUserStats(stats)
    }

    suspend fun checkAndPrepopulateDatabase() {
        withContext(Dispatchers.IO) {
            try {
                android.util.Log.d("ExerciseRepository", "Checking database...")
                val currentExercises = allExercises.first()
                android.util.Log.d("ExerciseRepository", "Database contains ${currentExercises.size} exercises")
                if (currentExercises.isEmpty()) {
                    android.util.Log.d("ExerciseRepository", "Prepopulating from JSON...")
                    val jsonString = try {
                        context.assets.open("extracted_exercises.json")
                            .bufferedReader()
                            .use { it.readText() }
                    } catch (e: Exception) {
                        android.util.Log.e("ExerciseRepository", "Error reading assets file", e)
                        throw e
                    }
                    
                    val listType = object : TypeToken<List<ExerciseEntity>>() {}.type
                    val exercises: List<ExerciseEntity> = try {
                        Gson().fromJson(jsonString, listType)
                    } catch (e: Exception) {
                        android.util.Log.e("ExerciseRepository", "Error parsing JSON content", e)
                        throw e
                    }
                    
                    android.util.Log.d("ExerciseRepository", "Parsed ${exercises.size} exercises. Inserting...")
                    if (exercises.isNotEmpty()) {
                        exerciseDao.insertAll(exercises)
                        android.util.Log.d("ExerciseRepository", "Insertion complete")
                    } else {
                        android.util.Log.w("ExerciseRepository", "No exercises found in JSON!")
                    }
                    
                    // Initialize stats if not present
                    val currentStats = userStats.first()
                    if (currentStats == null) {
                        android.util.Log.d("ExerciseRepository", "Initializing user stats...")
                        exerciseDao.insertUserStats(UserStatsEntity())
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("ExerciseRepository", "CRITICAL ERROR during prepopulation", e)
                // Rethrow or handle safely. Currently MainViewModel catches it.
            }
        }
    }

    suspend fun markExerciseAsResolved(exercise: ExerciseEntity) {
        withContext(Dispatchers.IO) {
            exerciseDao.updateExercise(exercise.copy(isResolved = true))
            
            // Update total resolved count in stats
            val currentStats = userStats.first() ?: UserStatsEntity()
            exerciseDao.insertUserStats(
                currentStats.copy(
                    totalResolved = currentStats.totalResolved + 1,
                    lastResolutionDate = System.currentTimeMillis()
                )
            )
        }
    }
}
