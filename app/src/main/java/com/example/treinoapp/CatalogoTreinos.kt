package com.example.treinoapp

import android.content.Context

data class TreinoCatalogoSeed(
    val nome: String,
    val descricao: String,
    val video: String? = null,
    /** Nome do PNG em drawable sem extensão; se null, deriva do nome do exercício. */
    val arquivoImagem: String? = null,
) {
    fun toModeloEntity(context: Context, vincularImagem: Boolean = false): ModeloEntity {
        val imagem1 = if (vincularImagem) {
            ExercicioImagemUtil.vincularImagemSeExistir(context, nome, arquivoImagem)
        } else {
            null
        }
        return ModeloEntity(
            nome = nome,
            descricao = descricao,
            imagem1 = imagem1,
            imagem2 = null,
            video = video,
        )
    }
}

object CatalogoTreinos {

    private fun desc(
        equipamento: String,
        grupo: String,
        series: String,
        repeticoes: String,
    ) = "Equipamento: $equipamento Grupo: $grupo Séries: $series Repetições: $repeticoes"

    val todos: List<TreinoCatalogoSeed> = listOf(
        // —— Catálogo original ——
        TreinoCatalogoSeed("Desenvolvimento com Barra", desc("Barra", "Ombros", "4", "10 a 12")),
        TreinoCatalogoSeed(
            "Agachamento Livre",
            desc("Barra", "Pernas", "4", "10 a 12"),
            video = "https://www.youtube.com/shorts/MGCcMsvBebQ",
        ),
        TreinoCatalogoSeed("Supino Reto com Barra", desc("Barra", "Peito", "4", "8 a 12")),
        TreinoCatalogoSeed(
            "Puxada Frente na Polia",
            desc("Máquina (Polia)", "Costas", "4", "10 a 12"),
            arquivoImagem = "puxada_frente_polia",
        ),
        TreinoCatalogoSeed("Rosca Direta", desc("Barra", "Bíceps", "3", "10 a 12")),
        TreinoCatalogoSeed("Tríceps Pulley", desc("Polia", "Tríceps", "3", "10 a 12")),
        TreinoCatalogoSeed("Leg Press 45°", desc("Máquina", "Pernas", "4", "10 a 15")),
        TreinoCatalogoSeed("Elevação Lateral", desc("Halteres", "Ombros", "3", "12 a 15")),
        TreinoCatalogoSeed("Remada Curvada", desc("Barra", "Costas", "4", "8 a 12")),
        TreinoCatalogoSeed("Abdominal Tradicional", desc("Peso Corporal", "Abdômen", "3", "15 a 20")),
        TreinoCatalogoSeed("Supino Inclinado com Halteres", desc("Halteres", "Peito", "4", "8 a 12")),
        TreinoCatalogoSeed("Crucifixo com Halteres", desc("Halteres", "Peito", "3", "12 a 15")),
        TreinoCatalogoSeed("Barra Fixa", desc("Barra Fixa", "Costas", "4", "6 a 10")),
        TreinoCatalogoSeed("Remada Baixa", desc("Máquina", "Costas", "4", "10 a 12")),
        TreinoCatalogoSeed("Stiff com Barra", desc("Barra", "Posterior de Coxa", "4", "10 a 12")),
        TreinoCatalogoSeed("Levantamento Terra", desc("Barra", "Pernas / Costas", "4", "6 a 10")),
        TreinoCatalogoSeed("Afundo com Halteres", desc("Halteres", "Pernas", "3", "10 a 12")),
        TreinoCatalogoSeed("Panturrilha em Pé", desc("Máquina", "Panturrilha", "4", "15 a 20")),
        TreinoCatalogoSeed("Desenvolvimento com Halteres", desc("Halteres", "Ombros", "4", "10 a 12")),
        TreinoCatalogoSeed("Encolhimento para Trapézio", desc("Halteres", "Trapézio", "3", "12 a 15")),
        TreinoCatalogoSeed("Rosca Alternada", desc("Halteres", "Bíceps", "3", "10 a 12")),
        TreinoCatalogoSeed("Rosca Martelo", desc("Halteres", "Bíceps", "3", "10 a 12")),
        TreinoCatalogoSeed("Rosca Scott", desc("Máquina", "Bíceps", "3", "10 a 12")),
        TreinoCatalogoSeed("Tríceps Testa", desc("Barra", "Tríceps", "3", "8 a 12")),
        TreinoCatalogoSeed("Tríceps Francês", desc("Halter", "Tríceps", "3", "10 a 12")),
        TreinoCatalogoSeed("Flexão de Braço", desc("Peso Corporal", "Peito", "3", "12 a 20")),
        TreinoCatalogoSeed("Cadeira Extensora", desc("Máquina", "Quadríceps", "4", "12 a 15")),
        TreinoCatalogoSeed("Mesa Flexora", desc("Máquina", "Posterior de Coxa", "4", "12 a 15")),
        TreinoCatalogoSeed("Abdominal Infra", desc("Peso Corporal", "Abdômen", "3", "15 a 20")),
        TreinoCatalogoSeed("Prancha", desc("Peso Corporal", "Core", "3", "30 a 60 segundos")),
        TreinoCatalogoSeed("Bicicleta Ergométrica", desc("Bicicleta", "Pernas", "1", "20 minutos")),
        TreinoCatalogoSeed("Corrida na Esteira", desc("Esteira", "Pernas", "1", "20 minutos")),
        TreinoCatalogoSeed("Caminhada na Esteira", desc("Esteira", "Pernas", "1", "30 minutos")),
        TreinoCatalogoSeed("Elevação Frontal", desc("Halteres", "Ombros", "3", "12 a 15")),
        TreinoCatalogoSeed("Crucifixo Invertido", desc("Halteres", "Ombros", "3", "12 a 15")),
        TreinoCatalogoSeed("Remada Unilateral", desc("Halter", "Costas", "3", "10 a 12")),
        TreinoCatalogoSeed("Pulldown com Corda", desc("Polia", "Costas", "3", "10 a 12")),
        TreinoCatalogoSeed("Agachamento no Smith", desc("Máquina Smith", "Pernas", "4", "10 a 12")),
        TreinoCatalogoSeed("Passada Caminhando", desc("Halteres", "Pernas", "3", "12 passos")),
        TreinoCatalogoSeed("Mergulho em Banco", desc("Banco", "Tríceps", "3", "10 a 15")),
        TreinoCatalogoSeed("Abdominal na Máquina", desc("Máquina", "Abdômen", "3", "12 a 15")),
        TreinoCatalogoSeed("Elíptico", desc("Elíptico", "Corpo inteiro", "1", "20 minutos")),
        TreinoCatalogoSeed("Pular Corda", desc("Corda", "Corpo inteiro", "3", "1 minuto")),
        TreinoCatalogoSeed("Burpee", desc("Peso Corporal", "Corpo inteiro", "3", "10 a 15")),
        TreinoCatalogoSeed("Alongamento Posterior de Coxa", desc("Peso Corporal", "Posterior de Coxa", "2", "30 segundos")),
        TreinoCatalogoSeed("Alongamento de Quadríceps", desc("Peso Corporal", "Quadríceps", "2", "30 segundos")),
        TreinoCatalogoSeed("Alongamento de Peitoral", desc("Peso Corporal", "Peito", "2", "30 segundos")),
        TreinoCatalogoSeed("Alongamento de Ombro", desc("Peso Corporal", "Ombros", "2", "30 segundos")),
        TreinoCatalogoSeed("Levantamento Terra Romeno", desc("Barra", "Posterior de Coxa", "4", "8 a 12")),
        TreinoCatalogoSeed("Supino Declinado", desc("Barra", "Peito", "4", "8 a 12")),
        // —— Novos exercícios ——
        TreinoCatalogoSeed("Supino com Halteres", desc("Halteres", "Peito", "4", "8 a 12")),
        TreinoCatalogoSeed("Supino na Máquina", desc("Máquina", "Peito", "4", "10 a 12")),
        TreinoCatalogoSeed("Crucifixo na Polia", desc("Polia", "Peito", "3", "12 a 15")),
        TreinoCatalogoSeed("Crossover (Polia Alta)", desc("Polia", "Peito", "3", "12 a 15")),
        TreinoCatalogoSeed("Flexão Inclinada", desc("Peso Corporal", "Peito", "3", "12 a 20")),
        TreinoCatalogoSeed("Flexão Declinada", desc("Peso Corporal", "Peito", "3", "12 a 20")),
        TreinoCatalogoSeed("Pullover com Halter", desc("Halter", "Peito", "3", "10 a 12")),
        TreinoCatalogoSeed("Puxada Supinada", desc("Polia", "Costas", "4", "10 a 12")),
        TreinoCatalogoSeed("Remada Cavalinho", desc("Máquina", "Costas", "4", "10 a 12")),
        TreinoCatalogoSeed("Remada Serrote", desc("Halter", "Costas", "3", "10 a 12")),
        TreinoCatalogoSeed("Remada na Polia Baixa", desc("Polia", "Costas", "4", "10 a 12")),
        TreinoCatalogoSeed("Pulldown Unilateral", desc("Polia", "Costas", "3", "10 a 12")),
        TreinoCatalogoSeed("Barra Fixa Supinada", desc("Barra Fixa", "Costas", "4", "6 a 10")),
        TreinoCatalogoSeed(
            "Levantamento Terra Sumô",
            desc("Barra", "Pernas / Costas", "4", "6 a 10"),
            arquivoImagem = "terra_sumo",
        ),
        TreinoCatalogoSeed("Desenvolvimento Arnold", desc("Halteres", "Ombros", "4", "10 a 12")),
        TreinoCatalogoSeed("Desenvolvimento na Máquina", desc("Máquina", "Ombros", "4", "10 a 12")),
        TreinoCatalogoSeed("Elevação Lateral na Polia", desc("Polia", "Ombros", "3", "12 a 15")),
        TreinoCatalogoSeed("Elevação Posterior", desc("Halteres", "Ombros", "3", "12 a 15")),
        TreinoCatalogoSeed("Remada Alta com Barra", desc("Barra", "Ombros", "3", "10 a 12")),
        TreinoCatalogoSeed("Face Pull", desc("Polia", "Ombros", "3", "15 a 20")),
        TreinoCatalogoSeed("Rosca Concentrada", desc("Halter", "Bíceps", "3", "10 a 12")),
        TreinoCatalogoSeed("Rosca 21 com Barra", desc("Barra", "Bíceps", "3", "21 repetições")),
        TreinoCatalogoSeed("Rosca na Polia", desc("Polia", "Bíceps", "3", "10 a 12")),
        TreinoCatalogoSeed("Rosca Inversa", desc("Barra", "Bíceps", "3", "10 a 12")),
        TreinoCatalogoSeed("Tríceps Coice", desc("Halter", "Tríceps", "3", "12 a 15")),
        TreinoCatalogoSeed(
            "Tríceps Banco (Paralelas)",
            desc("Barras Paralelas", "Tríceps", "3", "8 a 12"),
            arquivoImagem = "triceps_paralelas",
        ),
        TreinoCatalogoSeed("Extensão de Tríceps Unilateral", desc("Halter", "Tríceps", "3", "10 a 12")),
        TreinoCatalogoSeed("Agachamento Hack", desc("Máquina", "Pernas", "4", "10 a 12")),
        TreinoCatalogoSeed("Agachamento Búlgaro", desc("Halteres", "Pernas", "3", "10 a 12")),
        TreinoCatalogoSeed("Agachamento Frontal", desc("Barra", "Pernas", "4", "8 a 12")),
        TreinoCatalogoSeed("Agachamento Goblet", desc("Halter / Kettlebell", "Pernas", "4", "10 a 12")),
        TreinoCatalogoSeed("Leg Press Horizontal", desc("Máquina", "Pernas", "4", "10 a 15")),
        TreinoCatalogoSeed("Cadeira Abdutora", desc("Máquina", "Glúteos", "3", "15 a 20")),
        TreinoCatalogoSeed("Cadeira Adutora", desc("Máquina", "Pernas", "3", "15 a 20")),
        TreinoCatalogoSeed(
            "Extensão de Quadril (Coice)",
            desc("Polia / Máquina", "Glúteos", "3", "12 a 15"),
            arquivoImagem = "coice_gluteo",
        ),
        TreinoCatalogoSeed("Elevação Pélvica", desc("Barra / Banco", "Glúteos", "4", "10 a 12")),
        TreinoCatalogoSeed("Stiff com Halteres", desc("Halteres", "Posterior de Coxa", "4", "10 a 12")),
        TreinoCatalogoSeed("Panturrilha Sentado", desc("Máquina", "Panturrilha", "4", "15 a 20")),
        TreinoCatalogoSeed("Panturrilha no Leg Press", desc("Máquina", "Panturrilha", "4", "15 a 20")),
        TreinoCatalogoSeed("Avanço com Barra", desc("Barra", "Pernas", "3", "10 a 12")),
        TreinoCatalogoSeed("Step-up com Halteres", desc("Halteres", "Pernas", "3", "10 a 12")),
        TreinoCatalogoSeed(
            "Hip Thrust com Barra",
            desc("Barra", "Glúteos", "4", "10 a 12"),
            arquivoImagem = "hip_thrust",
        ),
        TreinoCatalogoSeed("Abdominal Oblíquo", desc("Peso Corporal", "Abdômen", "3", "15 a 20")),
        TreinoCatalogoSeed("Abdominal na Polia", desc("Polia", "Abdômen", "3", "12 a 15")),
        TreinoCatalogoSeed("Abdominal Bicicleta", desc("Peso Corporal", "Abdômen", "3", "15 a 20")),
        TreinoCatalogoSeed("Prancha Lateral", desc("Peso Corporal", "Core", "3", "30 a 45 segundos")),
        TreinoCatalogoSeed("Elevação de Pernas", desc("Peso Corporal", "Abdômen", "3", "12 a 15")),
        TreinoCatalogoSeed("Russian Twist", desc("Peso Corporal / Halter", "Abdômen", "3", "20 a 30")),
        TreinoCatalogoSeed(
            "Ab Wheel (Roda)",
            desc("Roda abdominal", "Abdômen", "3", "8 a 12"),
            arquivoImagem = "ab_wheel",
        ),
        TreinoCatalogoSeed("Remo Ergométrico", desc("Remo", "Corpo inteiro", "1", "20 minutos")),
        TreinoCatalogoSeed(
            "Escada (Stair Climber)",
            desc("Escada", "Pernas", "1", "20 minutos"),
            arquivoImagem = "escada_ergometrica",
        ),
        TreinoCatalogoSeed("Battle Rope", desc("Corda", "Corpo inteiro", "3", "30 segundos")),
        TreinoCatalogoSeed("Kettlebell Swing", desc("Kettlebell", "Corpo inteiro", "4", "12 a 15")),
        TreinoCatalogoSeed("Farmer Walk", desc("Halteres / Kettlebells", "Corpo inteiro", "3", "20 a 40 metros")),
        TreinoCatalogoSeed("Mountain Climber", desc("Peso Corporal", "Corpo inteiro", "3", "30 segundos")),
        TreinoCatalogoSeed("Alongamento de Lombar", desc("Peso Corporal", "Lombar", "2", "30 segundos")),
    )
}
