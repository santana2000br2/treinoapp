package com.example.treinoapp

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TreinoDiaDao {
    @Query("SELECT * FROM treino_dia WHERE diaSemana = :dia ORDER BY nome")
    fun getTreinosPorDia(dia: String): Flow<List<TreinoDiaEntity>>

    @Query("SELECT * FROM treino_dia")
    fun observarTodos(): Flow<List<TreinoDiaEntity>>

    @Query("SELECT id FROM treino_dia WHERE diaSemana = :dia ORDER BY nome")
    suspend fun idsPorDia(dia: String): List<Long>

    @Query("SELECT * FROM treino_dia WHERE imagem1 IS NULL OR imagem1 = ''")
    suspend fun getSemImagem(): List<TreinoDiaEntity>

    @Query("SELECT * FROM treino_dia")
    suspend fun getAllSync(): List<TreinoDiaEntity>

    @Insert
    suspend fun insert(treino: TreinoDiaEntity)

    @Update
    suspend fun update(treino: TreinoDiaEntity)

    @Delete
    suspend fun delete(treino: TreinoDiaEntity)

    @Query("DELETE FROM treino_dia WHERE diaSemana = :dia")
    suspend fun deleteAllFromDay(dia: String)
}