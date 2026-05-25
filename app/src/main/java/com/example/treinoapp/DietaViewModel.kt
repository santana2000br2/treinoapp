package com.example.treinoapp

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DietaViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = AppDatabase.getInstance(application).dietaAlimentoDao()

    fun alimentosPorTipo(tipoRefeicao: String) = dao.observarPorTipo(tipoRefeicao)

    fun adicionarAlimento(
        tipoRefeicao: String,
        nome: String,
        quantidade: Int,
        unidadeMedida: String,
    ) {
        val n = nome.trim()
        if (n.isEmpty() || quantidade <= 0) return
        val unidade = when (unidadeMedida) {
            DietaUnidadeMedida.GRAMAS, DietaUnidadeMedida.UNIDADE -> unidadeMedida
            else -> DietaUnidadeMedida.GRAMAS
        }
        viewModelScope.launch(Dispatchers.IO) {
            dao.insert(
                DietaAlimentoEntity(
                    tipoRefeicao = tipoRefeicao,
                    nome = n,
                    quantidade = quantidade,
                    unidadeMedida = unidade,
                ),
            )
        }
    }

    fun removerAlimento(entity: DietaAlimentoEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.delete(entity)
        }
    }
}
