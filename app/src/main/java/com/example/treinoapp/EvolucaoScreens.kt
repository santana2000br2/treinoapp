package com.example.treinoapp

import android.graphics.Paint as AndroidPaint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.max

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaEvolucao(
    navController: NavController,
    viewModel: EvolucaoViewModel,
    modifier: Modifier = Modifier,
) {
    val pontos by viewModel.pesos.collectAsState(initial = emptyList())
    var mostrarDialogo by remember { mutableStateOf(false) }
    var mostrarSeletorData by remember { mutableStateOf(false) }
    var textoPeso by remember { mutableStateOf("") }
    var dataSelecionadaMillis by remember { mutableStateOf(System.currentTimeMillis()) }

    val fmtDataHora = remember {
        SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR"))
    }

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
            FloatingActionButton(
                onClick = {
                    textoPeso = ""
                    dataSelecionadaMillis = System.currentTimeMillis()
                    mostrarDialogo = true
                },
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Registar peso")
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
                text = "Acompanhamento de peso",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 4.dp),
            )
            Text(
                text = "Cada registo guarda a data em que informaste o peso. O gráfico mostra a tendência ao longo do tempo.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 16.dp),
            )

            PesoLineChart(pontos = pontos)

            Text(
                text = "Histórico",
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.padding(top = 20.dp, bottom = 8.dp),
            )

            if (pontos.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "Sem registos ainda.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(bottom = 88.dp),
                    modifier = Modifier.weight(1f),
                ) {
                    items(pontos.asReversed(), key = { it.id }) { item ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                            ),
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    text = fmtDataHora.format(Date(item.dataMillis)),
                                    style = MaterialTheme.typography.bodyMedium,
                                )
                                Text(
                                    text = formatarPesoKg(item.pesoKg),
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.primary,
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (mostrarDialogo) {
        val pesoOk = parsePesoKg(textoPeso) != null
        AlertDialog(
            onDismissRequest = {
                mostrarDialogo = false
                textoPeso = ""
            },
            title = { Text("Novo registo de peso") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = textoPeso,
                        onValueChange = { textoPeso = it },
                        label = { Text("Peso (kg)") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.fillMaxWidth(),
                    )
                    OutlinedButton(
                        onClick = { mostrarSeletorData = true },
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Icon(Icons.Filled.CalendarMonth, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "Data: ${fmtDataHora.format(Date(dataSelecionadaMillis))}",
                            style = MaterialTheme.typography.bodyLarge,
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val p = parsePesoKg(textoPeso) ?: return@TextButton
                        viewModel.adicionarPeso(p, dataSelecionadaMillis)
                        textoPeso = ""
                        mostrarDialogo = false
                    },
                    enabled = pesoOk,
                ) {
                    Text("Guardar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        mostrarDialogo = false
                        textoPeso = ""
                    },
                ) {
                    Text("Cancelar")
                }
            },
        )
    }

    if (mostrarSeletorData) {
        key(dataSelecionadaMillis) {
            val dateState = rememberDatePickerState(initialSelectedDateMillis = dataSelecionadaMillis)
            DatePickerDialog(
                onDismissRequest = { mostrarSeletorData = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            dateState.selectedDateMillis?.let { dataSelecionadaMillis = it }
                            mostrarSeletorData = false
                        },
                    ) {
                        Text("OK")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { mostrarSeletorData = false }) {
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
private fun PesoLineChart(
    pontos: List<PesoEvolucaoEntity>,
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
                text = "Regista o teu peso com + para veres o gráfico.",
                style = MaterialTheme.typography.bodyMedium,
                color = gridLabelColor,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(24.dp),
            )
        }
        return
    }

    val density = LocalDensity.current
    val padLeft = 48.dp
    val padRight = 12.dp
    val padTop = 10.dp
    val padBottom = 30.dp

    val minP = pontos.minOf { it.pesoKg }
    val maxP = pontos.maxOf { it.pesoKg }
    val minT = pontos.minOf { it.dataMillis }
    var maxT = pontos.maxOf { it.dataMillis }
    if (maxT <= minT) maxT = minT + 86_400_000L

    val (yLo, yHi) = niceYRangeKg(minP, maxP)
    val yTicks = buildYTicks(yLo, yHi)

    val fmtCurto = remember { SimpleDateFormat("d MMM", Locale("pt", "BR")) }

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

        fun xForTime(t: Long): Float =
            pl + (t - minT).toFloat() / (maxT - minT).toFloat() * chartW

        fun yForPeso(kg: Double): Float =
            pt + ((yHi - kg) / (yHi - yLo)).toFloat() * chartH

        val labelPaint = AndroidPaint().apply {
            isAntiAlias = true
            color = gridLabelColor.toArgb()
            textSize = with(density) { 11.sp.toPx() }
        }

        yTicks.forEach { yk ->
            val yPx = yForPeso(yk)
            drawLine(
                color = gridLabelColor.copy(alpha = 0.22f),
                start = Offset(pl, yPx),
                end = Offset(w - pr, yPx),
                strokeWidth = 1.dp.toPx(),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 8f), 0f),
            )
            val label = "${yk.toInt()} kg"
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
            val first = pontos.first()
            path.moveTo(xForTime(first.dataMillis), yForPeso(first.pesoKg))
            pontos.drop(1).forEach { p ->
                path.lineTo(xForTime(p.dataMillis), yForPeso(p.pesoKg))
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
        } else {
            val p = pontos.first()
            drawCircle(
                color = lineColor,
                radius = 6.dp.toPx(),
                center = Offset(xForTime(p.dataMillis), yForPeso(p.pesoKg)),
            )
        }

        val xTicks = 6
        for (i in 0..xTicks) {
            val t = minT + ((maxT - minT) * i) / xTicks
            val xPx = xForTime(t)
            val label = fmtCurto.format(Date(t))
            val tw = labelPaint.measureText(label)
            drawContext.canvas.nativeCanvas.drawText(
                label,
                (xPx - tw / 2f).coerceIn(pl, w - pr - tw),
                h - 6.dp.toPx(),
                labelPaint,
            )
        }
    }
}

private fun niceYRangeKg(minKg: Double, maxKg: Double): Pair<Double, Double> {
    val span = maxKg - minKg
    val pad = when {
        span < 1e-6 -> 2.0
        else -> max(2.0, span * 0.12)
    }
    val lo = floor((minKg - pad) / 5.0) * 5.0
    val hi = ceil((maxKg + pad) / 5.0) * 5.0
    return if (hi <= lo) Pair(lo, lo + 5.0) else Pair(lo, hi)
}

private fun buildYTicks(yLo: Double, yHi: Double): List<Double> {
    var step = 5.0
    val firstPass = mutableListOf<Double>()
    var v = yLo
    while (v <= yHi + 0.001) {
        firstPass.add(v)
        v += step
    }
    if (firstPass.size > 14) {
        step = 10.0
        val out = mutableListOf<Double>()
        v = floor(yLo / step) * step
        while (v <= yHi + 0.001) {
            out.add(v)
            v += step
        }
        return out
    }
    return firstPass
}

private fun parsePesoKg(text: String): Double? {
    val t = text.trim().replace(',', '.')
    val v = t.toDoubleOrNull() ?: return null
    if (v <= 0.0 || v > 500.0) return null
    return v
}

private fun formatarPesoKg(kg: Double): String {
    val v = if (kg == floor(kg)) kg.toInt().toString() else String.format(Locale("pt", "BR"), "%.1f", kg)
    return "$v kg"
}
