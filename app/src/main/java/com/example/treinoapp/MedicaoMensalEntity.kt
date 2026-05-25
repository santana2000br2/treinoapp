package com.example.treinoapp

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "medicao_mensal",
    indices = [Index(value = ["mesReferencia"], unique = true)],
)
data class MedicaoMensalEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    /** Formato yyyy-MM — um registo por mês. */
    val mesReferencia: String,
    val dataMillis: Long,
    val pesoKg: Double? = null,
    val alturaCm: Double? = null,
    val cinturaCm: Double? = null,
    val abdomenCm: Double? = null,
    val peitoCm: Double? = null,
    val quadrilCm: Double? = null,
    val bracoEsquerdoCm: Double? = null,
    val bracoDireitoCm: Double? = null,
    val coxaEsquerdaCm: Double? = null,
    val coxaDireitaCm: Double? = null,
    val panturrilhaEsquerdaCm: Double? = null,
    val panturrilhaDireitaCm: Double? = null,
)
