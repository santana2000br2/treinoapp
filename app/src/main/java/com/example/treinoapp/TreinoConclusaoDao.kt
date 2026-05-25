package com.example.treinoapp

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TreinoConclusaoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: TreinoConclusaoEntity)

    @Query("DELETE FROM treino_conclusao WHERE dataYmd = :ymd AND treinoDiaId = :treinoDiaId")
    suspend fun delete(ymd: String, treinoDiaId: Long)

    @Query("SELECT treinoDiaId FROM treino_conclusao WHERE dataYmd = :ymd")
    fun observarIdsNoDia(ymd: String): Flow<List<Long>>

    @Query("SELECT * FROM treino_conclusao")
    fun observarTodas(): Flow<List<TreinoConclusaoEntity>>

    @Query(
        """
        SELECT COUNT(*) FROM treino_conclusao
        WHERE dataYmd = :ymd AND treinoDiaId IN (:ids)
        """,
    )
    suspend fun contarConcluidosNoDiaParaIds(ymd: String, ids: List<Long>): Int
}
