package com.example.using_english.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "exercises")
data class ExerciseEntity(
    @PrimaryKey 
    @SerializedName("exercise")
    val exercise: String, // e.g., "C1A4-T1-P1-Q1"
    
    @SerializedName("prompt")
    val prompt: String,
    
    @SerializedName("options")
    val options: List<String>,
    
    @SerializedName("solution")
    val solution: String,
    
    @SerializedName("source_file")
    val source_file: String,
    
    @SerializedName("page")
    val page: Int,
    
    @SerializedName("exercise_type")
    val exercise_type: String,
    
    @SerializedName("confidence")
    val confidence: Double,

    val isResolved: Boolean = false,
    val lastAttemptedAnswer: String? = null,
    
    // New fields for logical grouping and numbering
    val level: String = "",           // e.g., "C1" or "B2"
    val exerciseNumber: Int = 0,      // Sequential number of the block (Part) per level
    val questionNumber: Int = 0       // Sequential number of the gap within that block
)
