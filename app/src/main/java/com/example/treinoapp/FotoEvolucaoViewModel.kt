package com.example.treinoapp

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class FotoPosicaoInput(
    val pathExistente: String? = null,
    val uriNova: Uri? = null,
) {
    fun temFoto(): Boolean = pathExistente != null || uriNova != null
}

class FotoEvolucaoViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = AppDatabase.getInstance(application).fotoEvolucaoDao()
    private val app get() = getApplication<Application>()

    val sessoesComFotos: StateFlow<List<FotoEvolucaoSessaoComFotos>> = flow {
        dao.observarSessoes().collect { sessoes ->
            val lista = sessoes.map { sessao ->
                FotoEvolucaoSessaoComFotos(
                    sessao = sessao,
                    fotos = dao.fotosPorSessao(sessao.id),
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

    suspend fun carregarSessao(id: Long): FotoEvolucaoSessaoComFotos? = withContext(Dispatchers.IO) {
        val sessao = dao.sessaoPorId(id) ?: return@withContext null
        FotoEvolucaoSessaoComFotos(sessao, dao.fotosPorSessao(id))
    }

    fun salvarSessao(
        sessaoId: Long,
        dataMillis: Long,
        fotosPorPosicao: Map<String, FotoPosicaoInput>,
        onSucesso: () -> Unit,
        onSemFotos: () -> Unit,
        onErro: () -> Unit,
    ) {
        val ativos = fotosPorPosicao.filter { it.value.temFoto() }
        if (ativos.isEmpty()) {
            onSemFotos()
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val idSessao = if (sessaoId == 0L) {
                    dao.insertSessao(FotoEvolucaoSessaoEntity(dataMillis = dataMillis))
                } else {
                    dao.updateSessao(
                        FotoEvolucaoSessaoEntity(id = sessaoId, dataMillis = dataMillis),
                    )
                    sessaoId
                }

                val existentes = dao.fotosPorSessao(idSessao).associateBy { it.posicao }

                ativos.forEach { (posicao, input) ->
                    when {
                        input.uriNova != null -> {
                            val path = FotoEvolucaoImagemUtil.persistirDaGaleria(
                                app,
                                input.uriNova,
                                idSessao,
                                posicao,
                            ) ?: throw IllegalStateException("Falha ao guardar $posicao")
                            val anterior = existentes[posicao]
                            if (anterior != null) {
                                FotoEvolucaoImagemUtil.apagarArquivo(app, anterior.uriLocal)
                                dao.updateFoto(anterior.copy(uriLocal = path))
                            } else {
                                dao.insertFoto(
                                    FotoEvolucaoFotoEntity(
                                        sessaoId = idSessao,
                                        posicao = posicao,
                                        uriLocal = path,
                                    ),
                                )
                            }
                        }
                        input.pathExistente != null && existentes[posicao] == null -> {
                            dao.insertFoto(
                                FotoEvolucaoFotoEntity(
                                    sessaoId = idSessao,
                                    posicao = posicao,
                                    uriLocal = input.pathExistente,
                                ),
                            )
                        }
                    }
                }

                existentes.keys.filter { pos -> !ativos.containsKey(pos) }.forEach { posicao ->
                    existentes[posicao]?.let { foto ->
                        FotoEvolucaoImagemUtil.apagarArquivo(app, foto.uriLocal)
                        dao.deleteFoto(foto)
                    }
                }

                withContext(Dispatchers.Main) { onSucesso() }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) { onErro() }
            }
        }
    }

    fun excluirSessao(sessaoId: Long, onSucesso: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val sessao = dao.sessaoPorId(sessaoId) ?: return@launch
            dao.fotosPorSessao(sessaoId).forEach { foto ->
                FotoEvolucaoImagemUtil.apagarArquivo(app, foto.uriLocal)
            }
            dao.deleteSessao(sessao)
            withContext(Dispatchers.Main) { onSucesso() }
        }
    }
}
