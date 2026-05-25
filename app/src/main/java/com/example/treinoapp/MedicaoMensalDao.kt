package com.example.treinoapp

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface MedicaoMensalDao {
    @Query("SELECT * FROM medicao_mensal ORDER BY mesReferencia DESC")
    fun observarTodos(): Flow<List<MedicaoMensalEntity>>

    @Query("SELECT * FROM medicao_mensal WHERE id = :id LIMIT 1")
    suspend fun porId(id: Long): MedicaoMensalEntity?

    @Query("SELECT * FROM medicao_mensal WHERE mesReferencia = :mesReferencia LIMIT 1")
    suspend fun porMes(mesReferencia: String): MedicaoMensalEntity?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(entity: MedicaoMensalEntity): Long

    @Update
    suspend fun update(entity: MedicaoMensalEntity)

    @Delete
    suspend fun delete(entity: MedicaoMensalEntity)
}
