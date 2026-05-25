package com.example.treinoapp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.Image
import androidx.navigation.NavController
import com.example.treinoapp.R

object DietaRefeicaoIds {
    const val CAFE_MANHA = "cafe_manha"
    const val LANCHE_MANHA = "lanche_manha"
    const val ALMOCO = "almoco"
    const val LANCHE_TARDE = "lanche_tarde"
    const val JANTA = "janta"
}

/**
 * Imagens dos cartões: coloca os PNG em `app/src/main/res/drawable/` com estes nomes
 * (obrigatório em minúsculas e `_`; sem espaços nem acentos no nome do ficheiro):
 *
 * | Refeição           | Ficheiro               | Ligação no código              |
 * |--------------------|------------------------|--------------------------------|
 * | Café da manhã      | dieta_cafe_manha.png   | R.drawable.dieta_cafe_manha    |
 * | Lanche da manhã    | dieta_lanche_manha.png | R.drawable.dieta_lanche_manha  |
 * | Almoço             | dieta_almoco.png       | R.drawable.dieta_almoco        |
 * | Lanche da tarde    | dieta_lanche_tarde.png | R.drawable.dieta_lanche_tarde  |
 * | Janta              | dieta_janta.png        | R.drawable.dieta_janta         |
 *
 * Se o teu PNG tiver outro nome, ou renomeia o ficheiro para coincidir com a tabela,
 * ou altera só a linha `imagemDrawableRes = R.drawable.NOME_QUE_EXISTE_EM_drawable` abaixo.
 */
private data class OpcaoRefeicaoUi(
    val id: String,
    val titulo: String,
    /** Se não for null, a imagem preenche o cartão (por cima do gradiente de fallback). */
    val imagemDrawableRes: Int?,
    val coresGradienteFallback: List<Color>,
    val fullWidth: Boolean,
)

private val opcoesRefeicaoDieta = listOf(
    OpcaoRefeicaoUi(
        DietaRefeicaoIds.CAFE_MANHA,
        "Café da manhã",
        imagemDrawableRes = R.drawable.dieta_cafe_manha,
        coresGradienteFallback = listOf(Color(0xFF6D4C41), Color(0xFF8D6E63)),
        fullWidth = false,
    ),
    OpcaoRefeicaoUi(
        DietaRefeicaoIds.LANCHE_MANHA,
        "Lanche da manhã",
        imagemDrawableRes = R.drawable.dieta_lanche_manha,
        coresGradienteFallback = listOf(Color(0xFFFF8F00), Color(0xFFFFB74D)),
        fullWidth = false,
    ),
    OpcaoRefeicaoUi(
        DietaRefeicaoIds.ALMOCO,
        "Almoço",
        imagemDrawableRes = R.drawable.dieta_almoco,
        coresGradienteFallback = listOf(Color(0xFF2E7D32), Color(0xFF66BB6A)),
        fullWidth = false,
    ),
    OpcaoRefeicaoUi(
        DietaRefeicaoIds.LANCHE_TARDE,
        "Lanche da tarde",
        imagemDrawableRes = R.drawable.dieta_lanche_tarde,
        coresGradienteFallback = listOf(Color(0xFF00897B), Color(0xFF4DB6AC)),
        fullWidth = false,
    ),
    OpcaoRefeicaoUi(
        DietaRefeicaoIds.JANTA,
        "Janta",
        imagemDrawableRes = R.drawable.dieta_janta,
        coresGradienteFallback = listOf(Color(0xFF37474F), Color(0xFF546E7A)),
        fullWidth = true,
    ),
)

fun tituloRefeicaoDieta(tipoId: String): String =
    opcoesRefeicaoDieta.find { it.id == tipoId }?.titulo ?: "Dieta"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaDietaMenu(
    navController: NavController,
    modifier: Modifier = Modifier,
) {
    val primeirasQuatro = opcoesRefeicaoDieta.take(4)
    val janta = opcoesRefeicaoDieta[4]

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Dieta") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                },
            )
        },
        containerColor = Color(0xFFE8F5E9),
    ) { padding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFE8F5E9)),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(primeirasQuatro, key = { it.id }) { opcao ->
                DietaCartaoRefeicao(
                    opcao = opcao,
                    quadrado = true,
                    onClick = {
                        navController.navigate("dieta_refeicao/${opcao.id}")
                    },
                )
            }
            item(span = { GridItemSpan(2) }) {
                DietaCartaoRefeicao(
                    opcao = janta,
                    quadrado = false,
                    onClick = {
                        navController.navigate("dieta_refeicao/${janta.id}")
                    },
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DietaCartaoRefeicao(
    opcao: OpcaoRefeicaoUi,
    quadrado: Boolean,
    onClick: () -> Unit,
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (quadrado) Modifier.aspectRatio(1f)
                else Modifier.height(160.dp),
            ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(opcao.coresGradienteFallback),
                    ),
            )
            opcao.imagemDrawableRes?.let { resId ->
                Image(
                    painter = painterResource(id = resId),
                    contentDescription = opcao.titulo,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                )
            }
            // Vinheta leve para o título branco ler bem sobre foto ou gradiente
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.45f),
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.22f),
                            ),
                        ),
                    ),
            )
            Text(
                text = opcao.titulo,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(12.dp),
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaDietaCadastroAlimentos(
    navController: NavController,
    tipoRefeicaoId: String,
    viewModel: DietaViewModel,
    modifier: Modifier = Modifier,
) {
    val titulo = tituloRefeicaoDieta(tipoRefeicaoId)
    val alimentos by viewModel.alimentosPorTipo(tipoRefeicaoId).collectAsState(initial = emptyList())
    var mostrarDialogo by remember { mutableStateOf(false) }
    var textoNovo by remember { mutableStateOf("") }
    var textoQuantidade by remember { mutableStateOf("") }
    var unidadeMedida by remember { mutableStateOf(DietaUnidadeMedida.GRAMAS) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(titulo) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                },
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    textoNovo = ""
                    textoQuantidade = ""
                    unidadeMedida = DietaUnidadeMedida.GRAMAS
                    mostrarDialogo = true
                },
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Adicionar alimento")
            }
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
        ) {
            Text(
                text = "O que podes comer nesta refeição",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 8.dp),
            )
            if (alimentos.isEmpty()) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "Ainda não há itens. Toca em + para adicionar.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                ) {
                    items(alimentos, key = { it.id }) { item ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            ),
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = item.nome,
                                        style = MaterialTheme.typography.bodyLarge,
                                    )
                                    Text(
                                        text = item.formatoQuantidadeExibicao(),
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                }
                                IconButton(onClick = { viewModel.removerAlimento(item) }) {
                                    Icon(
                                        Icons.Filled.Delete,
                                        contentDescription = "Remover",
                                        tint = MaterialTheme.colorScheme.error,
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (mostrarDialogo) {
        val qParsed = textoQuantidade.trim().toIntOrNull()
        val quantidadeOk = qParsed != null && qParsed > 0
        val podeConfirmar = textoNovo.isNotBlank() && quantidadeOk

        AlertDialog(
            onDismissRequest = {
                mostrarDialogo = false
                textoNovo = ""
                textoQuantidade = ""
                unidadeMedida = DietaUnidadeMedida.GRAMAS
            },
            title = { Text("Novo alimento") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = textoNovo,
                        onValueChange = { textoNovo = it },
                        label = { Text("Nome (ex.: banana)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    OutlinedTextField(
                        value = textoQuantidade,
                        onValueChange = { novo ->
                            if (novo.isEmpty() || novo.all { it.isDigit() }) {
                                textoQuantidade = novo
                            }
                        },
                        label = {
                            Text(
                                if (unidadeMedida == DietaUnidadeMedida.GRAMAS) {
                                    "Quantidade (gramas)"
                                } else {
                                    "Quantidade (unidades)"
                                },
                            )
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Text(
                        text = "Medida",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(
                            selected = unidadeMedida == DietaUnidadeMedida.GRAMAS,
                            onClick = { unidadeMedida = DietaUnidadeMedida.GRAMAS },
                            label = { Text("Gramas") },
                        )
                        FilterChip(
                            selected = unidadeMedida == DietaUnidadeMedida.UNIDADE,
                            onClick = { unidadeMedida = DietaUnidadeMedida.UNIDADE },
                            label = { Text("Unidade") },
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val q = textoQuantidade.trim().toIntOrNull() ?: return@TextButton
                        viewModel.adicionarAlimento(
                            tipoRefeicaoId,
                            textoNovo,
                            q,
                            unidadeMedida,
                        )
                        textoNovo = ""
                        textoQuantidade = ""
                        unidadeMedida = DietaUnidadeMedida.GRAMAS
                        mostrarDialogo = false
                    },
                    enabled = podeConfirmar,
                ) {
                    Text("Adicionar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        mostrarDialogo = false
                        textoNovo = ""
                        textoQuantidade = ""
                        unidadeMedida = DietaUnidadeMedida.GRAMAS
                    },
                ) {
                    Text("Cancelar")
                }
            },
        )
    }
}
