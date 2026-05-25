package com.example.treinoapp

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class PosicaoFotoEvolucao(
    val id: String,
    val rotulo: String,
    val ordem: Int,
)

object FotoEvolucaoCatalogo {
    val posicoes: List<PosicaoFotoEvolucao> = listOf(
        PosicaoFotoEvolucao("frente", "Frente", 0),
        PosicaoFotoEvolucao("lado_direito", "Lado direito", 1),
        PosicaoFotoEvolucao("lado_esquerdo", "Lado esquerdo", 2),
        PosicaoFotoEvolucao("costas", "Costas", 3),
    )

    fun posicaoPorId(id: String): PosicaoFotoEvolucao? = posicoes.find { it.id == id }

    fun formatarData(millis: Long): String =
        SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR")).format(Date(millis))
}
