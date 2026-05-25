package com.example.treinoapp

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.key
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter

private data class SlotFotoUi(
    val pathExistente: String? = null,
    val uriNova: Uri? = null,
) {
    fun temFoto(): Boolean = pathExistente != null || uriNova != null
}

private fun uriDoSlot(context: android.content.Context, slot: SlotFotoUi?): Uri? {
    if (slot == null) return null
    slot.uriNova?.let { return it }
    slot.pathExistente?.let { return FotoEvolucaoImagemUtil.uriParaExibicao(context, it) }
    return null
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaFotosEvolucaoLista(
    navController: NavController,
    viewModel: FotoEvolucaoViewModel,
) {
    val context = LocalContext.current
    val sessoes by viewModel.sessoesComFotos.collectAsState(initial = emptyList())
    var sessaoParaExcluir by remember { mutableStateOf<FotoEvolucaoSessaoComFotos?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Fotos de Evolução") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                },
                actions = {
                    if (sessoes.size >= 2) {
                        IconButton(onClick = { navController.navigate("comparar") }) {
                            Icon(Icons.Filled.SwapHoriz, contentDescription = "Comparar")
                        }
                    }
                },
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate("cadastro") }) {
                Icon(Icons.Filled.Add, contentDescription = "Novo registo")
            }
        },
    ) { padding ->
        if (sessoes.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Icon(
                    Icons.Filled.PhotoCamera,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Registe um grupo de fotos por data",
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center,
                )
                Text(
                    text = "Frente, lados e costas. Depois compare dois períodos lado a lado.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 8.dp),
                )
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
                        text = "Cada registo é um conjunto de fotos numa data. Toque para editar.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                items(sessoes, key = { it.sessao.id }) { item ->
                    CardSessaoFotos(
                        item = item,
                        onAbrir = { navController.navigate("editar/${item.sessao.id}") },
                        onExcluir = { sessaoParaExcluir = item },
                    )
                }
            }
        }
    }

    sessaoParaExcluir?.let { alvo ->
        AlertDialog(
            onDismissRequest = { sessaoParaExcluir = null },
            title = { Text("Excluir registo?") },
            text = {
                Text(
                    "Remove todas as fotos de ${FotoEvolucaoCatalogo.formatarData(alvo.sessao.dataMillis)}.",
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.excluirSessao(alvo.sessao.id) {
                            sessaoParaExcluir = null
                            Toast.makeText(context, "Registo excluído", Toast.LENGTH_SHORT).show()
                        }
                    },
                ) {
                    Text("Excluir")
                }
            },
            dismissButton = {
                TextButton(onClick = { sessaoParaExcluir = null }) {
                    Text("Cancelar")
                }
            },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CardSessaoFotos(
    item: FotoEvolucaoSessaoComFotos,
    onAbrir: () -> Unit,
    onExcluir: () -> Unit,
) {
    val context = LocalContext.current
    Card(
        onClick = onAbrir,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = FotoEvolucaoCatalogo.formatarData(item.sessao.dataMillis),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
                Row {
                    IconButton(onClick = onAbrir) {
                        Icon(Icons.Filled.Edit, contentDescription = "Editar")
                    }
                    IconButton(onClick = onExcluir) {
                        Icon(Icons.Filled.Delete, contentDescription = "Excluir")
                    }
                }
            }
            Text(
                text = "${item.fotos.size} foto(s)",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                FotoEvolucaoCatalogo.posicoes.forEach { pos ->
                    val foto = item.fotoPorPosicao(pos.id)
                    val uri = foto?.let { FotoEvolucaoImagemUtil.uriParaExibicao(context, it.uriLocal) }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(0.75f)
                            .clip(RoundedCornerShape(8.dp))
                            .border(
                                1.dp,
                                MaterialTheme.colorScheme.outlineVariant,
                                RoundedCornerShape(8.dp),
                            ),
                        contentAlignment = Alignment.Center,
                    ) {
                        if (uri != null) {
                            Image(
                                painter = rememberAsyncImagePainter(uri),
                                contentDescription = pos.rotulo,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop,
                            )
                        } else {
                            Text(
                                text = "—",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
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
fun TelaFotosEvolucaoCadastro(
    navController: NavController,
    viewModel: FotoEvolucaoViewModel,
    sessaoId: Long,
) {
    val context = LocalContext.current
    var dataMillis by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var mostrarDatePicker by remember { mutableStateOf(false) }
    val slots = remember { mutableStateMapOf<String, SlotFotoUi>() }
    var posicaoParaEscolher by remember { mutableStateOf<String?>(null) }
    var carregado by remember { mutableStateOf(sessaoId == 0L) }

    LaunchedEffect(sessaoId) {
        if (sessaoId != 0L) {
            val dados = viewModel.carregarSessao(sessaoId)
            if (dados != null) {
                dataMillis = dados.sessao.dataMillis
                dados.fotos.forEach { foto ->
                    slots[foto.posicao] = SlotFotoUi(pathExistente = foto.uriLocal)
                }
            }
            carregado = true
        }
    }

    val picker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        val pos = posicaoParaEscolher
        if (uri != null && pos != null) {
            slots[pos] = SlotFotoUi(uriNova = uri)
        }
        posicaoParaEscolher = null
    }

    if (!carregado && sessaoId != 0L) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("A carregar…")
        }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (sessaoId == 0L) "Novo registo" else "Editar registo") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
        ) {
            Text(
                text = "Adicione fotos por posição (pode deixar algumas em branco).",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedButton(
                onClick = { mostrarDatePicker = true },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.CalendarMonth, contentDescription = null)
                    Spacer(modifier = Modifier.padding(horizontal = 8.dp))
                    Text("Data: ${FotoEvolucaoCatalogo.formatarData(dataMillis)}")
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            FotoEvolucaoCatalogo.posicoes.forEach { pos ->
                SlotFotoCadastro(
                    posicao = pos,
                    slot = slots[pos.id],
                    onAdicionar = {
                        posicaoParaEscolher = pos.id
                        picker.launch("image/*")
                    },
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
            Button(
                onClick = {
                    val inputs = FotoEvolucaoCatalogo.posicoes.mapNotNull { pos ->
                        val slot = slots[pos.id] ?: return@mapNotNull null
                        if (!slot.temFoto()) return@mapNotNull null
                        pos.id to FotoPosicaoInput(
                            pathExistente = slot.pathExistente,
                            uriNova = slot.uriNova,
                        )
                    }.toMap()
                    viewModel.salvarSessao(
                        sessaoId = sessaoId,
                        dataMillis = dataMillis,
                        fotosPorPosicao = inputs,
                        onSucesso = {
                            Toast.makeText(context, "Registo guardado", Toast.LENGTH_SHORT).show()
                            navController.popBackStack()
                        },
                        onSemFotos = {
                            Toast.makeText(
                                context,
                                "Adicione pelo menos uma foto",
                                Toast.LENGTH_SHORT,
                            ).show()
                        },
                        onErro = {
                            Toast.makeText(context, "Erro ao guardar", Toast.LENGTH_SHORT).show()
                        },
                    )
                },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Guardar registo")
            }
        }
    }

    if (mostrarDatePicker) {
        val state = rememberDatePickerState(initialSelectedDateMillis = dataMillis)
        DatePickerDialog(
            onDismissRequest = { mostrarDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        state.selectedDateMillis?.let { dataMillis = it }
                        mostrarDatePicker = false
                    },
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDatePicker = false }) {
                    Text("Cancelar")
                }
            },
        ) {
            DatePicker(state = state)
        }
    }
}

@Composable
private fun SlotFotoCadastro(
    posicao: PosicaoFotoEvolucao,
    slot: SlotFotoUi?,
    onAdicionar: () -> Unit,
) {
    val context = LocalContext.current
    val uri = uriDoSlot(context, slot)
    var ampliar by remember { mutableStateOf(false) }

    Column {
        Text(
            text = posicao.rotulo,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
        )
        Spacer(modifier = Modifier.height(6.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(0.75f)
                .clip(RoundedCornerShape(12.dp))
                .border(
                    1.dp,
                    MaterialTheme.colorScheme.outline,
                    RoundedCornerShape(12.dp),
                )
                .clickable(onClick = onAdicionar),
            contentAlignment = Alignment.Center,
        ) {
            if (uri != null) {
                Image(
                    painter = rememberAsyncImagePainter(uri),
                    contentDescription = posicao.rotulo,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                )
            } else {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(40.dp))
                    Text(
                        text = "Toque para adicionar",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 4.dp),
                    )
                }
            }
        }
        if (uri != null) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
            ) {
                TextButton(onClick = { ampliar = true }) {
                    Text("Ampliar")
                }
                TextButton(onClick = onAdicionar) {
                    Text("Trocar")
                }
            }
        }
    }

    if (ampliar && uri != null) {
        ImagemAmpliadaComZoomDialog(uri = uri, onDismiss = { ampliar = false })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaFotosEvolucaoComparar(
    navController: NavController,
    viewModel: FotoEvolucaoViewModel,
) {
    val context = LocalContext.current
    val sessoes by viewModel.sessoesComFotos.collectAsState(initial = emptyList())

    val idsIniciais = sessoes.take(2).map { it.sessao.id }
    var idsSelecionados by remember(sessoes.size) {
        mutableStateOf(idsIniciais)
    }

    val sessoesEscolhidas = sessoes
        .filter { it.sessao.id in idsSelecionados }
        .sortedBy { it.sessao.dataMillis }
    val sessaoAntes = sessoesEscolhidas.firstOrNull()
    val sessaoDepois = sessoesEscolhidas.getOrNull(1)
    val duasDatasSelecionadas = idsSelecionados.size == 2 &&
        sessaoAntes != null &&
        sessaoDepois != null &&
        sessaoAntes.sessao.id != sessaoDepois.sessao.id

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Antes e depois") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                },
            )
        },
    ) { padding ->
        if (sessoes.size < 2) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "Crie pelo menos dois registos em datas diferentes.",
                    modifier = Modifier.padding(24.dp),
                    textAlign = TextAlign.Center,
                )
            }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
                Text(
                    text = "Toque em 2 datas para comparar. A data mais antiga fica à esquerda (Antes).",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            item {
                SeletorDuasDatas(
                    sessoes = sessoes,
                    idsSelecionados = idsSelecionados,
                    onToggle = { id ->
                        idsSelecionados = when {
                            id in idsSelecionados -> idsSelecionados.filter { it != id }
                            idsSelecionados.size < 2 -> idsSelecionados + id
                            else -> listOf(idsSelecionados.first(), id)
                        }
                    },
                )
            }
            item {
                Text(
                    text = when (idsSelecionados.size) {
                        0 -> "Nenhuma data selecionada"
                        1 -> "Selecione mais 1 data"
                        else -> "2 datas selecionadas"
                    },
                    style = MaterialTheme.typography.labelMedium,
                    color = if (duasDatasSelecionadas) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    },
                )
            }
            item { HorizontalDivider() }

            when {
                !duasDatasSelecionadas -> {
                    if (idsSelecionados.size == 2) {
                        item {
                            Text(
                                text = "Escolha duas datas diferentes.",
                                color = MaterialTheme.colorScheme.error,
                            )
                        }
                    }
                }
                else -> {
                    val antes = sessaoAntes!!
                    val depois = sessaoDepois!!
                    val posicoesComparaveis = FotoEvolucaoCatalogo.posicoes.filter { pos ->
                        antes.fotoPorPosicao(pos.id) != null &&
                            depois.fotoPorPosicao(pos.id) != null
                    }
                    if (posicoesComparaveis.isEmpty()) {
                        item {
                            Text(
                                text = "Não há a mesma posição nos dois períodos. Ex.: se só tirou «Frente» numa data e «Costas» noutra, não dá para comparar lado a lado.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    } else {
                        items(posicoesComparaveis, key = { it.id }) { pos ->
                            val fotoAntes = antes.fotoPorPosicao(pos.id)!!
                            val fotoDepois = depois.fotoPorPosicao(pos.id)!!
                            ComparacaoPosicao(
                                posicao = pos,
                                dataAntes = FotoEvolucaoCatalogo.formatarData(antes.sessao.dataMillis),
                                dataDepois = FotoEvolucaoCatalogo.formatarData(depois.sessao.dataMillis),
                                uriAntes = FotoEvolucaoImagemUtil.uriParaExibicao(context, fotoAntes.uriLocal),
                                uriDepois = FotoEvolucaoImagemUtil.uriParaExibicao(context, fotoDepois.uriLocal),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SeletorDuasDatas(
    sessoes: List<FotoEvolucaoSessaoComFotos>,
    idsSelecionados: List<Long>,
    onToggle: (Long) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        sessoes.forEach { item ->
            val id = item.sessao.id
            FilterChip(
                selected = id in idsSelecionados,
                onClick = { onToggle(id) },
                label = {
                    Text(FotoEvolucaoCatalogo.formatarData(item.sessao.dataMillis))
                },
            )
        }
    }
}

@Composable
private fun ComparacaoPosicao(
    posicao: PosicaoFotoEvolucao,
    dataAntes: String,
    dataDepois: String,
    uriAntes: Uri?,
    uriDepois: Uri?,
) {
    var ampliarUri by remember { mutableStateOf<Uri?>(null) }

    Column {
        Text(
            text = posicao.rotulo,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            ColunaComparacao(
                rotulo = "Antes",
                data = dataAntes,
                uri = uriAntes,
                modifier = Modifier.weight(1f),
                onAmpliar = { uriAntes?.let { ampliarUri = it } },
            )
            ColunaComparacao(
                rotulo = "Depois",
                data = dataDepois,
                uri = uriDepois,
                modifier = Modifier.weight(1f),
                onAmpliar = { uriDepois?.let { ampliarUri = it } },
            )
        }
    }

    ampliarUri?.let { uri ->
        ImagemAmpliadaComZoomDialog(uri = uri, onDismiss = { ampliarUri = null })
    }
}

@Composable
private fun ColunaComparacao(
    rotulo: String,
    data: String,
    uri: Uri?,
    modifier: Modifier = Modifier,
    onAmpliar: () -> Unit,
) {
    Column(modifier = modifier) {
        Text(
            text = rotulo,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = data,
            style = MaterialTheme.typography.labelSmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(0.7f)
                .clickable(enabled = uri != null, onClick = onAmpliar),
            shape = RoundedCornerShape(10.dp),
            elevation = CardDefaults.cardElevation(2.dp),
        ) {
            if (uri != null) {
                Image(
                    painter = rememberAsyncImagePainter(uri),
                    contentDescription = "$rotulo $data",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                )
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "—",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}
