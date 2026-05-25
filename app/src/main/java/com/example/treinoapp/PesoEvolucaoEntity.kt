package com.example.treinoapp

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "peso_evolucao")
data class PesoEvolucaoEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    /** Peso em quilogramas. */
    val pesoKg: Double,
    /** Instantâneo da data/hora em que o peso foi registado. */
    val dataMillis: Long,
)
