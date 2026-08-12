package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.GenerationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GenerationDao {
    @Query("SELECT * FROM generations ORDER BY timestamp DESC")
    fun getAllGenerations(): Flow<List<GenerationEntity>>

    @Query("SELECT * FROM generations WHERE isFavorite = 1 ORDER BY timestamp DESC")
    fun getFavoriteGenerations(): Flow<List<GenerationEntity>>

    @Query("SELECT * FROM generations WHERE id = :id")
    suspend fun getGenerationById(id: Long): GenerationEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGeneration(generation: GenerationEntity): Long

    @Update
    suspend fun updateGeneration(generation: GenerationEntity)

    @Delete
    suspend fun deleteGeneration(generation: GenerationEntity)

    @Query("DELETE FROM generations WHERE id = :id")
    suspend fun deleteGenerationById(id: Long)
}
