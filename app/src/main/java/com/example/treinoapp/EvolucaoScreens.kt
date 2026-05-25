package com.example.treinoapp

import android.graphics.Paint as AndroidPaint
import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import java.util.Date
import java.util.Locale
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.max

// ==================== LISTA (cards por mês) ====================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaEvolucao(
    navController: NavController,
    viewModel: EvolucaoViewModel,
    modifier: Modifier = Modifier,
) {
    val medicoes by viewModel.medicoes.collectAsState(initial = emptyList())

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Evolução") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                },
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate("evolucao/cadastro") }) {
                Icon(Icons.Filled.Add, contentDescription = "Novo registo mensal")
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
                text = "Medidas corporais",
                style = MaterialTheme.typography.titleMedium,
            )
            Text(
                text = "Regista uma vez por mês. Toque numa medida para ver o gráfico de evolução.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp, bottom = 16.dp),
            )

            if (medicoes.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "Sem registos ainda.\nUse + para o primeiro registo mensal.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 88.dp),
                    modifier = Modifier.weight(1f),
                ) {
                    itemsIndexed(medicoes, key = { _, m -> m.id }) { index, medicao ->
                        CardMedicaoMensal(
                            medicao = medicao,
                            expandidoInicial = index == 0,
                            onEditar = { navController.navigate("evolucao/editar/${medicao.id}") },
                            onExcluir = { viewModel.excluirMedicao(medicao.id) {} },
                            onVerGrafico = { tipo ->
                                navController.navigate("evolucao/grafico/${tipo.id}")
                            },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CardMedicaoMensal(
    medicao: MedicaoMensalEntity,
    expandidoInicial: Boolean,
    onEditar: () -> Unit,
    onExcluir: () -> Unit,
    onVerGrafico: (TipoMedicao) -> Unit,
) {
    var confirmarExclusao by remember { mutableStateOf(false) }
    var expandido by remember { mutableStateOf(expandidoInicial) }
    val preenchidas = remember(medicao) {
        MedicaoCatalogo.todos.count { tipo -> tipo.valorDe(medicao) != null }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
        ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .animateContentSize()
                .padding(16.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { expandido = !expandido },
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = if (expandido) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                        contentDescription = if (expandido) "Retrair" else "Expandir",
                        tint = MaterialTheme.colorScheme.primary,
                    )
                    Column(modifier = Modifier.padding(start = 8.dp)) {
                        Text(
                            text = MedicaoCatalogo.formatarMesReferencia(medicao.mesReferencia),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                        )
                        Text(
                            text = "Data: ${MedicaoCatalogo.formatarData(medicao.dataMillis)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        if (!expandido) {
                            Text(
                                text = if (preenchidas == 0) {
                                    "Nenhuma medida · toque para expandir"
                                } else {
                                    "$preenchidas medida(s) · toque para expandir"
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(top = 4.dp),
                            )
                        }
                    }
                }
                IconButton(onClick = onEditar) {
                    Icon(Icons.Filled.Edit, contentDescription = "Editar")
                }
                IconButton(onClick = { confirmarExclusao = true }) {
                    Icon(Icons.Filled.Delete, contentDescription = "Excluir")
                }
            }

            if (expandido) {
                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                MedicaoCatalogo.todos.forEach { tipo ->
                    val valor = tipo.valorDe(medicao)
                    if (valor != null) {
                        LinhaMedicaoCard(
                            tipo = tipo,
                            valor = valor,
                            onVerGrafico = { onVerGrafico(tipo) },
                        )
                    }
                }

                if (preenchidas == 0) {
                    Text(
                        text = "Nenhuma medida preenchida neste registo.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }

    if (confirmarExclusao) {
        AlertDialog(
            onDismissRequest = { confirmarExclusao = false },
            title = { Text("Excluir registo?") },
            text = { Text("Este registo mensal será removido permanentemente.") },
            confirmButton = {
                TextButton(onClick = {
                    confirmarExclusao = false
                    onExcluir()
                }) {
                    Text("Excluir")
                }
            },
            dismissButton = {
                TextButton(onClick = { confirmarExclusao = false }) {
                    Text("Cancelar")
                }
            },
        )
    }
}

@Composable
private fun LinhaMedicaoCard(
    tipo: TipoMedicao,
    valor: Double,
    onVerGrafico: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onVerGrafico)
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f),
        ) {
            Icon(
                Icons.Filled.ShowChart,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(end = 8.dp),
            )
            Text(text = tipo.rotulo, style = MaterialTheme.typography.bodyMedium)
        }
        Text(
            text = MedicaoCatalogo.formatarValor(tipo, valor),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.primary,
        )
    }
}

// ==================== CADASTRO MENSAL ====================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaMedicaoCadastro(
    navController: NavController,
    viewModel: EvolucaoViewModel,
    medicaoId: Long,
    modifier: Modifier = Modifier,
) {
    val contexto = LocalContext.current
    val scroll = rememberScrollState()

    var carregado by remember { mutableStateOf(medicaoId == 0L) }
    var dataMillis by remember { mutableStateOf(System.currentTimeMillis()) }
    var campos by remember { mutableStateOf(camposMedicaoVazios()) }
    var mostrarData by remember { mutableStateOf(false) }
    var instrucaoTipo by remember { mutableStateOf<TipoMedicao?>(null) }

    LaunchedEffect(medicaoId) {
        if (medicaoId != 0L) {
            viewModel.obterPorId(medicaoId)?.let { m ->
                dataMillis = m.dataMillis
                campos = camposDeEntidade(m)
            }
        }
        carregado = true
    }

    if (!carregado) {
        Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("A carregar…")
        }
        return
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(if (medicaoId == 0L) "Novo registo mensal" else "Editar registo") },
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
                .verticalScroll(scroll)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = "Preencha as medidas do mês. Campos vazios são ignorados. Pelo menos uma medida é obrigatória.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            OutlinedButton(
                onClick = { mostrarData = true },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Icon(Icons.Filled.CalendarMonth, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Referência: ${MedicaoCatalogo.formatarData(dataMillis)}")
            }

            MedicaoCatalogo.todos.forEach { tipo ->
                CampoMedicao(
                    tipo = tipo,
                    valor = campos[tipo.id].orEmpty(),
                    onValorChange = { campos = campos + (tipo.id to it) },
                    onInfo = { instrucaoTipo = tipo },
                )
            }

            OutlinedButton(
                onClick = {
                    viewModel.salvarMedicao(
                        id = medicaoId,
                        dataMillis = dataMillis,
                        pesoKg = parseDecimal(campos["peso"]),
                        alturaCm = parseDecimal(campos["altura"]),
                        cinturaCm = parseDecimal(campos["cintura"]),
                        abdomenCm = parseDecimal(campos["abdomen"]),
                        peitoCm = parseDecimal(campos["peito"]),
                        quadrilCm = parseDecimal(campos["quadril"]),
                        bracoEsquerdoCm = parseDecimal(campos["braco_esquerdo"]),
                        bracoDireitoCm = parseDecimal(campos["braco_direito"]),
                        coxaEsquerdaCm = parseDecimal(campos["coxa_esquerda"]),
                        coxaDireitaCm = parseDecimal(campos["coxa_direita"]),
                        panturrilhaEsquerdaCm = parseDecimal(campos["panturrilha_esquerda"]),
                        panturrilhaDireitaCm = parseDecimal(campos["panturrilha_direita"]),
                        onSucesso = { navController.popBackStack() },
                        onMesDuplicado = {
                            Toast.makeText(
                                contexto,
                                "Já existe registo para este mês. Edite o registo existente.",
                                Toast.LENGTH_LONG,
                            ).show()
                        },
                        onSemDados = {
                            Toast.makeText(
                                contexto,
                                "Informe pelo menos uma medida.",
                                Toast.LENGTH_SHORT,
                            ).show()
                        },
                    )
                },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Guardar registo mensal")
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    instrucaoTipo?.let { tipo ->
        AlertDialog(
            onDismissRequest = { instrucaoTipo = null },
            title = { Text("Como medir: ${tipo.rotulo}") },
            text = { Text(tipo.instrucao) },
            confirmButton = {
                TextButton(onClick = { instrucaoTipo = null }) {
                    Text("OK")
                }
            },
        )
    }

    if (mostrarData) {
        key(dataMillis) {
            val dateState = rememberDatePickerState(initialSelectedDateMillis = dataMillis)
            DatePickerDialog(
                onDismissRequest = { mostrarData = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            dateState.selectedDateMillis?.let { dataMillis = it }
                            mostrarData = false
                        },
                    ) {
                        Text("OK")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { mostrarData = false }) {
                        Text("Cancelar")
                    }
                },
            ) {
                DatePicker(state = dateState)
            }
        }
    }
}

@Composable
private fun CampoMedicao(
    tipo: TipoMedicao,
    valor: String,
    onValorChange: (String) -> Unit,
    onInfo: () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth(),
    ) {
        OutlinedTextField(
            value = valor,
            onValueChange = onValorChange,
            label = { Text("${tipo.rotulo} (${tipo.unidade})") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.weight(1f),
        )
        IconButton(onClick = onInfo) {
            Icon(Icons.Filled.Info, contentDescription = "Como medir")
        }
    }
}

// ==================== GRÁFICO POR INDICADOR ====================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaMedicaoGrafico(
    navController: NavController,
    tipoId: String,
    medicoes: List<MedicaoMensalEntity>,
    modifier: Modifier = Modifier,
) {
    val tipo = MedicaoCatalogo.tipoPorId(tipoId)

    if (tipo == null) {
        Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Medida não encontrada")
        }
        return
    }

    val pontos = medicoes
        .mapNotNull { m ->
            val v = tipo.valorDe(m)
            if (v != null) m to v else null
        }
        .sortedBy { it.first.mesReferencia }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Gráfico — ${tipo.rotulo}") },
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
                .padding(horizontal = 16.dp),
        ) {
            Text(
                text = "Evolução de ${tipo.rotulo} (${tipo.unidade}) ao longo dos meses.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 16.dp),
            )

            MedicaoLineChart(
                pontos = pontos,
                unidade = tipo.unidade,
                rotulo = tipo.rotulo,
            )

            if (pontos.size >= 2) {
                val primeiro = pontos.first().second
                val ultimo = pontos.last().second
                val diff = ultimo - primeiro
                val sinal = when {
                    diff > 0.05 -> "+"
                    diff < -0.05 -> ""
                    else -> "±"
                }
                Text(
                    text = "Variação total: $sinal${String.format(Locale("pt", "BR"), "%.1f", diff)} ${tipo.unidade}",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 16.dp),
                )
            }
        }
    }
}

@Composable
private fun MedicaoLineChart(
    pontos: List<Pair<MedicaoMensalEntity, Double>>,
    unidade: String,
    rotulo: String,
    modifier: Modifier = Modifier,
) {
    val gridLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
    val lineColor = Color(0xFF1976D2)
    val surface = MaterialTheme.colorScheme.surface

    if (pontos.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(260.dp)
                .background(
                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                    RoundedCornerShape(12.dp),
                ),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "Regista $rotulo em pelo menos um mês para ver o gráfico.",
                style = MaterialTheme.typography.bodyMedium,
                color = gridLabelColor,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(24.dp),
            )
        }
        return
    }

    val density = LocalDensity.current
    val padLeft = 52.dp
    val padRight = 12.dp
    val padTop = 10.dp
    val padBottom = 36.dp

    val valores = pontos.map { it.second }
    val minV = valores.min()
    val maxV = valores.max()
    val indices = pontos.indices.toList()
    val minI = 0
    var maxI = indices.max()
    if (maxI <= minI) maxI = minI + 1

    val (yLo, yHi) = niceYRange(minV, maxV)
    val yTicks = buildYTicks(yLo, yHi)

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(300.dp)
            .background(surface, RoundedCornerShape(12.dp)),
    ) {
        val w = size.width
        val h = size.height
        val pl = padLeft.toPx()
        val pr = padRight.toPx()
        val pt = padTop.toPx()
        val pb = padBottom.toPx()
        val chartW = w - pl - pr
        val chartH = h - pt - pb

        fun xForIndex(i: Int): Float =
            pl + (i - minI).toFloat() / (maxI - minI).toFloat() * chartW

        fun yForValor(v: Double): Float =
            pt + ((yHi - v) / (yHi - yLo)).toFloat() * chartH

        val labelPaint = AndroidPaint().apply {
            isAntiAlias = true
            color = gridLabelColor.toArgb()
            textSize = with(density) { 11.sp.toPx() }
        }

        yTicks.forEach { yk ->
            val yPx = yForValor(yk)
            drawLine(
                color = gridLabelColor.copy(alpha = 0.22f),
                start = Offset(pl, yPx),
                end = Offset(w - pr, yPx),
                strokeWidth = 1.dp.toPx(),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 8f), 0f),
            )
            val label = if (unidade == "kg") {
                "${yk.toInt()} kg"
            } else {
                "${yk.toInt()} $unidade"
            }
            val tw = labelPaint.measureText(label)
            drawContext.canvas.nativeCanvas.drawText(
                label,
                pl - 6.dp.toPx() - tw,
                yPx + 4.dp.toPx(),
                labelPaint,
            )
        }

        if (pontos.size >= 2) {
            val path = Path()
            path.moveTo(xForIndex(0), yForValor(pontos[0].second))
            pontos.drop(1).forEachIndexed { idx, p ->
                path.lineTo(xForIndex(idx + 1), yForValor(p.second))
            }
            drawPath(
                path = path,
                color = lineColor,
                style = Stroke(
                    width = 3.dp.toPx(),
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round,
                ),
            )
        }
        pontos.forEachIndexed { idx, p ->
            drawCircle(
                color = lineColor,
                radius = 6.dp.toPx(),
                center = Offset(xForIndex(idx), yForValor(p.second)),
            )
        }

        pontos.forEachIndexed { idx, p ->
            val label = rotuloMesCurto(p.first.mesReferencia)
            val tw = labelPaint.measureText(label)
            drawContext.canvas.nativeCanvas.drawText(
                label,
                (xForIndex(idx) - tw / 2f).coerceIn(pl, w - pr - tw),
                h - 8.dp.toPx(),
                labelPaint,
            )
        }
    }
}

// ==================== Helpers ====================

private fun camposMedicaoVazios(): Map<String, String> =
    MedicaoCatalogo.todos.associate { it.id to "" }

private fun camposDeEntidade(m: MedicaoMensalEntity): Map<String, String> {
    fun fmt(v: Double?) = v?.let {
        if (it == floor(it)) it.toInt().toString()
        else String.format(Locale("pt", "BR"), "%.1f", it)
    }.orEmpty()

    return mapOf(
        "peso" to fmt(m.pesoKg),
        "altura" to fmt(m.alturaCm),
        "cintura" to fmt(m.cinturaCm),
        "abdomen" to fmt(m.abdomenCm),
        "peito" to fmt(m.peitoCm),
        "quadril" to fmt(m.quadrilCm),
        "braco_esquerdo" to fmt(m.bracoEsquerdoCm),
        "braco_direito" to fmt(m.bracoDireitoCm),
        "coxa_esquerda" to fmt(m.coxaEsquerdaCm),
        "coxa_direita" to fmt(m.coxaDireitaCm),
        "panturrilha_esquerda" to fmt(m.panturrilhaEsquerdaCm),
        "panturrilha_direita" to fmt(m.panturrilhaDireitaCm),
    )
}

private fun parseDecimal(text: String?): Double? {
    val t = text?.trim().orEmpty()
    if (t.isEmpty()) return null
    val v = t.replace(',', '.').toDoubleOrNull() ?: return null
    if (v <= 0.0 || v > 500.0) return null
    return v
}

private fun niceYRange(minV: Double, maxV: Double): Pair<Double, Double> {
    val span = maxV - minV
    val pad = when {
        span < 1e-6 -> if (maxV > 10) 2.0 else 1.0
        else -> max(if (maxV > 50) 2.0 else 1.0, span * 0.15)
    }
    val lo = floor((minV - pad) / 1.0) * 1.0
    val hi = ceil((maxV + pad) / 1.0) * 1.0
    return if (hi <= lo) Pair(lo, lo + 1.0) else Pair(lo, hi)
}

private fun rotuloMesCurto(mesReferencia: String): String {
    val partes = mesReferencia.split("-")
    if (partes.size != 2) return mesReferencia
    return "${partes[1]}/${partes[0].takeLast(2)}"
}

private fun buildYTicks(yLo: Double, yHi: Double): List<Double> {
    val span = yHi - yLo
    val step = when {
        span <= 5 -> 1.0
        span <= 20 -> 2.0
        span <= 50 -> 5.0
        else -> 10.0
    }
    val out = mutableListOf<Double>()
    var v = floor(yLo / step) * step
    while (v <= yHi + 0.001) {
        out.add(v)
        v += step
    }
    return out
}
