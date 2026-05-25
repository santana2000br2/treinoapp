package com.example.treinoapp

import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.BrightnessAuto
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CalendarViewMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.ViewAgenda
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.MonitorHeart
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material3.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import coil.compose.rememberAsyncImagePainter
import com.example.treinoapp.ui.theme.TreinoAppTheme
import kotlinx.coroutines.delay
import java.io.File
import java.io.FileOutputStream
import androidx.compose.foundation.background
import androidx.core.content.FileProvider

// ==================== Data Class original (usado nas telas) ====================
data class Treino(
    val nome: String,
    val descricao: String,
    val imagem1: Uri?,
    val imagem2: Uri?,
    val video: Uri?
)

// ==================== Activity principal ====================
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val dbInitializer = DatabaseInitializer(this)
        dbInitializer.populateIfNeeded()
        lifecycleScope.launch(Dispatchers.IO) {
            dbInitializer.garantirDadosTreino()
        }
        setContent {
            val context = LocalContext.current
            var themeMode by remember {
                mutableStateOf(ThemePreferences.getMode(context))
            }
            var homeVisualStyle by remember {
                mutableStateOf(ThemePreferences.getHomeVisualStyle(context))
            }
            val systemDark = isSystemInDarkTheme()
            val useDarkTheme = when (themeMode) {
                ThemeMode.SYSTEM -> systemDark
                ThemeMode.LIGHT -> false
                ThemeMode.DARK -> true
            }
            TreinoAppTheme(darkTheme = useDarkTheme) {
                AppNavigation(
                    themeMode = themeMode,
                    homeVisualStyle = homeVisualStyle,
                    onThemeModeChange = { newMode ->
                        ThemePreferences.setMode(context, newMode)
                        themeMode = newMode
                    },
                    onHomeVisualStyleChange = { newStyle ->
                        ThemePreferences.setHomeVisualStyle(context, newStyle)
                        homeVisualStyle = newStyle
                    },
                )
            }
        }
    }
}

// ==================== Navegação com ViewModel ====================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    homeVisualStyle: HomeVisualStyle = HomeVisualStyle.SIMPLE,
    onThemeModeChange: (ThemeMode) -> Unit = {},
    onHomeVisualStyleChange: (HomeVisualStyle) -> Unit = {},
) {
    val navController = rememberNavController()
    val context = LocalContext.current

    // Obtém o ViewModel usando a factory correta (necessário para receber o Application)
    val treinoViewModel: TreinoViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return TreinoViewModel(context.applicationContext as Application) as T
            }
        }
    )

    NavHost(
        navController = navController,
        startDestination = "splash"
    ) {
        // SPLASH
        composable("splash") {
            SplashScreen(navController)
        }

        // MENU INICIAL (Agenda, Dieta, Evolução; catálogo no ícone superior)
        composable("menu_inicial") {
            val systemDark = isSystemInDarkTheme()
            val darkTheme = when (themeMode) {
                ThemeMode.SYSTEM -> systemDark
                ThemeMode.LIGHT -> false
                ThemeMode.DARK -> true
            }
            TelaMenuInicial(
                navController = navController,
                themeMode = themeMode,
                homeVisualStyle = homeVisualStyle,
                darkTheme = darkTheme,
                onThemeModeChange = onThemeModeChange,
                onHomeVisualStyleChange = onHomeVisualStyleChange,
            )
        }

        composable("dieta") {
            TelaDietaMenu(navController = navController)
        }

        composable("dieta_refeicao/{tipo}") { backStackEntry ->
            val tipo = backStackEntry.arguments?.getString("tipo") ?: ""
            val dietaViewModel: DietaViewModel = viewModel(
                factory = object : ViewModelProvider.Factory {
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        @Suppress("UNCHECKED_CAST")
                        return DietaViewModel(context.applicationContext as Application) as T
                    }
                },
            )
            TelaDietaCadastroAlimentos(
                navController = navController,
                tipoRefeicaoId = tipo,
                viewModel = dietaViewModel,
            )
        }

        composable("evolucao") {
            val evolucaoViewModel = evolucaoViewModelLocal()
            TelaEvolucao(
                navController = navController,
                viewModel = evolucaoViewModel,
            )
        }

        composable("evolucao/cadastro") {
            val evolucaoViewModel = evolucaoViewModelLocal()
            TelaMedicaoCadastro(
                navController = navController,
                viewModel = evolucaoViewModel,
                medicaoId = 0L,
            )
        }

        composable(
            route = "evolucao/editar/{id}",
            arguments = listOf(navArgument("id") { type = NavType.LongType }),
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getLong("id") ?: 0L
            val evolucaoViewModel = evolucaoViewModelLocal()
            TelaMedicaoCadastro(
                navController = navController,
                viewModel = evolucaoViewModel,
                medicaoId = id,
            )
        }

        composable(
            route = "evolucao/grafico/{tipoId}",
            arguments = listOf(navArgument("tipoId") { type = NavType.StringType }),
        ) { backStackEntry ->
            val tipoId = backStackEntry.arguments?.getString("tipoId").orEmpty()
            val evolucaoViewModel = evolucaoViewModelLocal()
            val medicoes by evolucaoViewModel.medicoes.collectAsState(initial = emptyList())
            TelaMedicaoGrafico(
                navController = navController,
                tipoId = tipoId,
                medicoes = medicoes,
            )
        }

        navigation(
            startDestination = "lista",
            route = "fotos_evolucao",
        ) {
            composable("lista") {
                val fotoViewModel = fotoEvolucaoViewModelLocal()
                TelaFotosEvolucaoLista(
                    navController = navController,
                    viewModel = fotoViewModel,
                )
            }

            composable("cadastro") {
                val fotoViewModel = fotoEvolucaoViewModelLocal()
                TelaFotosEvolucaoCadastro(
                    navController = navController,
                    viewModel = fotoViewModel,
                    sessaoId = 0L,
                )
            }

            composable(
                route = "editar/{id}",
                arguments = listOf(navArgument("id") { type = NavType.LongType }),
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getLong("id") ?: 0L
                val fotoViewModel = fotoEvolucaoViewModelLocal()
                TelaFotosEvolucaoCadastro(
                    navController = navController,
                    viewModel = fotoViewModel,
                    sessaoId = id,
                )
            }

            composable("comparar") {
                val fotoViewModel = fotoEvolucaoViewModelLocal()
                TelaFotosEvolucaoComparar(
                    navController = navController,
                    viewModel = fotoViewModel,
                )
            }
        }

        navigation(
            startDestination = "lista",
            route = "treinos_prontos",
        ) {
            composable("lista") {
                val prontoViewModel = treinoProntoViewModelLocal()
                TelaTreinosProntosLista(
                    navController = navController,
                    viewModel = prontoViewModel,
                )
            }
            composable(
                route = "detalhe/{id}",
                arguments = listOf(navArgument("id") { type = NavType.LongType }),
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getLong("id") ?: 0L
                val prontoViewModel = treinoProntoViewModelLocal()
                TelaTreinosProntosDetalhe(
                    planoId = id,
                    navController = navController,
                    viewModel = prontoViewModel,
                )
            }
        }

        // LISTA DE DIAS
        composable("lista_dias") {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text("Treino da Semana 💪") },
                        navigationIcon = {
                            IconButton(onClick = { navController.popBackStack() }) {
                                Icon(Icons.Default.ArrowBack, contentDescription = "Voltar ao menu")
                            }
                        }
                    )
                }
            ) { padding ->
                TelaListaDias(
                    navController = navController,
                    viewModel = treinoViewModel,
                    modifier = Modifier.padding(padding),
                )
            }
        }

        // LISTA DE TREINOS DE UM DIA
        composable("lista_treinos/{dia}") { backStackEntry ->
            val dia = backStackEntry.arguments?.getString("dia") ?: ""
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text("Treinos de $dia") },
                        navigationIcon = {
                            IconButton(onClick = { navController.popBackStack() }) {
                                Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                            }
                        }
                    )
                }
            ) { padding ->
                TelaListaTreinos(
                    dia = dia,
                    navController = navController,
                    viewModel = treinoViewModel,
                    modifier = Modifier.padding(padding)
                )
            }
        }

        // DETALHE DE UM TREINO (do dia) - AGORA USA ID E COM BOTÃO DE EXCLUIR NA TOPAPPBAR
        composable("detalhe_treino/{dia}/{id}") { backStackEntry ->
            val dia = backStackEntry.arguments?.getString("dia") ?: ""
            val id = backStackEntry.arguments?.getString("id")?.toLongOrNull() ?: 0L

            var showDeleteDialog by remember { mutableStateOf(false) }
            val treinosDoDia by treinoViewModel.getTreinosPorDia(dia).collectAsState()
            val treinoComId = treinosDoDia.find { it.id == id }
            val treino = treinoComId?.treino

            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text("Detalhe do Treino") },
                        navigationIcon = {
                            IconButton(onClick = { navController.popBackStack() }) {
                                Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                            }
                        },
                        actions = {
                            if (treino != null) {
                                IconButton(onClick = { showDeleteDialog = true }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Excluir")
                                }
                            }
                        }
                    )
                }
            ) { paddingValues ->
                TelaDetalheTreino(
                    dia = dia,
                    id = id,
                    viewModel = treinoViewModel,
                    modifier = Modifier.padding(paddingValues)
                )
            }

            if (showDeleteDialog && treino != null) {
                AlertDialog(
                    onDismissRequest = { showDeleteDialog = false },
                    title = { Text("Excluir treino") },
                    text = { Text("Tem certeza que deseja excluir este treino?") },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                treino?.let {
                                    treinoViewModel.removerTreinoDoDia(it, dia, id)
                                }
                                navController.popBackStack()
                            }
                        ) {
                            Text("Excluir")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDeleteDialog = false }) {
                            Text("Cancelar")
                        }
                    }
                )
            }
        }

        // CADASTRO DE NOVO TREINO (direto no dia)
        composable("cadastro_treino/{dia}") { backStackEntry ->
            val dia = backStackEntry.arguments?.getString("dia") ?: ""
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text("Cadastrar treino - $dia") },
                        navigationIcon = {
                            IconButton(onClick = { navController.popBackStack() }) {
                                Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                            }
                        }
                    )
                }
            ) { padding ->
                TelaCadastroTreino(
                    dia = dia,
                    navController = navController,
                    viewModel = treinoViewModel,
                    modifier = Modifier.padding(padding)
                )
            }
        }

        // LISTA DE MODELOS (catálogo)
        composable("lista_modelos") {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text("Catálogo de Treinos") },
                        navigationIcon = {
                            IconButton(onClick = { navController.popBackStack() }) {
                                Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                            }
                        },
                        actions = {
                            IconButton(onClick = { navController.navigate("cadastro_modelo") }) {
                                Icon(Icons.Default.Edit, contentDescription = "Novo modelo")
                            }
                        }
                    )
                }
            ) { padding ->
                TelaListaModelos(
                    navController = navController,
                    viewModel = treinoViewModel,
                    modifier = Modifier.padding(padding)
                )
            }
        }

        // CADASTRO DE MODELO (criar/editar)
        composable("cadastro_modelo") {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text("Novo Modelo") },
                        navigationIcon = {
                            IconButton(onClick = { navController.popBackStack() }) {
                                Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                            }
                        }
                    )
                }
            ) { padding ->
                TelaCadastroModelo(
                    navController = navController,
                    viewModel = treinoViewModel,
                    modifier = Modifier.padding(padding)
                )
            }
        }
        composable("cadastro_modelo/{id}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id")?.toLongOrNull() ?: 0L
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text("Editar Modelo") },
                        navigationIcon = {
                            IconButton(onClick = { navController.popBackStack() }) {
                                Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                            }
                        }
                    )
                }
            ) { padding ->
                TelaCadastroModelo(
                    navController = navController,
                    viewModel = treinoViewModel,
                    modeloId = id,
                    modifier = Modifier.padding(padding)
                )
            }
        }

        // SELEÇÃO DE MODELOS PARA ADICIONAR A UM DIA
        composable("lista_modelos_selecao?dia={dia}") { backStackEntry ->
            val dia = backStackEntry.arguments?.getString("dia") ?: ""
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text("Selecionar Modelo") },
                        navigationIcon = {
                            IconButton(onClick = { navController.popBackStack() }) {
                                Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                            }
                        }
                    )
                }
            ) { padding ->
                TelaListaModelosSelecao(
                    dia = dia,
                    navController = navController,
                    viewModel = treinoViewModel,
                    modifier = Modifier.padding(padding)
                )
            }
        }
    }
}

@Composable
private fun evolucaoViewModelLocal(): EvolucaoViewModel {
    val context = LocalContext.current
    return viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return EvolucaoViewModel(context.applicationContext as Application) as T
            }
        },
    )
}

@Composable
private fun treinoProntoViewModelLocal(): TreinoProntoViewModel {
    val context = LocalContext.current
    return viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return TreinoProntoViewModel(context.applicationContext as Application) as T
            }
        },
    )
}

@Composable
private fun fotoEvolucaoViewModelLocal(): FotoEvolucaoViewModel {
    val context = LocalContext.current
    return viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return FotoEvolucaoViewModel(context.applicationContext as Application) as T
            }
        },
    )
}

// ==================== MENU INICIAL ====================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaMenuInicial(
    navController: NavController,
    themeMode: ThemeMode,
    homeVisualStyle: HomeVisualStyle,
    darkTheme: Boolean,
    onThemeModeChange: (ThemeMode) -> Unit,
    onHomeVisualStyleChange: (HomeVisualStyle) -> Unit,
) {
    val estiloIlustrado = homeVisualStyle == HomeVisualStyle.ILLUSTRATED
    val fundoHomeIlustradoClaro = estiloIlustrado && !darkTheme
    val fundoHomeIlustradoEscuro = estiloIlustrado && darkTheme
    var menuAparenciaExpandido by remember { mutableStateOf(false) }
    val hojeYmd = DiaCalendarioUtil.hojeYmd()
    val fraseMotivacional = remember(hojeYmd) { FrasesMotivacionais.fraseDoDia() }
    val corBarra = when {
        fundoHomeIlustradoClaro -> HomeIlustradoFundoClaro
        fundoHomeIlustradoEscuro -> HomeIlustradoFundoEscuro
        else -> MaterialTheme.colorScheme.surface
    }

    Scaffold(
        containerColor = when {
            fundoHomeIlustradoClaro -> HomeIlustradoFundoClaro
            fundoHomeIlustradoEscuro -> HomeIlustradoFundoEscuro
            else -> Color.Transparent
        },
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = corBarra,
                ),
                title = { TituloTreinoApp(estiloIlustrado = estiloIlustrado) },
                actions = {
                    IconButton(onClick = { navController.navigate("lista_modelos") }) {
                        Icon(
                            imageVector = Icons.Filled.MenuBook,
                            contentDescription = "Catálogo de treinos",
                        )
                    }
                    Box {
                        IconButton(onClick = { menuAparenciaExpandido = true }) {
                            Icon(
                                imageVector = Icons.Filled.Settings,
                                contentDescription = "Aparência",
                            )
                        }
                        DropdownMenu(
                            expanded = menuAparenciaExpandido,
                            onDismissRequest = { menuAparenciaExpandido = false }
                        ) {
                            Text(
                                text = "Aparência",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                            HorizontalDivider()
                            DropdownMenuItem(
                                text = { Text("Seguir o sistema") },
                                onClick = {
                                    onThemeModeChange(ThemeMode.SYSTEM)
                                    menuAparenciaExpandido = false
                                },
                                leadingIcon = {
                                    Icon(Icons.Filled.BrightnessAuto, contentDescription = null)
                                },
                                trailingIcon = {
                                    if (themeMode == ThemeMode.SYSTEM) {
                                        Icon(
                                            Icons.Filled.Check,
                                            contentDescription = "Selecionado",
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Claro") },
                                onClick = {
                                    onThemeModeChange(ThemeMode.LIGHT)
                                    menuAparenciaExpandido = false
                                },
                                leadingIcon = {
                                    Icon(Icons.Filled.LightMode, contentDescription = null)
                                },
                                trailingIcon = {
                                    if (themeMode == ThemeMode.LIGHT) {
                                        Icon(
                                            Icons.Filled.Check,
                                            contentDescription = "Selecionado",
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Noturno") },
                                onClick = {
                                    onThemeModeChange(ThemeMode.DARK)
                                    menuAparenciaExpandido = false
                                },
                                leadingIcon = {
                                    Icon(Icons.Filled.DarkMode, contentDescription = null)
                                },
                                trailingIcon = {
                                    if (themeMode == ThemeMode.DARK) {
                                        Icon(
                                            Icons.Filled.Check,
                                            contentDescription = "Selecionado",
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                            )
                            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                            Text(
                                text = "Estilo do menu",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            )
                            DropdownMenuItem(
                                text = { Text("Simples (sem imagens)") },
                                onClick = {
                                    onHomeVisualStyleChange(HomeVisualStyle.SIMPLE)
                                    menuAparenciaExpandido = false
                                },
                                leadingIcon = {
                                    Icon(Icons.Filled.ViewAgenda, contentDescription = null)
                                },
                                trailingIcon = {
                                    if (homeVisualStyle == HomeVisualStyle.SIMPLE) {
                                        Icon(
                                            Icons.Filled.Check,
                                            contentDescription = "Selecionado",
                                            tint = MaterialTheme.colorScheme.primary,
                                        )
                                    }
                                },
                            )
                            DropdownMenuItem(
                                text = { Text("Com ilustrações") },
                                onClick = {
                                    onHomeVisualStyleChange(HomeVisualStyle.ILLUSTRATED)
                                    menuAparenciaExpandido = false
                                },
                                leadingIcon = {
                                    Icon(Icons.Filled.Image, contentDescription = null)
                                },
                                trailingIcon = {
                                    if (homeVisualStyle == HomeVisualStyle.ILLUSTRATED) {
                                        Icon(
                                            Icons.Filled.Check,
                                            contentDescription = "Selecionado",
                                            tint = MaterialTheme.colorScheme.primary,
                                        )
                                    }
                                },
                            )
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        ConteudoMenuInicial(
            navController = navController,
            darkTheme = darkTheme,
            homeVisualStyle = homeVisualStyle,
            fraseMotivacional = fraseMotivacional,
            modifier = Modifier.padding(paddingValues),
        )
    }
}

// ==================== SPLASH ====================
@Composable
fun SplashScreen(navController: NavController) {
    LaunchedEffect(Unit) {
        delay(2000)
        navController.navigate("menu_inicial") {
            popUpTo("splash") { inclusive = true }
        }
    }
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo_treino),
            contentDescription = "Logo",
            modifier = Modifier.size(200.dp)
        )
    }
}

// ==================== LISTA DE DIAS ====================
@Composable
fun TelaListaDias(
    navController: NavController,
    viewModel: TreinoViewModel,
    modifier: Modifier = Modifier,
) {
    val diasDaSemana = listOf(
        "Segunda-feira", "Terça-feira", "Quarta-feira",
        "Quinta-feira", "Sexta-feira", "Sábado", "Domingo",
    )
    val sequencia by viewModel.sequenciaDiasTreino.collectAsState()

    LazyColumn(modifier = modifier) {
        items(count = 1, key = { _: Int -> "banner_sequencia" }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f),
                ),
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (sequencia == 0) {
                            "Sequência: ainda a começar — marca treinos no dia certo (≥50%)!"
                        } else {
                            "$sequencia dia(s) de treino seguidos! 🔥"
                        },
                        style = MaterialTheme.typography.titleMedium,
                    )
                    Text(
                        text = "O dia só conta para a sequência quando marcares pelo menos metade dos treinos listados nesse dia da semana.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 6.dp),
                    )
                }
            }
        }
        items(diasDaSemana, key = { it }) { dia ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                onClick = {
                    navController.navigate("lista_treinos/$dia")
                },
            ) {
                Text(
                    text = dia,
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.titleMedium,
                )
            }
        }
    }
}

// ==================== LISTA DE TREINOS DE UM DIA ====================
@Composable
fun TelaListaTreinos(
    dia: String,
    navController: NavController,
    viewModel: TreinoViewModel,
    modifier: Modifier = Modifier
) {
    // Observa os treinos do dia atual (agora vem como List<TreinoComId>)
    val treinosDoDia by viewModel.getTreinosPorDia(dia).collectAsState()
    val modelos by viewModel.modelos.collectAsState()
    val hojeYmd = remember { DiaCalendarioUtil.hojeYmd() }
    val concluidos by viewModel.observarIdsConcluidosNoDia(hojeYmd).collectAsState(initial = emptySet())
    val mesmaSemanaHoje = DiaCalendarioUtil.eMesmoDiaSemanaQueHoje(dia)

    Column(modifier = modifier.fillMaxSize()) {
        if (treinosDoDia.isEmpty()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text("Nenhum treino cadastrado para $dia")
            }
        } else {
            if (mesmaSemanaHoje) {
                val total = treinosDoDia.size
                val feitos = treinosDoDia.count { concluidos.contains(it.id) }
                val minimo = (total + 1) / 2
                val diaConta = feitos >= minimo
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (diaConta) {
                            MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.65f)
                        } else {
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                        },
                    ),
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "Hoje: $feitos de $total treinos marcados",
                            style = MaterialTheme.typography.titleSmall,
                        )
                        Text(
                            text = if (diaConta) {
                                "Meta cumprida — este dia conta para a sequência 🔥"
                            } else {
                                "Precisas de pelo menos $minimo para este dia contar (50%)."
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 4.dp),
                        )
                    }
                }
            } else {
                Text(
                    text = "Só podes marcar treinos em ${DiaCalendarioUtil.diaSemanaHojeAgenda()}. Aqui vês o plano de $dia.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                )
            }
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                itemsIndexed(treinosDoDia) { _, item ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 4.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable {
                                        navController.navigate("detalhe_treino/$dia/${item.id}")
                                    }
                                    .padding(12.dp),
                            ) {
                                Text(
                                    text = item.treino.nome,
                                    style = MaterialTheme.typography.titleMedium,
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = item.treino.descricao,
                                    style = MaterialTheme.typography.bodyMedium,
                                    maxLines = 2,
                                )
                            }
                            Checkbox(
                                checked = concluidos.contains(item.id),
                                onCheckedChange = { marcado ->
                                    viewModel.marcarConclusaoDiaHoje(dia, item.id, marcado)
                                },
                                enabled = mesmaSemanaHoje,
                            )
                        }
                    }
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { navController.navigate("cadastro_treino/$dia") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Criar Novo Treino")
            }

            if (modelos.isNotEmpty()) {
                Button(
                    onClick = {
                        navController.navigate("lista_modelos_selecao?dia=$dia")
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Adicionar do Catálogo")
                }
            } else {
                OutlinedButton(
                    onClick = { navController.navigate("lista_modelos") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Gerenciar Catálogo")
                }
            }
        }
    }
}

// ==================== DETALHE DE UM TREINO (do dia) ====================
@Composable
fun TelaDetalheTreino(
    dia: String,
    id: Long,
    viewModel: TreinoViewModel,
    modifier: Modifier = Modifier
) {
    val treinosDoDia by viewModel.getTreinosPorDia(dia).collectAsState()
    val treinoComId = treinosDoDia.find { it.id == id }
    val treino = treinoComId?.treino
    val hojeYmd = remember { DiaCalendarioUtil.hojeYmd() }
    val concluidos by viewModel.observarIdsConcluidosNoDia(hojeYmd).collectAsState(initial = emptySet())
    val podeMarcar = DiaCalendarioUtil.eMesmoDiaSemanaQueHoje(dia)

    var imagemAmpliada by remember { mutableStateOf<Uri?>(null) }

    if (treino == null) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Treino não encontrado")
        }
        return
    }
    Log.d("DEBUG_DETALHE", "treino.video = ${treino.video}")

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = treino.nome,
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(treino.descricao)
        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.45f),
            ),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Feito hoje",
                        style = MaterialTheme.typography.titleSmall,
                    )
                    Text(
                        text = "Conta para a meta de 50% do dia. Só podes marcar quando o calendário for $dia.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 4.dp),
                    )
                }
                Checkbox(
                    checked = concluidos.contains(id),
                    onCheckedChange = { feito ->
                        viewModel.marcarConclusaoDiaHoje(dia, id, feito)
                    },
                    enabled = podeMarcar,
                )
            }
        }
        if (!podeMarcar) {
            Text(
                text = "Para marcar como feito, volta aqui quando for $dia (hoje é ${DiaCalendarioUtil.diaSemanaHojeAgenda()}).",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 6.dp),
            )
        }
        Spacer(modifier = Modifier.height(16.dp))

        treino.imagem1?.let { uri ->
            Image(
                painter = rememberAsyncImagePainter(uri),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clickable { imagemAmpliada = uri }
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        treino.imagem2?.let { uri ->
            Image(
                painter = rememberAsyncImagePainter(uri),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clickable { imagemAmpliada = uri }
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        treino.video?.let { uri ->
            val modifierVideo =
                if (getYouTubeVideoId(uri) != null) {
                    Modifier.fillMaxWidth()
                } else {
                    Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                }
            VideoPlayer(uri = uri, modifier = modifierVideo)
            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    imagemAmpliada?.let { uri ->
        ImagemAmpliadaComZoomDialog(
            uri = uri,
            onDismiss = { imagemAmpliada = null },
        )
    }
}

// ==================== CADASTRO DE TREINO (direto no dia) ====================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaCadastroTreino(
    dia: String,
    navController: NavController,
    viewModel: TreinoViewModel,
    modifier: Modifier = Modifier
) {
    var nomeTreino by remember { mutableStateOf("") }
    var descricaoTreino by remember { mutableStateOf("") }
    var imagem1Uri by remember { mutableStateOf<Uri?>(null) }
    var imagem2Uri by remember { mutableStateOf<Uri?>(null) }
    var videoUri by remember { mutableStateOf<Uri?>(null) }

    var showYouTubeDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val scrollState = rememberScrollState()

    val imagePickerLauncher1 = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            imagem1Uri = salvarArquivoInterno(
                context,
                it,
                "imagem1_${System.currentTimeMillis()}.jpg"
            ) ?: run {
                Toast.makeText(context, "Erro ao salvar imagem 1", Toast.LENGTH_SHORT).show()
                null
            }
        }
    }

    val imagePickerLauncher2 = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            imagem2Uri = salvarArquivoInterno(
                context,
                it,
                "imagem2_${System.currentTimeMillis()}.jpg"
            ) ?: run {
                Toast.makeText(context, "Erro ao salvar imagem 2", Toast.LENGTH_SHORT).show()
                null
            }
        }
    }

    val videoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            videoUri = salvarArquivoInterno(
                context,
                it,
                "video_${System.currentTimeMillis()}.mp4"
            ) ?: run {
                Toast.makeText(context, "Erro ao salvar vídeo", Toast.LENGTH_SHORT).show()
                null
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        Text(
            text = "Cadastrar treino - $dia",
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = nomeTreino,
            onValueChange = { nomeTreino = it },
            label = { Text("Nome do Treino") },
            modifier = Modifier.fillMaxWidth(),
            isError = nomeTreino.isBlank()
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = descricaoTreino,
            onValueChange = { descricaoTreino = it },
            label = { Text("Descrição") },
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            isError = descricaoTreino.isBlank()
        )
        Spacer(modifier = Modifier.height(24.dp))

        Text("Imagem 1")
        Button(
            onClick = { imagePickerLauncher1.launch("image/*") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Selecionar Imagem 1")
        }
        imagem1Uri?.let { uri ->
            Spacer(modifier = Modifier.height(8.dp))
            Image(
                painter = rememberAsyncImagePainter(uri),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))

        Text("Imagem 2")
        Button(
            onClick = { imagePickerLauncher2.launch("image/*") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Selecionar Imagem 2")
        }
        imagem2Uri?.let { uri ->
            Spacer(modifier = Modifier.height(8.dp))
            Image(
                painter = rememberAsyncImagePainter(uri),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Vídeo com duas opções
        Text("Vídeo")
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { videoPickerLauncher.launch("video/*") },
                modifier = Modifier.weight(1f)
            ) {
                Text("Galeria")
            }
            Button(
                onClick = { showYouTubeDialog = true },
                modifier = Modifier.weight(1f)
            ) {
                Text("YouTube")
            }
        }

        if (showYouTubeDialog) {
            YouTubeLinkDialog(
                onDismiss = { showYouTubeDialog = false },
                onConfirm = { link ->
                    videoUri = Uri.parse(link)
                }
            )
        }

        videoUri?.let { uri ->
            Spacer(modifier = Modifier.height(8.dp))
            if (getYouTubeVideoId(uri) != null) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    VideoPlayer(
                        uri = uri,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = { videoUri = null }) {
                        Icon(Icons.Default.Delete, contentDescription = "Remover")
                    }
                }
            } else {
                // Vídeo local: exibe o player com botão de remover
                Box {
                    VideoPlayer(
                        uri = uri,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp)
                    )
                    IconButton(
                        onClick = { videoUri = null },
                        modifier = Modifier.align(Alignment.TopEnd)
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "Remover")
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                val novoTreino = Treino(
                    nome = nomeTreino,
                    descricao = descricaoTreino,
                    imagem1 = imagem1Uri,
                    imagem2 = imagem2Uri,
                    video = videoUri
                )
                viewModel.adicionarTreinoAoDia(dia, novoTreino)
                Toast.makeText(context, "Treino salvo!", Toast.LENGTH_SHORT).show()
                navController.popBackStack()
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = nomeTreino.isNotBlank() && descricaoTreino.isNotBlank()
        ) {
            Text("Salvar")
        }
    }
}

// ==================== LISTA DE MODELOS (catálogo) ====================
@Composable
fun TelaListaModelos(
    navController: NavController,
    viewModel: TreinoViewModel,
    modifier: Modifier = Modifier
) {
    val modelos by viewModel.modelosEntity.collectAsState()
    val context = LocalContext.current
    var consulta by remember { mutableStateOf("") }
    val modelosFiltrados = remember(modelos, consulta) {
        if (consulta.isBlank()) modelos
        else modelos.filter { it.nome.contains(consulta, ignoreCase = true) }
    }

    Column(modifier = modifier.fillMaxSize()) {
        if (modelos.isEmpty()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("Nenhum modelo cadastrado")
            }
        } else {
            OutlinedTextField(
                value = consulta,
                onValueChange = { consulta = it },
                label = { Text("Buscar por nome") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                singleLine = true
            )
            if (modelosFiltrados.isEmpty()) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Nenhum modelo encontrado para \"$consulta\".")
                }
            } else {
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            itemsIndexed(modelosFiltrados) { index, modelo ->
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = modelo.nome,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = modelo.descricao,
                                style = MaterialTheme.typography.bodyMedium,
                                maxLines = 1
                            )
                        }
                        Row {
                            IconButton(
                                onClick = {
                                    navController.navigate("cadastro_modelo/${modelo.id}")
                                }
                            ) {
                                Icon(Icons.Default.Edit, contentDescription = "Editar")
                            }
                            IconButton(
                                onClick = {
                                    val treino = Treino(
                                        nome = modelo.nome,
                                        descricao = modelo.descricao,
                                        imagem1 = modelo.imagem1?.let { Uri.parse(it) },
                                        imagem2 = modelo.imagem2?.let { Uri.parse(it) },
                                        video = modelo.video?.let { Uri.parse(it) }
                                    )
                                    viewModel.removerModelo(treino, modelo.id)
                                    Toast.makeText(context, "Modelo removido", Toast.LENGTH_SHORT).show()
                                }
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = "Excluir")
                            }
                        }
                    }
                }
            }
        }
            }
        }
    }
}

// ==================== CADASTRO DE MODELO ====================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaCadastroModelo(
    navController: NavController,
    viewModel: TreinoViewModel,
    modeloId: Long = 0L,
    modifier: Modifier = Modifier
) {
    val modelos by viewModel.modelosEntity.collectAsState()
    val modeloExistente = if (modeloId != 0L) {
        modelos.find { it.id == modeloId }
    } else null

    var nomeTreino by remember { mutableStateOf(modeloExistente?.nome ?: "") }
    var descricaoTreino by remember { mutableStateOf(modeloExistente?.descricao ?: "") }
    var imagem1Uri by remember { mutableStateOf(ExercicioImagemUtil.parseUri(modeloExistente?.imagem1)) }
    var imagem2Uri by remember { mutableStateOf(ExercicioImagemUtil.parseUri(modeloExistente?.imagem2)) }
    var videoUri by remember { mutableStateOf(ExercicioImagemUtil.parseUri(modeloExistente?.video)) }

    LaunchedEffect(modeloExistente?.id, modeloExistente?.imagem1, modeloExistente?.imagem2, modeloExistente?.video) {
        modeloExistente?.let { modelo ->
            if (modeloId != 0L) {
                nomeTreino = modelo.nome
                descricaoTreino = modelo.descricao
                imagem1Uri = ExercicioImagemUtil.parseUri(modelo.imagem1)
                imagem2Uri = ExercicioImagemUtil.parseUri(modelo.imagem2)
                videoUri = ExercicioImagemUtil.parseUri(modelo.video)
            }
        }
    }

    var showYouTubeDialog by remember { mutableStateOf(false) }

    val contexto = LocalContext.current
    val scrollState = rememberScrollState()

    val imagePickerLauncher1 = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            imagem1Uri = salvarArquivoInterno(
                contexto,
                it,
                "modelo_imagem1_${System.currentTimeMillis()}.jpg"
            ) ?: run {
                Toast.makeText(contexto, "Erro ao salvar imagem", Toast.LENGTH_SHORT).show()
                null
            }
        }
    }

    val imagePickerLauncher2 = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            imagem2Uri = salvarArquivoInterno(
                contexto,
                it,
                "modelo_imagem2_${System.currentTimeMillis()}.jpg"
            ) ?: run {
                Toast.makeText(contexto, "Erro ao salvar imagem", Toast.LENGTH_SHORT).show()
                null
            }
        }
    }

    val videoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            videoUri = salvarArquivoInterno(
                contexto,
                it,
                "modelo_video_${System.currentTimeMillis()}.mp4"
            ) ?: run {
                Toast.makeText(contexto, "Erro ao salvar vídeo", Toast.LENGTH_SHORT).show()
                null
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        Text(
            text = if (modeloId == 0L) "Novo Modelo" else "Editar Modelo",
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = nomeTreino,
            onValueChange = { nomeTreino = it },
            label = { Text("Nome do Treino") },
            modifier = Modifier.fillMaxWidth(),
            isError = nomeTreino.isBlank()
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = descricaoTreino,
            onValueChange = { descricaoTreino = it },
            label = { Text("Descrição") },
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            isError = descricaoTreino.isBlank()
        )
        Spacer(modifier = Modifier.height(24.dp))

        Text("Imagem 1")
        Button(
            onClick = { imagePickerLauncher1.launch("image/*") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Selecionar Imagem 1")
        }
        imagem1Uri?.let { uri ->
            Spacer(modifier = Modifier.height(8.dp))
            Image(
                painter = rememberAsyncImagePainter(uri),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))

        Text("Imagem 2")
        Button(
            onClick = { imagePickerLauncher2.launch("image/*") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Selecionar Imagem 2")
        }
        imagem2Uri?.let { uri ->
            Spacer(modifier = Modifier.height(8.dp))
            Image(
                painter = rememberAsyncImagePainter(uri),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Vídeo com duas opções
        Text("Vídeo")
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { videoPickerLauncher.launch("video/*") },
                modifier = Modifier.weight(1f)
            ) {
                Text("Galeria")
            }
            Button(
                onClick = { showYouTubeDialog = true },
                modifier = Modifier.weight(1f)
            ) {
                Text("YouTube")
            }
        }

        if (showYouTubeDialog) {
            YouTubeLinkDialog(
                onDismiss = { showYouTubeDialog = false },
                onConfirm = { link ->
                    videoUri = Uri.parse(link)
                }
            )
        }

        videoUri?.let { uri ->
            Spacer(modifier = Modifier.height(8.dp))
            if (getYouTubeVideoId(uri) != null) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    VideoPlayer(
                        uri = uri,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = { videoUri = null }) {
                        Icon(Icons.Default.Delete, contentDescription = "Remover")
                    }
                }
            } else {
                // Vídeo local: exibe o player com botão de remover
                Box {
                    VideoPlayer(
                        uri = uri,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp)
                    )
                    IconButton(
                        onClick = { videoUri = null },
                        modifier = Modifier.align(Alignment.TopEnd)
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "Remover")
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                val treino = Treino(
                    nome = nomeTreino,
                    descricao = descricaoTreino,
                    imagem1 = imagem1Uri,
                    imagem2 = imagem2Uri,
                    video = videoUri
                )
                if (modeloId == 0L) {
                    viewModel.inserirModelo(treino)
                    Toast.makeText(contexto, "Modelo salvo", Toast.LENGTH_SHORT).show()
                } else {
                    viewModel.atualizarModelo(treino, modeloId)
                    Toast.makeText(contexto, "Modelo atualizado", Toast.LENGTH_SHORT).show()
                }
                navController.popBackStack()
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = nomeTreino.isNotBlank() && descricaoTreino.isNotBlank()
        ) {
            Text("Salvar")
        }
    }
}

// ==================== SELEÇÃO DE MODELOS PARA ADICIONAR AO DIA ====================
@Composable
fun TelaListaModelosSelecao(
    dia: String,
    navController: NavController,
    viewModel: TreinoViewModel,
    modifier: Modifier = Modifier
) {
    val modelos by viewModel.modelos.collectAsState()
    val context = LocalContext.current
    var consulta by remember { mutableStateOf("") }
    val modelosFiltrados = remember(modelos, consulta) {
        if (consulta.isBlank()) modelos
        else modelos.filter { it.nome.contains(consulta, ignoreCase = true) }
    }

    Column(modifier = modifier.fillMaxSize()) {
        if (modelos.isEmpty()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("Nenhum modelo disponível. Crie um no catálogo.")
            }
        } else {
            OutlinedTextField(
                value = consulta,
                onValueChange = { consulta = it },
                label = { Text("Buscar por nome") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                singleLine = true
            )
            if (modelosFiltrados.isEmpty()) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Nenhum modelo encontrado para \"$consulta\".")
                }
            } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(modelosFiltrados) { index, modelo ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            viewModel.adicionarTreinoAoDia(dia, modelo)
                            Toast.makeText(context, "Treino adicionado a $dia", Toast.LENGTH_SHORT).show()
                            navController.popBackStack()
                        }
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = modelo.nome,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = modelo.descricao,
                                style = MaterialTheme.typography.bodyMedium,
                                maxLines = 2
                            )
                        }
                    }
                }
            }
            }
        }

        Button(
            onClick = { navController.navigate("lista_modelos") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text("Gerenciar Catálogo")
        }
    }
}

// ==================== FUNÇÃO AUXILIAR PARA SALVAR ARQUIVO ====================
fun salvarArquivoInterno(context: Context, uri: Uri, nomeArquivo: String): Uri? {
    return try {
        context.contentResolver.openInputStream(uri)?.use { inputStream ->
            val file = File(context.filesDir, nomeArquivo)
            FileOutputStream(file).use { outputStream ->
                inputStream.copyTo(outputStream)
            }
            // LOG AQUI
            Log.d("DEBUG", "Arquivo salvo em: ${file.absolutePath}, existe? ${file.exists()}")
            FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file,
            )
        }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

// ==================== FUNÇÃO PARA EXTRAIR ID DO YOUTUBE ====================
/** Aceita youtu.be/ID, youtu.be/ID?si=…, watch?v=, shorts/, embed/ */
private val youtubeIdFromText =
    Regex(
        "(?:youtube\\.com/(?:watch\\?v=|embed/|shorts/|v/)|youtu\\.be/)([A-Za-z0-9_-]{11})"
    )

fun getYouTubeVideoId(uri: Uri): String? {
    val raw = uri.toString().trim()
    if (raw.isEmpty()) return null

    youtubeIdFromText.find(raw)?.groupValues?.get(1)?.let { id ->
        Log.d("DEBUG_YOUTUBE", "id por regex no texto bruto: uri=$uri id=$id")
        return id
    }

    val normalized: Uri =
        if (!uri.scheme.isNullOrEmpty()) {
            uri
        } else if (raw.contains("youtube", ignoreCase = true) ||
            raw.contains("youtu.be", ignoreCase = true)
        ) {
            Uri.parse("https://$raw")
        } else {
            uri
        }

    val host = normalized.host?.lowercase() ?: run {
        return youtubeIdFromText.find(raw)?.groupValues?.get(1)
    }

    val fromPath = when {
        host.contains("youtube.com") -> {
            when {
                normalized.path?.contains("/shorts/", ignoreCase = true) == true ->
                    normalized.pathSegments.lastOrNull()
                normalized.path?.contains("/embed/", ignoreCase = true) == true ->
                    normalized.pathSegments.lastOrNull()
                else -> normalized.getQueryParameter("v")
            }
        }
        host == "youtu.be" || host.endsWith(".youtu.be") -> {
            val fromRegex = youtubeIdFromText.find(raw)?.groupValues?.get(1)
            if (fromRegex != null) {
                fromRegex
            } else {
                val noQuery = normalized.path?.substringBefore('?')?.trim('/') ?: ""
                when {
                    noQuery.length == 11 -> noQuery
                    else -> normalized.pathSegments.firstOrNull()
                }
            }
        }
        else -> null
    }

    val id = fromPath ?: youtubeIdFromText.find(raw)?.groupValues?.get(1)
    Log.d("DEBUG_YOUTUBE", "uri=$uri normalized=$normalized host=$host id=$id")
    return id?.takeIf { it.isNotBlank() }
}

// ==================== VIDEO PLAYER (ATUALIZADO) ====================
@Composable
fun VideoPlayer(uri: Uri?, modifier: Modifier = Modifier) {
    if (uri == null) return
    val context = LocalContext.current
    if (getYouTubeVideoId(uri) != null) {
        FilledTonalButton(
            onClick = {
                try {
                    context.startActivity(Intent(Intent.ACTION_VIEW, uri))
                } catch (e: Exception) {
                    Log.e("VideoPlayer", "Abrir YouTube", e)
                    Toast.makeText(
                        context,
                        "Não foi possível abrir o YouTube",
                        Toast.LENGTH_SHORT,
                    ).show()
                }
            },
            modifier = modifier,
        ) {
            Icon(Icons.Default.PlayArrow, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Ver vídeo no YouTube")
        }
    } else {
        VideoPlayerExo(uri = uri, modifier = modifier)
    }
}

// ==================== DIÁLOGO PARA INSERIR LINK DO YOUTUBE ====================
@Composable
fun YouTubeLinkDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var link by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Inserir link do YouTube") },
        text = {
            OutlinedTextField(
                value = link,
                onValueChange = { link = it },
                label = { Text("URL do vídeo") },
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (link.isNotBlank()) {
                        onConfirm(link)
                        onDismiss()
                    }
                }
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

// ==================== PREVIEW ====================
@Preview(showBackground = true)
@Composable
fun PreviewApp() {
    TreinoAppTheme {
        AppNavigation()
    }
}