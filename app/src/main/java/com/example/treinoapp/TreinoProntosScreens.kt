package com.example.treinoapp

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Link
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaTreinosProntosLista(
    navController: NavController,
    viewModel: TreinoProntoViewModel,
) {
    val resumos by viewModel.resumos.collectAsState()
    var aCarregar by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.garantirPlanosInseridos()
        aCarregar = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Treinos Prontos") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                },
            )
        },
    ) { padding ->
        if (aCarregar) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center,
            ) {
                Text("A carregar planos…")
            }
        } else if (resumos.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Icon(
                    Icons.Filled.FitnessCenter,
                    contentDescription = null,
                    modifier = Modifier.padding(bottom = 16.dp),
                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                )
                Text(
                    text = "Nenhum treino pronto disponível",
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center,
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        aCarregar = true
                        scope.launch {
                            viewModel.garantirPlanosInseridos()
                            aCarregar = false
                        }
                    },
                ) {
                    Text("Carregar planos")
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                item {
                    Text(
                        text = "Planos criados para si. Toque para ver o detalhe e associar à agenda da semana.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                items(resumos, key = { it.plano.id }) { resumo ->
                    Card(
                        onClick = { navController.navigate("detalhe/${resumo.plano.id}") },
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = resumo.plano.nome,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                            )
                            if (resumo.plano.descricao.isNotBlank()) {
                                Text(
                                    text = resumo.plano.descricao.lines().first(),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.padding(top = 4.dp),
                                )
                            }
                            Text(
                                text = "${resumo.quantidadeExercicios} exercícios",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(top = 6.dp),
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaTreinosProntosDetalhe(
    planoId: Long,
    navController: NavController,
    viewModel: TreinoProntoViewModel,
) {
    val context = LocalContext.current
    var planoComItens by remember { mutableStateOf<TreinoProntoComItens?>(null) }
    var dialogAssociar by remember { mutableStateOf(false) }

    LaunchedEffect(planoId) {
        planoComItens = viewModel.carregarPlanoComItens(planoId)
    }

    val plano = planoComItens
    val itensPorDia = remember(plano) {
        plano?.itens
            ?.groupBy { it.item.diaSemana }
            ?.toList()
            ?.sortedBy { (dia, _) -> TreinoAgendaDias.todos.indexOf(dia) }
            .orEmpty()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(plano?.plano?.nome ?: "Treino pronto") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                },
            )
        },
        bottomBar = {
            if (plano != null && plano.itens.isNotEmpty()) {
                Button(
                    onClick = { dialogAssociar = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Link, contentDescription = null)
                        Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                        Text("Associar à agenda")
                    }
                }
            }
        },
    ) { padding ->
        when {
            plano == null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center,
                ) {
                    Text("A carregar…")
                }
            }
            plano.itens.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center,
                ) {
                    Text("Plano sem exercícios.")
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    item {
                        Text(
                            text = plano.plano.descricao,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        HorizontalDivider()
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    itensPorDia.forEach { (dia, itens) ->
                        item(key = "header_$dia") {
                            Text(
                                text = tituloDiaTreino(plano.plano.nome, dia),
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(vertical = 8.dp),
                            )
                        }
                        items(itens, key = { it.item.id }) { linha ->
                            Card(modifier = Modifier.fillMaxWidth()) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                        text = linha.modelo.nome,
                                        style = MaterialTheme.typography.titleMedium,
                                    )
                                    val series = linha.item.descricaoOverride
                                        ?.lineSequence()
                                        ?.firstOrNull()
                                        ?.removePrefix("Séries/repetições: ")
                                        ?.substringBefore('\n')
                                    if (!series.isNullOrBlank()) {
                                        Text(
                                            text = series,
                                            style = MaterialTheme.typography.labelLarge,
                                            color = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.padding(top = 4.dp),
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (dialogAssociar) {
        val diasNoPlano = itensPorDia.map { it.first }
        AlertDialog(
            onDismissRequest = { dialogAssociar = false },
            title = { Text("Associar à agenda?") },
            text = {
                Text(
                    text = buildString {
                        append("Os exercícios serão adicionados aos dias:\n\n")
                        diasNoPlano.forEach { append("• $it\n") }
                        append("\nPode editar ou remover depois na Agenda de treino.")
                    },
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        dialogAssociar = false
                        viewModel.associarPlanoCompletoAgenda(
                            planoId = planoId,
                            onSucesso = { qtd, dias ->
                                Toast.makeText(
                                    context,
                                    "$qtd exercícios em $dias dias da agenda",
                                    Toast.LENGTH_LONG,
                                ).show()
                                navController.navigate("lista_dias")
                            },
                            onPlanoVazio = {
                                Toast.makeText(context, "Plano vazio", Toast.LENGTH_SHORT).show()
                            },
                            onErro = {
                                Toast.makeText(context, "Erro ao associar", Toast.LENGTH_SHORT).show()
                            },
                        )
                    },
                ) {
                    Text("Associar")
                }
            },
            dismissButton = {
                TextButton(onClick = { dialogAssociar = false }) {
                    Text("Cancelar")
                }
            },
        )
    }
}

private fun tituloDiaTreino(planoNome: String, diaSemana: String): String {
    val perda = planoNome.contains("Perda de Peso", ignoreCase = true)
    val shape = planoNome.contains("Manutenção", ignoreCase = true) ||
        planoNome.contains("Shape", ignoreCase = true)
    return when (diaSemana) {
        "Segunda-feira" -> when {
            perda -> "Segunda — Peito + Tríceps + Cardio"
            shape -> "Segunda — Peito + Tríceps + Cardio leve"
            else -> "Segunda — Peito + Tríceps"
        }
        "Terça-feira" -> when {
            perda -> "Terça — Pernas + Glúteos + Cardio"
            else -> "Terça — Costas + Bíceps"
        }
        "Quarta-feira" -> when {
            perda -> "Quarta — Costas + Bíceps + HIIT"
            shape -> "Quarta — Pernas + Cardio"
            else -> "Quarta — Pernas completas"
        }
        "Quinta-feira" -> when {
            perda -> "Quinta — Ombro + Abdômen + Funcional"
            else -> "Quinta — Ombro + Abdômen"
        }
        "Sexta-feira" -> when {
            perda -> "Sexta — Metabólico full body"
            shape -> "Sexta — Full body leve + condicionamento"
            else -> "Sexta — Superior completo"
        }
        else -> diaSemana
    }
}
