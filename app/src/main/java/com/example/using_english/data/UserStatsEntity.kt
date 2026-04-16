package com.example.using_english.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_stats")
data class UserStatsEntity(
    @PrimaryKey val id: Int = 0,
    val streak: Int = 0,
    val totalResolved: Int = 0,
    val lastResolutionDate: Long = 0, // Timestamp to calculate streak
    val isBlackTheme: Boolean = false
)
