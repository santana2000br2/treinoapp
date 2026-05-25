package com.example.treinoapp

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters

@Entity(tableName = "treino_dia")
data class TreinoDiaEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val diaSemana: String,
    val nome: String,
    val descricao: String,
    val imagem1: String?,
    val imagem2: String?,
    val video: String?
)