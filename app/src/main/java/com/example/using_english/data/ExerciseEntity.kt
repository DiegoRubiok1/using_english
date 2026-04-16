package com.example.using_english.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "exercises")
data class ExerciseEntity(
    @PrimaryKey val exercise: String, // e.g., "C1A4-T1-P1-Q1"
    val prompt: String,
    val options: List<String>,
    val solution: String,
    val source_file: String,
    val page: Int,
    val exercise_type: String,
    val confidence: Double,
    val isResolved: Boolean = false,
    val lastAttemptedAnswer: String? = null
)
