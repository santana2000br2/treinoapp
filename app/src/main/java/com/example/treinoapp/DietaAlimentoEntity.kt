package com.example.treinoapp

import androidx.room.Entity
import androidx.room.PrimaryKey

/** Valores persistidos na coluna [DietaAlimentoEntity.unidadeMedida]. */
object DietaUnidadeMedida {
    const val GRAMAS = "GRAMAS"
    const val UNIDADE = "UNIDADE"
}

@Entity(tableName = "dieta_alimentos")
data class DietaAlimentoEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val tipoRefeicao: String,
    val nome: String,
    val quantidade: Int,
    val unidadeMedida: String,
)

/** Texto para a lista (ex.: "50 gramas", "1 unidade"). */
fun DietaAlimentoEntity.formatoQuantidadeExibicao(): String =
    when (unidadeMedida) {
        DietaUnidadeMedida.GRAMAS ->
            if (quantidade == 1) "$quantidade grama" else "$quantidade gramas"
        DietaUnidadeMedida.UNIDADE ->
            if (quantidade == 1) "$quantidade unidade" else "$quantidade unidades"
        else -> quantidade.toString()
    }
