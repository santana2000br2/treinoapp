package com.example.treinoapp

/**
 * Calcula quantos dias seguidos cumpriram a regra: pelo menos metade dos treinos
 * agendados para esse dia da semana marcados como feitos nessa data civil.
 * Dias sem nenhum treino na agenda são ignorados (não quebram nem somam).
 *
 * Se hoje ainda é dia de treino e a meta não foi cumprida, a sequência conta a partir de ontem.
 */
object TreinoStreakCalculator {

    suspend fun calcular(
        conclusaoDao: TreinoConclusaoDao,
        treinoDiaDao: TreinoDiaDao,
    ): Int {
        val treinoIdsCache = mutableMapOf<String, List<Long>>()

        suspend fun idsNoPlano(diaAgenda: String): List<Long> =
            treinoIdsCache.getOrPut(diaAgenda) { treinoDiaDao.idsPorDia(diaAgenda) }

        suspend fun diaTemTreinos(ymd: String): Boolean =
            idsNoPlano(DiaCalendarioUtil.ymdParaDiaSemanaAgenda(ymd)).isNotEmpty()

        suspend fun diaMeta50(ymd: String): Boolean {
            val nome = DiaCalendarioUtil.ymdParaDiaSemanaAgenda(ymd)
            val ids = idsNoPlano(nome)
            if (ids.isEmpty()) return false
            val minimo = (ids.size + 1) / 2
            val feitos = if (ids.isEmpty()) 0 else conclusaoDao.contarConcluidosNoDiaParaIds(ymd, ids)
            return feitos >= minimo
        }

        var cursor = DiaCalendarioUtil.hojeYmd()
        if (diaTemTreinos(cursor) && !diaMeta50(cursor)) {
            cursor = DiaCalendarioUtil.diaAnterior(cursor)
        }

        var streak = 0
        repeat(400) {
            if (!diaTemTreinos(cursor)) {
                cursor = DiaCalendarioUtil.diaAnterior(cursor)
                return@repeat
            }
            if (diaMeta50(cursor)) {
                streak++
                cursor = DiaCalendarioUtil.diaAnterior(cursor)
            } else {
                return streak
            }
        }
        return streak
    }
}
