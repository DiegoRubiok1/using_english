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
            listOf("Multiple-choice cloze", "Open cloze", "Word formation", "Key word transformation")
        } else {
            listOf(category)
        }
        return exerciseDao.getExercisesByLevelAndCategories(level, categories)
    }

    suspend fun getExerciseById(id: String): ExerciseEntity? = withContext(Dispatchers.IO) {
        exerciseDao.getExerciseById(id)
    }

    suspend fun getNextUnresolvedExercise(levelPrefix: String, currentId: String): ExerciseEntity? = withContext(Dispatchers.IO) {
        null // Legacy
    }

    suspend fun getNextUnresolvedExerciseInPart(levelPrefix: String, exerciseNumber: Int, questionNumber: Int): ExerciseEntity? = withContext(Dispatchers.IO) {
        exerciseDao.getNextUnresolvedExerciseInPart(levelPrefix, exerciseNumber, questionNumber)
    }

    suspend fun getExercisesByPart(level: String, exerciseNumber: Int): List<ExerciseEntity> = withContext(Dispatchers.IO) {
        exerciseDao.getExercisesByPart(level, exerciseNumber)
    }

    suspend fun updateExercise(exercise: ExerciseEntity) = withContext(Dispatchers.IO) {
        exerciseDao.updateExercise(exercise)
    }

    suspend fun updateUserStats(stats: UserStatsEntity) = withContext(Dispatchers.IO) {
        exerciseDao.insertUserStats(stats)
    }

    suspend fun resetAllData() = withContext(Dispatchers.IO) {
        exerciseDao.resetAllProgress()
        exerciseDao.resetUserStats()
    }

    suspend fun checkAndPrepopulateDatabase() {
        withContext(Dispatchers.IO) {
            try {
                android.util.Log.d("ExerciseRepository", "Checking database...")
                val currentExercises = allExercises.first()
                
                // Identify what's currently in the DB
                val existingExercises = currentExercises.map { it.exercise }.toSet()

                val assetFiles = listOf("extracted_exercises_c1.json", "extracted_exercises_b2.json")
                val allLoadedExercises = mutableListOf<ExerciseEntity>()
                val gson = Gson()
                val listType = object : TypeToken<List<ExerciseEntity>>() {}.type

                for (fileName in assetFiles) {
                    try {
                        android.util.Log.d("ExerciseRepository", "Checking $fileName...")
                        val jsonString = context.assets.open(fileName)
                            .bufferedReader()
                            .use { it.readText() }
                        
                        val rawExercises: List<ExerciseEntity> = gson.fromJson(jsonString, listType)
                        
                        // Process exercises to add sequence numbers
                        val level = if (fileName.contains("c1")) "C1" else "B2"
                        
                        // Group by "Block" (Book + Test + Part)
                        // Example ID: C1A4-T1-P1-Q1 -> Block: C1A4-T1-P1
                        val groupedByBlock = rawExercises.groupBy { 
                            val parts = it.exercise.split("-")
                            if (parts.size >= 3) parts.subList(0, 3).joinToString("-") else it.exercise
                        }

                        var exerciseCounter = 1
                        val processedExercises = mutableListOf<ExerciseEntity>()

                        groupedByBlock.forEach { (_, questions) ->
                            questions.forEachIndexed { index, q ->
                                processedExercises.add(
                                    q.copy(
                                        level = level,
                                        exerciseNumber = exerciseCounter,
                                        questionNumber = index + 1
                                    )
                                )
                            }
                            exerciseCounter++
                        }
                        
                        // Only add exercises that don't already exist in the DB
                        val newExercises = processedExercises.filter { it.exercise !in existingExercises }
                        if (newExercises.isNotEmpty()) {
                            allLoadedExercises.addAll(newExercises)
                            android.util.Log.d("ExerciseRepository", "Found ${newExercises.size} new exercises in $fileName ($level)")
                        }
                    } catch (e: Exception) {
                        android.util.Log.e("ExerciseRepository", "Error loading $fileName", e)
                    }
                }

                if (allLoadedExercises.isNotEmpty()) {
                    android.util.Log.d("ExerciseRepository", "Inserting total ${allLoadedExercises.size} new exercises...")
                    exerciseDao.insertAll(allLoadedExercises)
                    android.util.Log.d("ExerciseRepository", "Insertion complete")
                }

                // Initialize stats if not present
                val currentStats = userStats.first()
                if (currentStats == null) {
                    android.util.Log.d("ExerciseRepository", "Initializing user stats...")
                    exerciseDao.insertUserStats(UserStatsEntity())
                }
            } catch (e: Exception) {
                android.util.Log.e("ExerciseRepository", "CRITICAL ERROR during prepopulation", e)
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
