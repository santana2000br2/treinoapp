package com.example.treinoapp

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class TipoMedicao(
    val id: String,
    val rotulo: String,
    val unidade: String,
    val instrucao: String,
    val valorDe: (MedicaoMensalEntity) -> Double?,
)

object MedicaoCatalogo {
    val todos: List<TipoMedicao> = listOf(
        TipoMedicao(
            id = "peso",
            rotulo = "Peso",
            unidade = "kg",
            instrucao = "Medir em balança plana, de preferência em jejum, sem sapatos e com roupas leves.",
            valorDe = { it.pesoKg },
        ),
        TipoMedicao(
            id = "altura",
            rotulo = "Altura",
            unidade = "cm",
            instrucao = "Ficar descalço, encostado na parede, olhando para frente, com postura reta.",
            valorDe = { it.alturaCm },
        ),
        TipoMedicao(
            id = "cintura",
            rotulo = "Cintura",
            unidade = "cm",
            instrucao = "Medir na parte mais estreita do tronco, geralmente acima do umbigo. Não prender a respiração.",
            valorDe = { it.cinturaCm },
        ),
        TipoMedicao(
            id = "abdomen",
            rotulo = "Abdômen",
            unidade = "cm",
            instrucao = "Medir na altura do umbigo, mantendo a barriga relaxada.",
            valorDe = { it.abdomenCm },
        ),
        TipoMedicao(
            id = "peito",
            rotulo = "Peito/Tórax",
            unidade = "cm",
            instrucao = "Passar a fita ao redor do tórax, na linha dos mamilos, mantendo postura reta.",
            valorDe = { it.peitoCm },
        ),
        TipoMedicao(
            id = "quadril",
            rotulo = "Quadril",
            unidade = "cm",
            instrucao = "Medir na parte mais larga do quadril/glúteos.",
            valorDe = { it.quadrilCm },
        ),
        TipoMedicao(
            id = "braco_esquerdo",
            rotulo = "Braço esquerdo",
            unidade = "cm",
            instrucao = "Medir a parte mais larga do braço esquerdo, com o braço relaxado ao lado do corpo.",
            valorDe = { it.bracoEsquerdoCm },
        ),
        TipoMedicao(
            id = "braco_direito",
            rotulo = "Braço direito",
            unidade = "cm",
            instrucao = "Medir a parte mais larga do braço direito, com o braço relaxado ao lado do corpo.",
            valorDe = { it.bracoDireitoCm },
        ),
        TipoMedicao(
            id = "coxa_esquerda",
            rotulo = "Coxa esquerda",
            unidade = "cm",
            instrucao = "Medir a parte mais larga da coxa esquerda, geralmente alguns centímetros abaixo da virilha.",
            valorDe = { it.coxaEsquerdaCm },
        ),
        TipoMedicao(
            id = "coxa_direita",
            rotulo = "Coxa direita",
            unidade = "cm",
            instrucao = "Medir a parte mais larga da coxa direita, geralmente alguns centímetros abaixo da virilha.",
            valorDe = { it.coxaDireitaCm },
        ),
        TipoMedicao(
            id = "panturrilha_esquerda",
            rotulo = "Panturrilha esquerda",
            unidade = "cm",
            instrucao = "Medir a parte mais larga da panturrilha esquerda, em pé e relaxada.",
            valorDe = { it.panturrilhaEsquerdaCm },
        ),
        TipoMedicao(
            id = "panturrilha_direita",
            rotulo = "Panturrilha direita",
            unidade = "cm",
            instrucao = "Medir a parte mais larga da panturrilha direita, em pé e relaxada.",
            valorDe = { it.panturrilhaDireitaCm },
        ),
    )

    fun tipoPorId(id: String): TipoMedicao? = todos.find { it.id == id }

    fun millisParaMesReferencia(millis: Long): String {
        val cal = Calendar.getInstance().apply { timeInMillis = millis }
        return String.format(
            Locale.US,
            "%04d-%02d",
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH) + 1,
        )
    }

    fun formatarMesReferencia(mesReferencia: String): String {
        return try {
            val parsed = SimpleDateFormat("yyyy-MM", Locale.US).parse(mesReferencia)
            if (parsed != null) {
                SimpleDateFormat("MMMM yyyy", Locale("pt", "BR")).format(parsed)
                    .replaceFirstChar { it.titlecase(Locale("pt", "BR")) }
            } else {
                mesReferencia
            }
        } catch (_: Exception) {
            mesReferencia
        }
    }

    fun formatarData(millis: Long): String =
        SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR")).format(Date(millis))

    fun formatarValor(tipo: TipoMedicao, valor: Double): String {
        val texto = if (valor == kotlin.math.floor(valor) && tipo.unidade != "kg") {
            valor.toInt().toString()
        } else {
            String.format(Locale("pt", "BR"), "%.1f", valor)
        }
        return "$texto ${tipo.unidade}"
    }
}
