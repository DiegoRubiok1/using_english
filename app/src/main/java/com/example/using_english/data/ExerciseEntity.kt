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
    val lastAttemptedAnswer: String? = null,
    
    // New fields for logical grouping and numbering
    val level: String = "",           // e.g., "C1" or "B2"
    val exerciseNumber: Int = 0,      // Sequential number of the block (Part) per level
    val questionNumber: Int = 0       // Sequential number of the gap within that block
)
