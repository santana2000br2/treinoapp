package com.example.treinoapp

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/** Plano de treino pronto (ex.: «Peito e tríceps», «Full body»). */
@Entity(tableName = "treino_pronto")
data class TreinoProntoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val nome: String,
    val descricao: String = "",
)

/** Exercício do catálogo (`modelos`) incluído num plano pronto. */
@Entity(
    tableName = "treino_pronto_item",
    foreignKeys = [
        ForeignKey(
            entity = TreinoProntoEntity::class,
            parentColumns = ["id"],
            childColumns = ["treinoProntoId"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = ModeloEntity::class,
            parentColumns = ["id"],
            childColumns = ["modeloId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [
        Index("treinoProntoId"),
        Index(value = ["treinoProntoId", "modeloId", "diaSemana"], unique = true),
    ],
)
data class TreinoProntoItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val treinoProntoId: Long,
    val modeloId: Long,
    /** Dia da agenda (ex.: Segunda-feira). */
    val diaSemana: String,
    val ordem: Int,
    /** Séries/reps do plano; ao associar, sobrepõe a descrição do catálogo. */
    val descricaoOverride: String? = null,
)

data class TreinoProntoItemComModelo(
    val item: TreinoProntoItemEntity,
    val modelo: ModeloEntity,
)

data class TreinoProntoComItens(
    val plano: TreinoProntoEntity,
    val itens: List<TreinoProntoItemComModelo>,
) {
    val quantidadeExercicios: Int get() = itens.size
}

data class TreinoProntoResumo(
    val plano: TreinoProntoEntity,
    val quantidadeExercicios: Int,
)
