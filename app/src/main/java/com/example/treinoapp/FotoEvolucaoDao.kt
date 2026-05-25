package com.example.treinoapp

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface FotoEvolucaoDao {
    @Transaction
    @Query("SELECT * FROM foto_evolucao_sessao ORDER BY dataMillis DESC")
    fun observarSessoesComFotosRel(): Flow<List<FotoEvolucaoSessaoComFotosRel>>

    @Query("SELECT * FROM foto_evolucao_sessao ORDER BY dataMillis DESC")
    fun observarSessoes(): Flow<List<FotoEvolucaoSessaoEntity>>

    @Query("SELECT * FROM foto_evolucao_sessao WHERE id = :id LIMIT 1")
    suspend fun sessaoPorId(id: Long): FotoEvolucaoSessaoEntity?

    @Insert
    suspend fun insertSessao(sessao: FotoEvolucaoSessaoEntity): Long

    @Update
    suspend fun updateSessao(sessao: FotoEvolucaoSessaoEntity)

    @Delete
    suspend fun deleteSessao(sessao: FotoEvolucaoSessaoEntity)

    @Query("SELECT * FROM foto_evolucao_foto WHERE sessaoId = :sessaoId ORDER BY posicao ASC")
    suspend fun fotosPorSessao(sessaoId: Long): List<FotoEvolucaoFotoEntity>

    @Query("SELECT * FROM foto_evolucao_foto WHERE sessaoId = :sessaoId AND posicao = :posicao LIMIT 1")
    suspend fun fotoPorSessaoEPosicao(sessaoId: Long, posicao: String): FotoEvolucaoFotoEntity?

    @Insert
    suspend fun insertFoto(foto: FotoEvolucaoFotoEntity): Long

    @Update
    suspend fun updateFoto(foto: FotoEvolucaoFotoEntity)

    @Delete
    suspend fun deleteFoto(foto: FotoEvolucaoFotoEntity)

    @Query("DELETE FROM foto_evolucao_foto WHERE sessaoId = :sessaoId")
    suspend fun deleteFotosDaSessao(sessaoId: Long)
}
