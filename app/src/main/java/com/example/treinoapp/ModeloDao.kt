package com.example.treinoapp

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ModeloDao {
    @Query("SELECT * FROM modelos ORDER BY nome")
    fun getAll(): Flow<List<ModeloEntity>>

    @Query("SELECT * FROM modelos WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): ModeloEntity?

    @Query("SELECT COUNT(*) FROM modelos")
    suspend fun getCount(): Int

    @Query("SELECT nome FROM modelos")
    suspend fun getAllNomes(): List<String>

    @Query("SELECT * FROM modelos")
    suspend fun getAllSync(): List<ModeloEntity>

    @Insert
    suspend fun insert(modelo: ModeloEntity)

    @Update
    suspend fun update(modelo: ModeloEntity)

    @Delete
    suspend fun delete(modelo: ModeloEntity)
}