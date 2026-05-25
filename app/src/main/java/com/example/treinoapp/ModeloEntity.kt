package com.example.treinoapp

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "modelos")
data class ModeloEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nome: String,
    val descricao: String,
    val imagem1: String?,
    val imagem2: String?,
    val video: String?
)