package com.example.treinoapp

import androidx.room.Entity

/**
 * Marca que o utilizador concluiu um treino da agenda num dia civil (yyyy-MM-dd).
 * Cada linha em [treino_dia] para esse dia da semana conta como um "exercício" para a meta dos 50%.
 */
@Entity(
    tableName = "treino_conclusao",
    primaryKeys = ["dataYmd", "treinoDiaId"],
)
data class TreinoConclusaoEntity(
    val dataYmd: String,
    val treinoDiaId: Long,
)
