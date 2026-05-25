package com.example.treinoapp

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
data class TreinoComId(
    val id: Long,
    val treino: Treino
)

class TreinoViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getInstance(application)

    // Flow de ModeloEntity (com String) para operações que precisam do id
    private val _modelosEntity = MutableStateFlow<List<ModeloEntity>>(emptyList())
    val modelosEntity: StateFlow<List<ModeloEntity>> = _modelosEntity.asStateFlow()

    // Flow de Treino (com Uri) para as telas
    private val _modelos = MutableStateFlow<List<Treino>>(emptyList())
    val modelos: StateFlow<List<Treino>> = _modelos.asStateFlow()

    // Mapa para armazenar os flows de treinos por dia, agora com ID
    private val treinosPorDiaMap = mutableMapOf<String, MutableStateFlow<List<TreinoComId>>>()

    private val _sequenciaDias = MutableStateFlow(0)
    val sequenciaDiasTreino: StateFlow<Int> = _sequenciaDias.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            DatabaseInitializer(getApplication()).sincronizarImagens()
        }
        viewModelScope.launch {
            db.modeloDao().getAll().collect { entities ->
                _modelosEntity.value = entities
                _modelos.value = entities.map { it.toTreino() }
            }
        }
        viewModelScope.launch(Dispatchers.IO) {
            combine(
                db.treinoConclusaoDao().observarTodas(),
                db.treinoDiaDao().observarTodos(),
            ) { _, _ -> }
                .collect {
                    _sequenciaDias.value = TreinoStreakCalculator.calcular(
                        db.treinoConclusaoDao(),
                        db.treinoDiaDao(),
                    )
                }
        }
    }

    fun observarIdsConcluidosNoDia(ymd: String) =
        db.treinoConclusaoDao().observarIdsNoDia(ymd).map { it.toSet() }

    /**
     * Marca ou desmarca um treino da agenda como feito **hoje** (data civil).
     * Só tem efeito se [diaTela] for o dia da semana de hoje (ex.: à terça só na Terça-feira).
     */
    fun marcarConclusaoDiaHoje(diaTela: String, treinoDiaId: Long, feito: Boolean) {
        if (!DiaCalendarioUtil.eMesmoDiaSemanaQueHoje(diaTela)) return
        viewModelScope.launch(Dispatchers.IO) {
            val ymd = DiaCalendarioUtil.hojeYmd()
            if (feito) {
                db.treinoConclusaoDao().insert(TreinoConclusaoEntity(ymd, treinoDiaId))
            } else {
                db.treinoConclusaoDao().delete(ymd, treinoDiaId)
            }
        }
    }

    fun getTreinosPorDia(dia: String): StateFlow<List<TreinoComId>> {
        return treinosPorDiaMap.getOrPut(dia) {
            val flow = MutableStateFlow<List<TreinoComId>>(emptyList())
            viewModelScope.launch {
                db.treinoDiaDao().getTreinosPorDia(dia).collect { entities ->
                    flow.value = entities.map { entity ->
                        TreinoComId(entity.id, entity.toTreino())
                    }
                }
            }
            flow
        }
    }

    fun inserirModelo(treino: Treino) {
        viewModelScope.launch(Dispatchers.IO) {
            db.modeloDao().insert(treino.toModeloEntity())
        }
    }

    fun atualizarModelo(treino: Treino, id: Long) {
        Log.d("DEBUG_VIEWMODEL", "atualizarModelo: id=$id, video=${treino.video}")
        viewModelScope.launch(Dispatchers.IO) {
            db.modeloDao().update(treino.toModeloEntity(id))
        }
    }

    fun removerModelo(treino: Treino, id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            db.modeloDao().delete(treino.toModeloEntity(id))
        }
    }

    fun adicionarTreinoAoDia(dia: String, treino: Treino) {
        viewModelScope.launch(Dispatchers.IO) {
            db.treinoDiaDao().insert(treino.toTreinoDiaEntity(dia))
        }
    }

    fun removerTreinoDoDia(treino: Treino, dia: String, id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            db.treinoDiaDao().delete(treino.toTreinoDiaEntity(dia, id))
        }
    }

    fun atualizarTreinoDoDia(treino: Treino, dia: String, id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            db.treinoDiaDao().update(treino.toTreinoDiaEntity(dia, id))
        }
    }

    fun limparDia(dia: String) {
        viewModelScope.launch(Dispatchers.IO) {
            db.treinoDiaDao().deleteAllFromDay(dia)
        }
    }

    // ==================== Funções de conversão ====================
    // De entidade (com String) para Treino (com Uri)
    private fun ModeloEntity.toTreino(): Treino = Treino(
        nome = nome,
        descricao = descricao,
        imagem1 = imagem1?.let { Uri.parse(it) },
        imagem2 = imagem2?.let { Uri.parse(it) },
        video = video?.let { Uri.parse(it) }
    )

    private fun TreinoDiaEntity.toTreino(): Treino = Treino(
        nome = nome,
        descricao = descricao,
        imagem1 = imagem1?.let { Uri.parse(it) },
        imagem2 = imagem2?.let { Uri.parse(it) },
        video = video?.let { Uri.parse(it) }
    )

    // De Treino (com Uri) para entidade (com String)
    private fun Treino.toModeloEntity(id: Long = 0): ModeloEntity = ModeloEntity(
        id = id,
        nome = nome,
        descricao = descricao,
        imagem1 = imagem1?.toString(),
        imagem2 = imagem2?.toString(),
        video = video?.toString()
    )

    private fun Treino.toTreinoDiaEntity(dia: String, id: Long = 0): TreinoDiaEntity = TreinoDiaEntity(
        id = id,
        diaSemana = dia,
        nome = nome,
        descricao = descricao,
        imagem1 = imagem1?.toString(),
        imagem2 = imagem2?.toString(),
        video = video?.toString()
    )
}