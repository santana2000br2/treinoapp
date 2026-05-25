package com.example.treinoapp

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DatabaseInitializer(private val context: Context) {

    fun populateIfNeeded() {
        CoroutineScope(Dispatchers.IO).launch {
            val db = AppDatabase.getInstance(context)
            val modeloDao = db.modeloDao()

            if (modeloDao.getCount() == 0) {
                CatalogoTreinos.todos.forEach { seed ->
                    modeloDao.insert(seed.toModeloEntity(context, vincularImagem = false))
                }
            }
            inserirModelosFaltantes(modeloDao)
            sincronizarImagens()
        }
    }

    suspend fun sincronizarImagens() {
        val db = AppDatabase.getInstance(context)
        sincronizarImagensDrawable(db.modeloDao(), db.treinoDiaDao())
    }

    private suspend fun inserirModelosFaltantes(modeloDao: ModeloDao) {
        val nomesExistentes = modeloDao.getAllNomes().toSet()
        CatalogoTreinos.todos
            .filter { it.nome !in nomesExistentes }
            .forEach { seed -> modeloDao.insert(seed.toModeloEntity(context, vincularImagem = false)) }
    }

    private suspend fun sincronizarImagensDrawable(
        modeloDao: ModeloDao,
        treinoDiaDao: TreinoDiaDao,
    ) {
        val seedsPorNome = CatalogoTreinos.todos.associateBy { it.nome }

        modeloDao.getAllSync().forEach { modelo ->
            val arquivo = seedsPorNome[modelo.nome]?.arquivoImagem
            val uri = ExercicioImagemUtil.vincularImagemSeExistir(context, modelo.nome, arquivo)
                ?: return@forEach
            val uriLegada = modelo.imagem1?.startsWith("file://") == true
            if (!ExercicioImagemUtil.uriImagemValida(context, modelo.imagem1) ||
                modelo.imagem1 != uri ||
                uriLegada
            ) {
                modeloDao.update(modelo.copy(imagem1 = uri))
            }
        }

        treinoDiaDao.getAllSync().forEach { treino ->
            val arquivo = seedsPorNome[treino.nome]?.arquivoImagem
            val uri = ExercicioImagemUtil.vincularImagemSeExistir(context, treino.nome, arquivo)
                ?: return@forEach
            val uriLegada = treino.imagem1?.startsWith("file://") == true
            if (!ExercicioImagemUtil.uriImagemValida(context, treino.imagem1) ||
                treino.imagem1 != uri ||
                uriLegada
            ) {
                treinoDiaDao.update(treino.copy(imagem1 = uri))
            }
        }
    }
}
