package com.example.data.repository

import com.example.data.local.GenerationDao
import com.example.data.model.GenerationEntity
import kotlinx.coroutines.flow.Flow

class GenerationRepository(private val generationDao: GenerationDao) {
    val allGenerations: Flow<List<GenerationEntity>> = generationDao.getAllGenerations()
    val favoriteGenerations: Flow<List<GenerationEntity>> = generationDao.getFavoriteGenerations()

    suspend fun getById(id: Long): GenerationEntity? = generationDao.getGenerationById(id)

    suspend fun insert(generation: GenerationEntity): Long = generationDao.insertGeneration(generation)

    suspend fun update(generation: GenerationEntity) = generationDao.updateGeneration(generation)

    suspend fun delete(generation: GenerationEntity) = generationDao.deleteGeneration(generation)

    suspend fun deleteById(id: Long) = generationDao.deleteGenerationById(id)
}
