package com.example.treinoapp

import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CalendarViewMonth
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

internal val MenuBrandRed = Color(0xFFE53935)

/** PNGs do menu (1536×1024): largura ÷ altura. */
private const val MENU_CARD_ASPECT_RATIO = 1.5f

private const val FOOTER_BANNER_ASPECT_RATIO = 661f / 125f

private val SaudeMiniLargura = 100.dp

internal val HomeIlustradoFundoClaro = Color(0xFFF8F9FA)
internal val HomeIlustradoFundoEscuro = Color(0xFF0B1220)

private data class OpcaoMenuInicial(
    val id: String,
    val titulo: String,
    val subtitulo: String,
    val icone: ImageVector,
    val corDestaque: Color,
    @DrawableRes val imagemClara: Int,
    @DrawableRes val imagemEscura: Int? = null,
    val rota: String? = null,
)

private val opcoesMenuInicial = listOf(
    OpcaoMenuInicial(
        id = "agenda",
        titulo = "Agenda de treino",
        subtitulo = "Treinos por dia da semana",
        icone = Icons.Filled.CalendarMonth,
        corDestaque = Color(0xFFE53935),
        imagemClara = R.drawable.menu_agenda,
        imagemEscura = R.drawable.menu_agenda_escuro,
        rota = "lista_dias",
    ),
    OpcaoMenuInicial(
        id = "dieta",
        titulo = "Dieta",
        subtitulo = "Alimentação e planos",
        icone = Icons.Filled.Restaurant,
        corDestaque = Color(0xFF43A047),
        imagemClara = R.drawable.menu_dieta,
        imagemEscura = R.drawable.menu_dieta_escuro,
        rota = "dieta",
    ),
    OpcaoMenuInicial(
        id = "evolucao",
        titulo = "Evolução",
        subtitulo = "Medidas e gráficos",
        icone = Icons.Filled.ShowChart,
        corDestaque = Color(0xFF1E88E5),
        imagemClara = R.drawable.menu_evolucao,
        imagemEscura = R.drawable.menu_evolucao_escuro,
        rota = "evolucao",
    ),
    OpcaoMenuInicial(
        id = "fotos_evolucao",
        titulo = "Fotos de Evolução",
        subtitulo = "Registo visual do progresso",
        icone = Icons.Filled.PhotoLibrary,
        corDestaque = Color(0xFF8E24AA),
        imagemClara = R.drawable.menu_fotos,
        imagemEscura = R.drawable.menu_fotos_escuro,
        rota = "fotos_evolucao",
    ),
    OpcaoMenuInicial(
        id = "treinos_prontos",
        titulo = "Treinos Prontos",
        subtitulo = "Planos prontos — associar à agenda",
        icone = Icons.Filled.FitnessCenter,
        corDestaque = Color(0xFFFB8C00),
        imagemClara = R.drawable.menu_treinos_prontos,
        imagemEscura = R.drawable.menu_treinos_prontos_escuro,
        rota = "treinos_prontos",
    ),
    OpcaoMenuInicial(
        id = "frequencia",
        titulo = "Calendário de Frequência",
        subtitulo = "Sequência tipo GitHub",
        icone = Icons.Filled.CalendarViewMonth,
        corDestaque = Color(0xFF00897B),
        imagemClara = R.drawable.menu_frequencia,
        imagemEscura = R.drawable.menu_frequencia_escuro,
    ),
)

private data class OpcaoSaudeUi(
    val id: String,
    val titulo: String,
    val subtitulo: String,
    @DrawableRes val imagemClara: Int? = null,
    @DrawableRes val imagemEscura: Int? = null,
)

private val opcoesSaude = listOf(
    OpcaoSaudeUi(
        "agua",
        "Água",
        "Meta diária",
        imagemClara = R.drawable.saude_agua,
        imagemEscura = R.drawable.saude_agua_escuro,
    ),
    OpcaoSaudeUi(
        "sono",
        "Sono",
        "Qualidade",
        imagemEscura = R.drawable.saude_sono_escuro,
    ),
    OpcaoSaudeUi(
        "imc",
        "IMC",
        "Índice de Massa",
        imagemClara = R.drawable.saude_imc,
        imagemEscura = R.drawable.saude_imc_escuro,
    ),
    OpcaoSaudeUi(
        "bf",
        "BF%",
        "Gordura corporal",
        imagemClara = R.drawable.saude_bf,
        imagemEscura = R.drawable.saude_bf_escuro,
    ),
)

@DrawableRes
private fun imagemMenuParaTema(opcao: OpcaoMenuInicial, darkTheme: Boolean): Int {
    if (darkTheme && opcao.imagemEscura != null) return opcao.imagemEscura
    return opcao.imagemClara
}

@DrawableRes
private fun imagemSaudeParaTema(opcao: OpcaoSaudeUi, darkTheme: Boolean): Int? {
    if (darkTheme && opcao.imagemEscura != null) return opcao.imagemEscura
    return opcao.imagemClara
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CartaoMenuSimples(
    opcao: OpcaoMenuInicial,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val emBreve = opcao.rota == null
    ElevatedCard(
        onClick = onClick,
        modifier = modifier.aspectRatio(1f),
        shape = RoundedCornerShape(16.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Icon(
                imageVector = opcao.icone,
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = MaterialTheme.colorScheme.primary,
            )
            Column {
                Text(
                    text = opcao.titulo,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = opcao.subtitulo,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 4.dp),
                )
            }
            if (emBreve) {
                Text(
                    text = "Em breve",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.85f),
                )
            } else {
                Spacer(modifier = Modifier.height(14.dp))
            }
        }
    }
}

@Composable
private fun RodapeFraseSimples(frase: String) {
    Text(
        text = frase,
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.9f),
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, start = 4.dp, end = 4.dp),
    )
}

@Composable
private fun CartaoMenuIlustrado(
    opcao: OpcaoMenuInicial,
    darkTheme: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(14.dp)
    val sombra = if (darkTheme) 0.dp else 3.dp
    Image(
        painter = painterResource(imagemMenuParaTema(opcao, darkTheme)),
        contentDescription = opcao.titulo,
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(MENU_CARD_ASPECT_RATIO)
            .shadow(sombra, shape, clip = false)
            .clip(shape)
            .clickable(onClick = onClick),
        contentScale = ContentScale.FillBounds,
    )
}

@Composable
private fun CartaoSaudeMiniFallback(
    opcao: OpcaoSaudeUi,
    darkTheme: Boolean,
) {
    val fundo = if (darkTheme) Color(0xFF252B3D) else Color.White
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(fundo)
            .padding(horizontal = 8.dp, vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Filled.Bedtime,
                contentDescription = null,
                modifier = Modifier.size(56.dp),
                tint = Color(0xFF7E57C2),
            )
        }
        Text(
            text = opcao.titulo,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            text = opcao.subtitulo,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(top = 2.dp),
        )
    }
}

@Composable
private fun CartaoSaudeMini(
    opcao: OpcaoSaudeUi,
    darkTheme: Boolean,
    onClick: () -> Unit,
) {
    val shape = RoundedCornerShape(12.dp)
    val sombra = if (darkTheme) 0.dp else 2.dp

    val imagemRes = imagemSaudeParaTema(opcao, darkTheme)

    Box(
        modifier = Modifier
            .width(SaudeMiniLargura)
            .aspectRatio(1f)
            .shadow(sombra, shape, clip = false)
            .clip(shape)
            .clickable(onClick = onClick),
    ) {
        if (imagemRes != null) {
            Image(
                painter = painterResource(imagemRes),
                contentDescription = opcao.titulo,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.FillBounds,
            )
        } else {
            CartaoSaudeMiniFallback(opcao = opcao, darkTheme = darkTheme)
        }
    }
}

@Composable
private fun SecaoSaudePerformance(
    darkTheme: Boolean,
    onOpcaoClick: (OpcaoSaudeUi) -> Unit,
) {
    val fundoSecao = if (darkTheme) {
        Color(0xFF1E2A3D).copy(alpha = 0.75f)
    } else {
        Color(0xFFE3F2FD)
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = fundoSecao,
        shadowElevation = if (darkTheme) 0.dp else 1.dp,
    ) {
        Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp)) {
            Text(
                text = "Saúde & Performance",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = if (darkTheme) Color(0xFF90CAF9) else Color(0xFF1565C0),
            )
            Text(
                text = "Acompanhe seus principais indicadores diários",
                style = MaterialTheme.typography.bodySmall,
                color = if (darkTheme) {
                    MaterialTheme.colorScheme.onSurfaceVariant
                } else {
                    Color(0xFF546E7A)
                },
                modifier = Modifier.padding(top = 2.dp, bottom = 10.dp),
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    opcoesSaude.forEach { opcao ->
                        CartaoSaudeMini(
                            opcao = opcao,
                            darkTheme = darkTheme,
                            onClick = { onOpcaoClick(opcao) },
                        )
                    }
                }
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(start = 4.dp),
                )
            }
        }
    }
}

@Composable
fun TituloTreinoApp(estiloIlustrado: Boolean) {
    if (!estiloIlustrado) {
        Text(
            text = "TreinoApp",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
        )
        return
    }
    val usarImagem = false
    if (usarImagem) {
        Image(
            painter = painterResource(R.drawable.treinoapp),
            contentDescription = "TreinoApp",
            modifier = Modifier.height(28.dp),
            contentScale = ContentScale.Fit,
        )
    } else {
        Text(
            text = buildAnnotatedString {
                withStyle(SpanStyle(color = MaterialTheme.colorScheme.onSurface)) {
                    append("Treino")
                }
                withStyle(SpanStyle(color = MenuBrandRed, fontWeight = FontWeight.Bold)) {
                    append("App")
                }
            },
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
        )
    }
}

@DrawableRes
private fun footerMotivacaoParaTema(darkTheme: Boolean): Int {
    return if (darkTheme) R.drawable.footer_motivacao_escuro else R.drawable.footer_motivacao
}

@Composable
private fun RodapeMotivacional(darkTheme: Boolean) {
    Image(
        painter = painterResource(footerMotivacaoParaTema(darkTheme)),
        contentDescription = "Disciplina hoje, resultados sempre",
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
            .aspectRatio(FOOTER_BANNER_ASPECT_RATIO),
        contentScale = ContentScale.FillBounds,
    )
}

private fun navegarOpcaoMenu(
    context: android.content.Context,
    navController: NavController,
    opcao: OpcaoMenuInicial,
) {
    val rota = opcao.rota
    if (rota != null) {
        navController.navigate(rota)
    } else {
        Toast.makeText(
            context,
            "${opcao.titulo} — em breve",
            Toast.LENGTH_SHORT,
        ).show()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ConteudoMenuSimples(
    navController: NavController,
    fraseMotivacional: String,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val linhasMenu = remember { opcoesMenuInicial.chunked(2) }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Text(
                text = "Escolha uma opção",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp),
            )
        }
        linhasMenu.forEach { linha ->
            item(key = "simple_${linha.joinToString { it.id }}") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    linha.forEach { opcao ->
                        CartaoMenuSimples(
                            opcao = opcao,
                            onClick = { navegarOpcaoMenu(context, navController, opcao) },
                            modifier = Modifier.weight(1f),
                        )
                    }
                    if (linha.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
        item(key = "footer_simple") {
            RodapeFraseSimples(frase = fraseMotivacional)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ConteudoMenuIlustrado(
    navController: NavController,
    darkTheme: Boolean,
    fraseMotivacional: String,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val linhasMenu = remember { opcoesMenuInicial.chunked(2) }

    val fundo = if (darkTheme) HomeIlustradoFundoEscuro else HomeIlustradoFundoClaro

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(fundo),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        linhasMenu.forEach { linha ->
            item(key = "ill_${linha.joinToString { it.id }}") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    linha.forEach { opcao ->
                        CartaoMenuIlustrado(
                            opcao = opcao,
                            darkTheme = darkTheme,
                            onClick = { navegarOpcaoMenu(context, navController, opcao) },
                            modifier = Modifier.weight(1f),
                        )
                    }
                    if (linha.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
        item(key = "saude") {
            SecaoSaudePerformance(
                darkTheme = darkTheme,
                onOpcaoClick = { opcao ->
                    Toast.makeText(
                        context,
                        "${opcao.titulo} — em breve",
                        Toast.LENGTH_SHORT,
                    ).show()
                },
            )
        }
        item(key = "footer_ill") {
            RodapeMotivacional(darkTheme = darkTheme)
        }
    }
}

@Composable
fun ConteudoMenuInicial(
    navController: NavController,
    darkTheme: Boolean,
    homeVisualStyle: HomeVisualStyle,
    fraseMotivacional: String,
    modifier: Modifier = Modifier,
) {
    when (homeVisualStyle) {
        HomeVisualStyle.SIMPLE -> ConteudoMenuSimples(
            navController = navController,
            fraseMotivacional = fraseMotivacional,
            modifier = modifier,
        )
        HomeVisualStyle.ILLUSTRATED -> ConteudoMenuIlustrado(
            navController = navController,
            darkTheme = darkTheme,
            fraseMotivacional = fraseMotivacional,
            modifier = modifier,
        )
    }
}
