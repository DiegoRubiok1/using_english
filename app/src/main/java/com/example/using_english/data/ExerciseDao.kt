package com.example.using_english.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ExerciseDao {
    @Query("SELECT * FROM exercises")
    fun getAllExercises(): Flow<List<ExerciseEntity>>

    @Query("SELECT * FROM exercises WHERE exercise_type IN (:categories) AND exercise LIKE :level || '%'")
    fun getExercisesByLevelAndCategories(level: String, categories: List<String>): Flow<List<ExerciseEntity>>

    @Query("SELECT * FROM exercises WHERE exercise = :id")
    suspend fun getExerciseById(id: String): ExerciseEntity?

    @Query("""
        SELECT * FROM exercises 
        WHERE isResolved = 0 
        AND exercise LIKE :levelPrefix || '%' 
        AND exerciseNumber = :exerciseNumber
        AND questionNumber > :currentQuestionNumber
        ORDER BY questionNumber ASC 
        LIMIT 1
    """)
    suspend fun getNextUnresolvedExerciseInPart(levelPrefix: String, exerciseNumber: Int, currentQuestionNumber: Int): ExerciseEntity?

    @Query("SELECT * FROM exercises WHERE level = :level AND exerciseNumber = :exerciseNumber ORDER BY questionNumber ASC")
    suspend fun getExercisesByPart(level: String, exerciseNumber: Int): List<ExerciseEntity>

    @Query("SELECT * FROM exercises WHERE exercise LIKE :blockId || '%' ORDER BY questionNumber ASC")
    suspend fun getExercisesByBlock(blockId: String): List<ExerciseEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(exercises: List<ExerciseEntity>)

    @Update
    suspend fun updateExercise(exercise: ExerciseEntity)

    @Query("SELECT COUNT(*) FROM exercises WHERE isResolved = 1")
    suspend fun getResolvedCount(): Int

    // User Stats
    @Query("SELECT * FROM user_stats WHERE id = 0")
    fun getUserStats(): Flow<UserStatsEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserStats(stats: UserStatsEntity)

    @Query("UPDATE exercises SET isResolved = 0, lastAttemptedAnswer = NULL")
    suspend fun resetAllProgress()

    @Query("UPDATE user_stats SET totalResolved = 0 WHERE id = 0")
    suspend fun resetUserStats()
}
