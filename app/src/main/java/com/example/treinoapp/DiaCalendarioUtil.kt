package com.example.treinoapp

import java.util.Calendar
import java.util.Locale

/**
 * Datas em [yyyy-MM-dd] no fuso local do dispositivo.
 */
object DiaCalendarioUtil {

    fun hojeYmd(): String = calendarParaYmd(Calendar.getInstance())

    fun calendarParaYmd(cal: Calendar): String {
        val y = cal.get(Calendar.YEAR)
        val m = cal.get(Calendar.MONTH) + 1
        val d = cal.get(Calendar.DAY_OF_MONTH)
        return String.format(Locale.US, "%04d-%02d-%02d", y, m, d)
    }

    fun ymdParaCalendar(ymd: String): Calendar {
        val p = ymd.split("-").map { it.toInt() }
        return Calendar.getInstance().apply {
            set(Calendar.YEAR, p[0])
            set(Calendar.MONTH, p[1] - 1)
            set(Calendar.DAY_OF_MONTH, p[2])
            set(Calendar.HOUR_OF_DAY, 12)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
    }

    /** Nome do dia da semana no mesmo formato da agenda (ex.: "Segunda-feira"). */
    fun ymdParaDiaSemanaAgenda(ymd: String): String =
        calendarParaDiaSemanaAgenda(ymdParaCalendar(ymd))

    fun calendarParaDiaSemanaAgenda(cal: Calendar): String =
        when (cal.get(Calendar.DAY_OF_WEEK)) {
            Calendar.MONDAY -> "Segunda-feira"
            Calendar.TUESDAY -> "Terça-feira"
            Calendar.WEDNESDAY -> "Quarta-feira"
            Calendar.THURSDAY -> "Quinta-feira"
            Calendar.FRIDAY -> "Sexta-feira"
            Calendar.SATURDAY -> "Sábado"
            Calendar.SUNDAY -> "Domingo"
            else -> ""
        }

    fun diaSemanaHojeAgenda(): String =
        calendarParaDiaSemanaAgenda(Calendar.getInstance())

    /** Hoje é o mesmo dia da semana que o ecrã da agenda (ex.: lista de Segunda-feira). */
    fun eMesmoDiaSemanaQueHoje(diaAgenda: String): Boolean =
        diaSemanaHojeAgenda() == diaAgenda

    fun diaAnterior(ymd: String): String {
        val c = ymdParaCalendar(ymd)
        c.add(Calendar.DAY_OF_MONTH, -1)
        return calendarParaYmd(c)
    }
}
