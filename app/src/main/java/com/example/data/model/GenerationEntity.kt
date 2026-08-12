package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "generations")
data class GenerationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val prompt: String,
    val negativePrompt: String = "",
    val originalImagePath: String,
    val generatedImagePath: String,
    val styleName: String,
    val denoisingStrength: Float,
    val aspectRatio: String,
    val engineUsed: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isFavorite: Boolean = false
)
