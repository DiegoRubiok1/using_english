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
}
