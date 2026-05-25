package com.example.treinoapp

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface DietaAlimentoDao {
    @Query("SELECT * FROM dieta_alimentos WHERE tipoRefeicao = :tipo ORDER BY nome")
    fun observarPorTipo(tipo: String): Flow<List<DietaAlimentoEntity>>

    @Insert
    suspend fun insert(entity: DietaAlimentoEntity)

    @Delete
    suspend fun delete(entity: DietaAlimentoEntity)
}
