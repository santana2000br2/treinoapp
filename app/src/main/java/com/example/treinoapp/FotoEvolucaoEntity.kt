package com.example.treinoapp

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity(tableName = "foto_evolucao_sessao")
data class FotoEvolucaoSessaoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val dataMillis: Long,
)

@Entity(
    tableName = "foto_evolucao_foto",
    foreignKeys = [
        ForeignKey(
            entity = FotoEvolucaoSessaoEntity::class,
            parentColumns = ["id"],
            childColumns = ["sessaoId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [
        Index("sessaoId"),
        Index(value = ["sessaoId", "posicao"], unique = true),
    ],
)
data class FotoEvolucaoFotoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val sessaoId: Long,
    val posicao: String,
    val uriLocal: String,
)

data class FotoEvolucaoSessaoComFotosRel(
    @Embedded val sessao: FotoEvolucaoSessaoEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "sessaoId",
    )
    val fotos: List<FotoEvolucaoFotoEntity>,
)

data class FotoEvolucaoSessaoComFotos(
    val sessao: FotoEvolucaoSessaoEntity,
    val fotos: List<FotoEvolucaoFotoEntity>,
) {
    fun fotoPorPosicao(posicaoId: String): FotoEvolucaoFotoEntity? =
        fotos.find { it.posicao == posicaoId }
}

fun FotoEvolucaoSessaoComFotosRel.paraUi() =
    FotoEvolucaoSessaoComFotos(sessao = sessao, fotos = fotos)
