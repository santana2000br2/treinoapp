package com.example.treinoapp

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.withContext

class TreinoProntoViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getInstance(application)
    private val planoDao = db.treinoProntoDao()
    private val modeloDao = db.modeloDao()
    private val treinoDiaDao = db.treinoDiaDao()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            garantirPlanosInseridos()
        }
    }

    suspend fun garantirPlanosInseridos() {
        TreinoProntosInicializador.popularSeNecessario(planoDao, modeloDao)
    }

    val resumos: StateFlow<List<TreinoProntoResumo>> = flow {
        planoDao.observarPlanos().collect { planos ->
            val lista = planos.map { plano ->
                TreinoProntoResumo(
                    plano = plano,
                    quantidadeExercicios = planoDao.contarItens(plano.id),
                )
            }
            emit(lista)
        }
    }
        .flowOn(Dispatchers.IO)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList(),
        )

    suspend fun carregarPlanoComItens(planoId: Long): TreinoProntoComItens? = withContext(Dispatchers.IO) {
        val plano = planoDao.planoPorId(planoId) ?: return@withContext null
        val itens = planoDao.itensPorPlano(planoId).mapNotNull { item ->
            val modelo = modeloDao.getById(item.modeloId) ?: return@mapNotNull null
            TreinoProntoItemComModelo(item = item, modelo = modelo)
        }
        TreinoProntoComItens(plano = plano, itens = itens)
    }

    /**
     * Copia cada exercício do plano para o respetivo dia da agenda (Segunda a Sexta, etc.).
     */
    fun associarPlanoCompletoAgenda(
        planoId: Long,
        onSucesso: (Int, Int) -> Unit,
        onPlanoVazio: () -> Unit,
        onErro: () -> Unit,
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val itens = planoDao.itensPorPlano(planoId)
                    .filter { it.diaSemana.isNotBlank() }
                if (itens.isEmpty()) {
                    withContext(Dispatchers.Main) { onPlanoVazio() }
                    return@launch
                }
                var adicionados = 0
                itens.forEach { item ->
                    val modelo = modeloDao.getById(item.modeloId) ?: return@forEach
                    treinoDiaDao.insert(
                        modelo.toTreinoDiaEntity(
                            diaSemana = item.diaSemana,
                            descricao = item.descricaoOverride ?: modelo.descricao,
                        ),
                    )
                    adicionados++
                }
                val dias = itens.map { it.diaSemana }.distinct().size
                withContext(Dispatchers.Main) { onSucesso(adicionados, dias) }
            } catch (_: Exception) {
                withContext(Dispatchers.Main) { onErro() }
            }
        }
    }

    private fun ModeloEntity.toTreinoDiaEntity(
        diaSemana: String,
        descricao: String,
    ): TreinoDiaEntity =
        TreinoDiaEntity(
            diaSemana = diaSemana,
            nome = nome,
            descricao = descricao,
            imagem1 = imagem1,
            imagem2 = imagem2,
            video = video,
        )
}
