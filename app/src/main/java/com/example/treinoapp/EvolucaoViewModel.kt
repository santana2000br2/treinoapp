package com.example.treinoapp

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EvolucaoViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getInstance(application)
    private val dao = db.medicaoMensalDao()

    val medicoes: Flow<List<MedicaoMensalEntity>> = dao.observarTodos()

    suspend fun obterPorId(id: Long): MedicaoMensalEntity? = dao.porId(id)

    suspend fun obterPorMes(mesReferencia: String): MedicaoMensalEntity? = dao.porMes(mesReferencia)

    fun salvarMedicao(
        id: Long = 0L,
        dataMillis: Long,
        pesoKg: Double?,
        alturaCm: Double?,
        cinturaCm: Double?,
        abdomenCm: Double?,
        peitoCm: Double?,
        quadrilCm: Double?,
        bracoEsquerdoCm: Double?,
        bracoDireitoCm: Double?,
        coxaEsquerdaCm: Double?,
        coxaDireitaCm: Double?,
        panturrilhaEsquerdaCm: Double?,
        panturrilhaDireitaCm: Double?,
        onSucesso: () -> Unit,
        onMesDuplicado: () -> Unit,
        onSemDados: () -> Unit,
    ) {
        val valores = listOf(
            pesoKg, alturaCm, cinturaCm, abdomenCm, peitoCm, quadrilCm,
            bracoEsquerdoCm, bracoDireitoCm, coxaEsquerdaCm, coxaDireitaCm,
            panturrilhaEsquerdaCm, panturrilhaDireitaCm,
        )
        if (valores.all { it == null }) {
            onSemDados()
            return
        }

        val mesReferencia = MedicaoCatalogo.millisParaMesReferencia(dataMillis)
        viewModelScope.launch(Dispatchers.IO) {
            val existente = dao.porMes(mesReferencia)
            if (existente != null && existente.id != id) {
                withContext(Dispatchers.Main) { onMesDuplicado() }
                return@launch
            }

            val entidade = MedicaoMensalEntity(
                id = id,
                mesReferencia = mesReferencia,
                dataMillis = dataMillis,
                pesoKg = pesoKg,
                alturaCm = alturaCm,
                cinturaCm = cinturaCm,
                abdomenCm = abdomenCm,
                peitoCm = peitoCm,
                quadrilCm = quadrilCm,
                bracoEsquerdoCm = bracoEsquerdoCm,
                bracoDireitoCm = bracoDireitoCm,
                coxaEsquerdaCm = coxaEsquerdaCm,
                coxaDireitaCm = coxaDireitaCm,
                panturrilhaEsquerdaCm = panturrilhaEsquerdaCm,
                panturrilhaDireitaCm = panturrilhaDireitaCm,
            )

            if (id == 0L) {
                dao.insert(entidade)
            } else {
                dao.update(entidade)
            }
            withContext(Dispatchers.Main) { onSucesso() }
        }
    }

    fun excluirMedicao(id: Long, onConcluido: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.porId(id)?.let { dao.delete(it) }
            withContext(Dispatchers.Main) { onConcluido() }
        }
    }
}
