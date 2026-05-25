package com.example.treinoapp

/**
 * Planos pré-definidos pelo desenvolvedor (utilizador só consulta e associa à agenda).
 */
data class TreinoProntoItemSeed(
    val diaSemana: String,
    val nomeCatalogo: String,
    val seriesReps: String,
)

data class TreinoProntoPlanoSeed(
    val nome: String,
    val descricao: String,
    val itens: List<TreinoProntoItemSeed>,
)

object TreinoProntosSeed {

    val ganhoMassaSegundaASexta = TreinoProntoPlanoSeed(
        nome = "Ganho de Massa Muscular (Segunda a Sexta)",
        descricao = """
            Esse plano é voltado para uma pessoa magra que quer ganhar massa muscular (“bulking limpo”), focando em:

            • Exercícios compostos
            • Progressão de carga
            • Volume adequado
            • Descanso muscular

            Regras importantes
            • Descanso entre séries: 60–90 segundos
            • Carga: peso difícil nas últimas 2 repetições
            • Alimentação é essencial para crescer
            • Dormir 7–9 horas
            • Aumentar os pesos gradualmente
        """.trimIndent(),
        itens = listOf(
            // Segunda — Peito + Tríceps
            item("Segunda-feira", "Supino Reto com Barra", "4x8-10"),
            item("Segunda-feira", "Supino Inclinado com Halteres", "4x10"),
            item("Segunda-feira", "Crucifixo com Halteres", "3x12"),
            item("Segunda-feira", "Crossover (Polia Alta)", "3x12"),
            item("Segunda-feira", "Tríceps Pulley", "3x12"),
            item("Segunda-feira", "Tríceps Francês", "3x10"),
            item("Segunda-feira", "Mergulho em Banco", "3x12"),
            // Terça — Costas + Bíceps
            item("Terça-feira", "Puxada Frente na Polia", "4x10"),
            item("Terça-feira", "Remada Curvada", "4x8-10"),
            item("Terça-feira", "Remada Baixa", "3x10"),
            item("Terça-feira", "Pulldown com Corda", "3x12"),
            item("Terça-feira", "Rosca Direta", "4x10"),
            item("Terça-feira", "Rosca Alternada", "3x12"),
            item("Terça-feira", "Rosca Martelo", "3x12"),
            // Quarta — Pernas
            item("Quarta-feira", "Agachamento Livre", "4x8-10"),
            item("Quarta-feira", "Leg Press 45°", "4x12"),
            item("Quarta-feira", "Cadeira Extensora", "3x12"),
            item("Quarta-feira", "Mesa Flexora", "3x12"),
            item("Quarta-feira", "Stiff com Barra", "4x10"),
            item("Quarta-feira", "Panturrilha no Leg Press", "4x15"),
            item("Quarta-feira", "Panturrilha em Pé", "4x15"),
            // Quinta — Ombro + Abdômen
            item("Quinta-feira", "Desenvolvimento com Halteres", "4x10"),
            item("Quinta-feira", "Elevação Lateral", "4x12"),
            item("Quinta-feira", "Elevação Frontal", "3x12"),
            item("Quinta-feira", "Remada Alta com Barra", "3x10"),
            item("Quinta-feira", "Encolhimento para Trapézio", "4x12"),
            item("Quinta-feira", "Abdominal na Polia", "3x15"),
            item("Quinta-feira", "Elevação de Pernas", "3x15"),
            item("Quinta-feira", "Prancha", "3x40 segundos"),
            // Sexta — Full upper
            item("Sexta-feira", "Supino Inclinado com Halteres", "3x10"),
            item("Sexta-feira", "Barra Fixa", "3x máximo"),
            item("Sexta-feira", "Remada Unilateral", "3x10"),
            item("Sexta-feira", "Desenvolvimento com Barra", "3x10"),
            item("Sexta-feira", "Rosca Scott", "3x10"),
            item("Sexta-feira", "Tríceps Pulley", "3x12"),
        ),
    )

    val perdaPesoSegundaASexta = TreinoProntoPlanoSeed(
        nome = "Perda de Peso (Segunda a Sexta)",
        descricao = """
            Objetivo:
            • Queimar gordura
            • Melhorar condicionamento
            • Preservar massa muscular
            • Acelerar metabolismo

            Este plano mistura musculação, cardio, treino funcional e alta intensidade moderada.

            Regras importantes
            • Descanso: 30–60 segundos
            • Prioridade: execução correta
            • Fazer cardio após o treino
            • Manter déficit calórico na alimentação
            • Beber bastante água
        """.trimIndent(),
        itens = listOf(
            // Segunda — Peito + Tríceps + Cardio
            item("Segunda-feira", "Supino Reto com Barra", "4x12"),
            item("Segunda-feira", "Supino Inclinado com Halteres", "3x12"),
            item("Segunda-feira", "Crossover (Polia Alta)", "3x15"),
            item("Segunda-feira", "Tríceps Pulley", "3x15"),
            item("Segunda-feira", "Tríceps Corda na Polia", "3x15"),
            item("Segunda-feira", "Caminhada na Esteira", "20 min — caminhada rápida/inclinação"),
            // Terça — Pernas + Glúteos + Cardio
            item("Terça-feira", "Agachamento Livre", "4x12"),
            item("Terça-feira", "Leg Press 45°", "4x15"),
            item("Terça-feira", "Avanço com Barra", "3x12"),
            item("Terça-feira", "Cadeira Extensora", "3x15"),
            item("Terça-feira", "Mesa Flexora", "3x15"),
            item("Terça-feira", "Panturrilha no Leg Press", "4x20"),
            item("Terça-feira", "Escada (Stair Climber)", "15–20 minutos"),
            // Quarta — Costas + Bíceps + HIIT
            item("Quarta-feira", "Puxada Frente na Polia", "4x12"),
            item("Quarta-feira", "Remada Baixa", "3x12"),
            item("Quarta-feira", "Remada Curvada", "3x10"),
            item("Quarta-feira", "Rosca Direta", "3x12"),
            item("Quarta-feira", "Rosca Martelo", "3x12"),
            item("Quarta-feira", "HIIT na Esteira", "20 min — 30s corrida forte / 1 min caminhada"),
            // Quinta — Ombro + Abdômen + Funcional
            item("Quinta-feira", "Desenvolvimento com Halteres", "4x12"),
            item("Quinta-feira", "Elevação Lateral", "3x15"),
            item("Quinta-feira", "Remada Alta com Barra", "3x12"),
            item("Quinta-feira", "Abdominal na Polia", "3x15"),
            item("Quinta-feira", "Prancha", "3x45 segundos"),
            item("Quinta-feira", "Russian Twist", "3x20"),
            item("Quinta-feira", "Battle Rope", "3 voltas · 30 segundos"),
            item("Quinta-feira", "Mountain Climber", "3 voltas · 30 segundos"),
            item("Quinta-feira", "Kettlebell Swing", "3 voltas · 15 repetições"),
            // Sexta — Metabólico full body (circuito)
            item("Sexta-feira", "Agachamento Livre", "Circuito 3–4x · 15 reps"),
            item("Sexta-feira", "Flexão de Braço", "Circuito 3–4x · 12 reps"),
            item("Sexta-feira", "Remo Ergométrico", "Circuito 3–4x · 1 minuto"),
            item("Sexta-feira", "Step-up com Halteres", "Circuito 3–4x · 12 reps"),
            item("Sexta-feira", "Burpee", "Circuito 3–4x · 10 reps"),
            item("Sexta-feira", "Prancha Lateral", "Circuito 3–4x · 30s cada lado"),
        ),
    )

    val manutencaoShapeSegundaASexta = TreinoProntoPlanoSeed(
        nome = "Manutenção de Shape (Segunda a Sexta)",
        descricao = """
            Objetivo:
            • Manter massa muscular
            • Preservar definição
            • Continuar forte e condicionado
            • Evitar perda de rendimento

            Volume moderado, intensidade boa, cardio controlado — menos desgaste que um treino de ganho de massa.

            Regras importantes
            • Descanso: 45–90 segundos
            • Treinar próximo da falha em alguns exercícios
            • Cardio moderado
            • Manter alimentação equilibrada
            • Priorizar consistência
        """.trimIndent(),
        itens = listOf(
            // Segunda — Peito + Tríceps + Cardio leve
            item("Segunda-feira", "Supino Reto com Barra", "4x8-10"),
            item("Segunda-feira", "Supino Inclinado com Halteres", "3x10"),
            item("Segunda-feira", "Crossover (Polia Alta)", "3x12"),
            item("Segunda-feira", "Tríceps Corda na Polia", "3x12"),
            item("Segunda-feira", "Tríceps Francês", "3x10"),
            item("Segunda-feira", "Caminhada na Esteira", "15 min — ritmo leve"),
            // Terça — Costas + Bíceps
            item("Terça-feira", "Puxada Frente na Polia", "4x10"),
            item("Terça-feira", "Remada Curvada", "4x10"),
            item("Terça-feira", "Remada Baixa", "3x12"),
            item("Terça-feira", "Rosca Direta", "3x10"),
            item("Terça-feira", "Rosca Martelo", "3x12"),
            // Quarta — Pernas + Cardio
            item("Quarta-feira", "Agachamento Livre", "4x8-10"),
            item("Quarta-feira", "Leg Press 45°", "4x12"),
            item("Quarta-feira", "Stiff com Barra", "3x10"),
            item("Quarta-feira", "Mesa Flexora", "3x12"),
            item("Quarta-feira", "Cadeira Extensora", "3x12"),
            item("Quarta-feira", "Panturrilha no Leg Press", "4x15"),
            item("Quarta-feira", "Escada (Stair Climber)", "10–15 minutos"),
            // Quinta — Ombro + Abdômen
            item("Quinta-feira", "Desenvolvimento com Barra", "4x10"),
            item("Quinta-feira", "Elevação Lateral", "4x12"),
            item("Quinta-feira", "Remada Alta com Barra", "3x10"),
            item("Quinta-feira", "Encolhimento para Trapézio", "3x12"),
            item("Quinta-feira", "Abdominal na Polia", "3x15"),
            item("Quinta-feira", "Prancha", "3x1 minuto"),
            item("Quinta-feira", "Elevação de Pernas", "3x15"),
            // Sexta — Full body leve + condicionamento
            item("Sexta-feira", "Agachamento com Halteres", "Circuito 3–4x · 15 reps"),
            item("Sexta-feira", "Flexão de Braço", "Circuito 3–4x · 15 reps"),
            item("Sexta-feira", "Remo Ergométrico", "Circuito 3–4x · 1 minuto"),
            item("Sexta-feira", "Kettlebell Swing", "Circuito 3–4x · 15 reps"),
            item("Sexta-feira", "Battle Rope", "Circuito 3–4x · 30 segundos"),
            item("Sexta-feira", "Mountain Climber", "Circuito 3–4x · 30 segundos"),
        ),
    )

    val planos: List<TreinoProntoPlanoSeed> = listOf(
        ganhoMassaSegundaASexta,
        perdaPesoSegundaASexta,
        manutencaoShapeSegundaASexta,
    )

    private fun item(diaSemana: String, nomeCatalogo: String, seriesReps: String) =
        TreinoProntoItemSeed(diaSemana, nomeCatalogo, seriesReps)

    fun descricaoParaAgenda(seriesReps: String, descricaoCatalogo: String): String =
        "Séries/repetições: $seriesReps\n$descricaoCatalogo"
}
