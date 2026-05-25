package com.example.treinoapp

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EvolucaoViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = AppDatabase.getInstance(application).pesoEvolucaoDao()

    val pesos = dao.observarTodos()

    fun adicionarPeso(pesoKg: Double, dataMillis: Long) {
        if (pesoKg <= 0.0 || pesoKg > 500.0) return
        viewModelScope.launch(Dispatchers.IO) {
            dao.insert(
                PesoEvolucaoEntity(
                    pesoKg = pesoKg,
                    dataMillis = dataMillis,
                ),
            )
        }
    }
}
