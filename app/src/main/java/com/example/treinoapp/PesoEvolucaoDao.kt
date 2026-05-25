package com.example.treinoapp

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PesoEvolucaoDao {
    @Query("SELECT * FROM peso_evolucao ORDER BY dataMillis ASC")
    fun observarTodos(): Flow<List<PesoEvolucaoEntity>>

    @Insert
    suspend fun insert(entity: PesoEvolucaoEntity)
}
