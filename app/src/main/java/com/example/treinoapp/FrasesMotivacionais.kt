package com.example.treinoapp

import java.util.Calendar

/**
 * Frases que rodam **por dia civil** (muda à meia-noite no fuso do dispositivo).
 */
object FrasesMotivacionais {

    private val frases: List<String> = listOf(
        "Hoje é o dia de fazer melhor que ontem.",
        "Você não precisa de motivação, precisa começar.",
        "Um treino ruim ainda é melhor que nenhum treino.",
        "Constância vence talento. Sempre.",
        "Seu corpo aguenta. Sua mente é que precisa acreditar.",
        "Não negocie com a preguiça.",
        "O resultado vem para quem não desiste.",
        "Mais um treino, mais perto do seu objetivo.",
        "Você já evoluiu mais do que imagina.",
        "Disciplina é fazer mesmo sem vontade.",
        "O difícil hoje vira aquecimento amanhã.",
        "Sem esforço, sem mudança.",
        "Você não começou à toa — continue.",
        "Cada repetição conta.",
        "Pequenos passos, grandes resultados.",
        "Treine pelo que você quer se tornar.",
        "Seu futuro agradece o esforço de hoje.",
        "A dor passa, o orgulho fica.",
        "Não pare até se orgulhar.",
        "Você é mais forte do que pensa.",
        "Foco no processo, não só no resultado.",
        "O único treino perdido é o que você não fez.",
        "Faça por você. Sempre.",
        "Você não precisa ser perfeito, só consistente.",
        "Treine mesmo quando ninguém estiver olhando.",
        "Resultados silenciosos, conquistas barulhentas.",
        "Hoje você constrói o corpo de amanhã.",
        "Não espere motivação, crie hábito.",
        "A evolução vem para quem insiste.",
        "Mais disciplina, menos desculpas.",
        "Seu limite é só o começo.",
        "Você já venceu só por não desistir.",
        "Treinar é um ato de respeito com você mesmo.",
        "Um dia ou dia um. Você escolhe.",
        "Continue — você está no caminho certo.",
        "O progresso é lento, mas vale a pena.",
        "Não é sorte, é esforço acumulado.",
        "Se fosse fácil, todo mundo faria.",
        "O corpo alcança o que a mente acredita.",
        "Hoje você supera o “eu” de ontem.",
        "Não se compare, evolua.",
        "Cada treino é um investimento em você.",
        "Vai com preguiça mesmo, mas vai.",
        "Você não precisa estar motivado, só comprometido.",
        "Seu objetivo não combina com desistência.",
        "A mudança começa quando você decide continuar.",
        "Um passo por vez ainda é progresso.",
        "Seja constante, não perfeito.",
        "O esforço de hoje é o resultado de amanhã.",
        "Você chegou até aqui — não pare agora.",
    )

    /** Índice estável por dia (ano + dia do ano) para a frase mudar todos os dias. */
    fun fraseDoDia(): String {
        val cal = Calendar.getInstance()
        val indiceDia = cal.get(Calendar.YEAR) * 400 + cal.get(Calendar.DAY_OF_YEAR)
        return frases[indiceDia % frases.size]
    }
}
