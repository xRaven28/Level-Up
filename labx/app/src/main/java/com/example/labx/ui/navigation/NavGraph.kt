package com.example.labx.ui.navigation

import LoginUniversalScreen
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.labx.data.local.AppDatabase
import com.example.labx.data.local.PreferenciasManager
import com.example.labx.data.local.SesionUsuarioManager
import com.example.labx.data.repository.ProductoRepositoryImpl
import com.example.labx.data.repository.UsuarioRepository
import com.example.labx.domain.repository.RepositorioProductos
import com.example.labx.ui.screen.*
import com.example.labx.ui.viewmodel.*
import com.example.labx.domain.model.Producto

@Composable
fun NavGraph(
    navController: NavHostController,
    preferenciasManager: PreferenciasManager,
    carritoViewModel: CarritoViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val repositorioProductos: RepositorioProductos =
        ProductoRepositoryImpl(
            AppDatabase.getDatabase(context).productoDao()
        )

    val productoViewModel: ProductoViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return ProductoViewModel(repositorioProductos) as T
            }
        }
    )

    val usuarioViewModel: UsuarioViewModel = viewModel(
        factory = UsuarioViewModelFactory(
            repository = UsuarioRepository(
                AppDatabase.getDatabase(context).usuarioDao()
            ),
            sesionUsuarioManager = SesionUsuarioManager(context)
        )
    )

    NavHost(
        navController = navController,
        startDestination = Rutas.PORTADA,
        modifier = modifier
    ) {

        /* ---------- PORTADA ---------- */
        composable(Rutas.PORTADA) {

            val sesionManager = SesionUsuarioManager(context)
            val haySesionUsuario = sesionManager.obtenerUsuarioActivoId() != null
            val haySesionAdmin = preferenciasManager.estaAdminLogueado()

            PortadaScreen(
                onEntrarClick = {
                    navController.navigate(Rutas.HOME) {
                        popUpTo(Rutas.PORTADA) { inclusive = true }
                    }
                },
                onLoginClick = {
                    when {
                        haySesionAdmin -> navController.navigate(Rutas.PANEL_ADMIN)
                        haySesionUsuario -> navController.navigate(Rutas.MI_CUENTA)
                        else -> navController.navigate("login_universal")
                    }
                }
            )
        }

        /* ---------- LOGIN UNIVERSAL ---------- */
        composable("login_universal") {
            LoginUniversalScreen(
                usuarioViewModel = usuarioViewModel,
                preferenciasManager = preferenciasManager,

                onLoginUsuario = {
                    navController.navigate(Rutas.HOME) {
                        popUpTo("login_universal") { inclusive = true }
                    }
                },

                onLoginAdmin = {
                    navController.navigate(Rutas.PANEL_ADMIN) {
                        popUpTo("login_universal") { inclusive = true }
                    }
                },

                onVolverClick = { navController.popBackStack() },

                onIrRegistro = {
                    navController.navigate(Rutas.REGISTRO_USUARIO)
                }
            )
        }

        /* ---------- HOME ---------- */
        composable(Rutas.HOME) {
            HomeScreen(
                repositorioLocal = repositorioProductos,
                usuarioViewModel = usuarioViewModel,
                carritoViewModel = carritoViewModel,

                onProductoClick = { id ->
                    navController.navigate("${Rutas.DETALLE}/$id")
                },

                onCarritoClick = {
                    navController.navigate(Rutas.CARRITO)
                },

                onIrLogin = {
                    navController.navigate("login_universal")
                },

                onMiCuentaClick = {
                    navController.navigate(Rutas.MI_CUENTA)
                },

                onVolverPortada = {
                    navController.navigate(Rutas.PORTADA) {
                        popUpTo(Rutas.HOME) { inclusive = true }
                    }
                }
            )
        }

        /* ---------- DETALLE PRODUCTO ---------- */
        composable(
            route = "${Rutas.DETALLE}/{productoId}",
            arguments = listOf(navArgument("productoId") { type = NavType.IntType })
        ) { backStackEntry ->

            val productoId = backStackEntry.arguments?.getInt("productoId") ?: 0

            DetalleProductoScreen(
                productoId = productoId,
                productoRepository = repositorioProductos,
                carritoViewModel = carritoViewModel,
                onVolverClick = { navController.popBackStack() }
            )
        }

        /* ---------- CARRITO ---------- */
        composable(Rutas.CARRITO) {
            CarritoScreen(
                carritoViewModel = carritoViewModel,
                usuarioViewModel = usuarioViewModel,
                onVolverClick = { navController.popBackStack() },

                onProductoClick = { id ->
                    navController.navigate("${Rutas.DETALLE}/$id")
                },

                onIrACheckout = {
                    navController.navigate(Rutas.CHECKOUT)
                }
            )
        }

        /* ---------- CHECKOUT ---------- */
        composable(Rutas.CHECKOUT) {
            CheckoutScreen(
                carritoViewModel = carritoViewModel,
                usuarioViewModel = usuarioViewModel,
                onVolverClick = { navController.popBackStack() },
                onPagoExitoso = { idTx ->
                    navController.navigate("${Rutas.PAGO_EXITOSO}/$idTx") {
                        popUpTo(Rutas.HOME) { inclusive = false }
                    }
                }
            )
        }

        /* ---------- PAGO EXITOSO ---------- */
        composable(
            route = "${Rutas.PAGO_EXITOSO}/{idTransaccion}",
            arguments = listOf(navArgument("idTransaccion") { type = NavType.StringType })
        ) {
            val id = it.arguments?.getString("idTransaccion") ?: "N/A"

            PagoExitosoScreen(
                carritoViewModel = carritoViewModel,
                idTransaccion = id,
                onVolverInicio = {
                    navController.navigate(Rutas.HOME) {
                        popUpTo(Rutas.HOME) { inclusive = true }
                    }
                }
            )
        }

        /* ---------- REGISTRO ---------- */
        composable(Rutas.REGISTRO_USUARIO) {
            RegistroUsuarioScreen(
                usuarioViewModel = usuarioViewModel,
                onRegistroExitoso = {
                    navController.navigate(Rutas.HOME) {
                        popUpTo(Rutas.HOME) { inclusive = true }
                    }
                },
                onIrLogin = {
                    navController.navigate("login_universal")
                }
            )
        }

        /* ---------- MI CUENTA ---------- */
        composable(Rutas.MI_CUENTA) {
            MiCuentaScreen(
                usuarioViewModel = usuarioViewModel,
                preferenciasManager = preferenciasManager,
                onCerrarSesion = {
                    navController.navigate(Rutas.PORTADA) { popUpTo(0) }
                },
                onVolver = { navController.popBackStack() },
                onIrHome = {
                    navController.navigate(Rutas.HOME)
                }
            )
        }

        /* ---------- PANEL ADMIN ---------- */
        composable(Rutas.PANEL_ADMIN) {

            if (!preferenciasManager.estaAdminLogueado()) {
                LaunchedEffect(Unit) {
                    navController.navigate("login_universal") { popUpTo(0) }
                }
                return@composable
            }

            val productosState by productoViewModel.uiState.collectAsState()

            AdminPanelScreen(
                productos = productosState.productos,
                usernameAdmin = preferenciasManager.obtenerUsernameAdmin() ?: "Admin",

                // ✅ AGREGAR PRODUCTO (MISMA RUTA DEL FORMULARIO)
                onAgregarProducto = {
                    navController.navigate("formulario_producto?productoId=-1")
                },

                // ✅ EDITAR PRODUCTO (MISMA RUTA + ID)
                onEditarProducto = { producto ->
                    navController.navigate("formulario_producto?productoId=${producto.id}")
                },

                onEliminarProducto = { producto ->
                    productoViewModel.eliminarProducto(producto)
                },

                onCerrarSesion = {
                    preferenciasManager.cerrarSesionAdmin()
                    navController.navigate(Rutas.PORTADA) { popUpTo(0) }
                },

                onVolver = {
                    navController.popBackStack()
                },

                usuarioViewModel = usuarioViewModel,

                onVerProductosApi = {
                    navController.navigate(Rutas.PRODUCTOS_API)
                }
            )
        }

        /* ---------- FORMULARIO PRODUCTO (AGREGAR / EDITAR) ---------- */
        composable(
            route = "formulario_producto?productoId={productoId}",
            arguments = listOf(
                navArgument("productoId") {
                    type = NavType.IntType
                    defaultValue = -1
                }
            )
        ) { backStackEntry ->

            val productoId = backStackEntry.arguments?.getInt("productoId") ?: -1

            val productoExistenteState: State<Producto?> = produceState(initialValue = null, productoId) {
                value = if (productoId != -1) {
                    productoViewModel.obtenerProductoPorId(productoId)
                } else {
                    null
                }
            }

            val productoExistente = productoExistenteState.value

            FormularioProductoScreen(
                productoExistente = productoExistente,

                onGuardar = { producto ->
                    if (productoId == -1) {
                        productoViewModel.agregarProducto(producto)
                    } else {
                        productoViewModel.actualizarProducto(producto)
                    }
                    navController.popBackStack()
                },

                onCancelar = {
                    navController.popBackStack()
                }
            )
        }
    }
}