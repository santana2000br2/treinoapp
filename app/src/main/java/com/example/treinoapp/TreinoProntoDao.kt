package com.example.treinoapp

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TreinoProntoDao {
    @Query("SELECT * FROM treino_pronto ORDER BY nome COLLATE NOCASE ASC")
    fun observarPlanos(): Flow<List<TreinoProntoEntity>>

    @Query("SELECT * FROM treino_pronto WHERE id = :id LIMIT 1")
    suspend fun planoPorId(id: Long): TreinoProntoEntity?

    @Query("SELECT * FROM treino_pronto WHERE nome = :nome LIMIT 1")
    suspend fun planoPorNome(nome: String): TreinoProntoEntity?

    @Insert
    suspend fun insertPlano(plano: TreinoProntoEntity): Long

    @Update
    suspend fun updatePlano(plano: TreinoProntoEntity)

    @Query("DELETE FROM treino_pronto WHERE id = :id")
    suspend fun deletePlanoPorId(id: Long)

    @Query("SELECT * FROM treino_pronto_item WHERE treinoProntoId = :planoId ORDER BY ordem ASC, id ASC")
    suspend fun itensPorPlano(planoId: Long): List<TreinoProntoItemEntity>

    @Query("SELECT COUNT(*) FROM treino_pronto_item WHERE treinoProntoId = :planoId")
    suspend fun contarItens(planoId: Long): Int

    @Query(
        """
        SELECT COUNT(*) FROM treino_pronto_item
        WHERE treinoProntoId = :planoId AND modeloId = :modeloId
        """,
    )
    suspend fun existeModeloNoPlano(planoId: Long, modeloId: Long): Int

    @Query("SELECT COALESCE(MAX(ordem), -1) FROM treino_pronto_item WHERE treinoProntoId = :planoId")
    suspend fun maxOrdem(planoId: Long): Int

    @Insert
    suspend fun insertItem(item: TreinoProntoItemEntity): Long

    @Delete
    suspend fun deleteItem(item: TreinoProntoItemEntity)

    @Query("DELETE FROM treino_pronto_item WHERE id = :itemId")
    suspend fun deleteItemPorId(itemId: Long)
}
