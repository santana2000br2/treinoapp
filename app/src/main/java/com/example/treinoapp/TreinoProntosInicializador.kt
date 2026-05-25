package com.example.treinoapp

import android.util.Log

object TreinoProntosInicializador {

    private const val TAG = "TreinoProntosSeed"

    suspend fun popularSeNecessario(
        planoDao: TreinoProntoDao,
        modeloDao: ModeloDao,
    ) {
        try {
            val modelosPorNome = modeloDao.getAllSync().associateBy { it.nome }
            if (modelosPorNome.isEmpty()) {
                Log.w(TAG, "Catálogo vazio — adie o seed dos treinos prontos")
                return
            }

            TreinoProntosSeed.planos.forEach { planoSeed ->
                val existente = planoDao.planoPorNome(planoSeed.nome)
                if (existente != null) {
                    val qtdItens = planoDao.contarItens(existente.id)
                    if (qtdItens >= planoSeed.itens.size) {
                        Log.d(TAG, "Plano já completo: ${planoSeed.nome} ($qtdItens itens)")
                        return@forEach
                    }
                    Log.d(TAG, "Recriando plano incompleto: ${planoSeed.nome} ($qtdItens/${planoSeed.itens.size})")
                    planoDao.deletePlanoPorId(existente.id)
                }

                val planoId = planoDao.insertPlano(
                    TreinoProntoEntity(
                        nome = planoSeed.nome,
                        descricao = planoSeed.descricao,
                    ),
                )

                var inseridos = 0
                planoSeed.itens.forEachIndexed { index, itemSeed ->
                    val modelo = modelosPorNome[itemSeed.nomeCatalogo]
                    if (modelo == null) {
                        Log.w(TAG, "Exercício não encontrado: ${itemSeed.nomeCatalogo}")
                        return@forEachIndexed
                    }
                    planoDao.insertItem(
                        TreinoProntoItemEntity(
                            treinoProntoId = planoId,
                            modeloId = modelo.id,
                            diaSemana = itemSeed.diaSemana,
                            ordem = index,
                            descricaoOverride = TreinoProntosSeed.descricaoParaAgenda(
                                itemSeed.seriesReps,
                                modelo.descricao,
                            ),
                        ),
                    )
                    inseridos++
                }
                Log.i(TAG, "Plano «${planoSeed.nome}»: $inseridos/${planoSeed.itens.size} exercícios")
                if (inseridos == 0) {
                    planoDao.deletePlanoPorId(planoId)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Falha ao popular treinos prontos", e)
        }
    }
}
