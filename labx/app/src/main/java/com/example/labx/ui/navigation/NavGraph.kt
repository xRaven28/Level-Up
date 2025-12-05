package com.example.labx.ui.navigation

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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
import com.example.labx.ui.screen.*
import com.example.labx.ui.viewmodel.*

@Composable
fun NavGraph(
    navController: NavHostController,
    productoRepository: ProductoRepositoryImpl,
    preferenciasManager: PreferenciasManager,
    productoViewModel: ProductoViewModel,
    modifier: Modifier = Modifier
) {

    val context = LocalContext.current

    // ✅ ÚNICO CARRITO VIEWMODEL COMPARTIDO ENTRE TODAS LAS PANTALLAS
    val carritoViewModel: CarritoViewModel = viewModel()

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
            PortadaScreen(
                onEntrarClick = {
                    navController.navigate(Rutas.HOME) {
                        popUpTo(Rutas.PORTADA) { inclusive = true }
                    }
                },
                onLoginUsuarioClick = { navController.navigate(Rutas.LOGIN_USUARIO) },
                onLoginAdminClick = { navController.navigate(Rutas.LOGIN_ADMIN) }
            )
        }

        /* ---------- HOME ---------- */
        composable(Rutas.HOME) {
            HomeScreen(
                productoRepository = productoRepository,
                usuarioViewModel = usuarioViewModel,
                carritoViewModel = carritoViewModel,
                onProductoClick = { id ->
                    navController.navigate("${Rutas.DETALLE}/$id")
                },
                onCarritoClick = {
                    navController.navigate(Rutas.CARRITO)
                },
                onIrLogin = {
                    navController.navigate(Rutas.LOGIN_USUARIO)
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
        ) {
            val productoId = it.arguments?.getInt("productoId") ?: 0

            DetalleProductoScreen(
                productoId = productoId,
                productoRepository = productoRepository,
                carritoViewModel = carritoViewModel, // ✅ PASADO CORRECTAMENTE
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
                onIrLogin = { navController.navigate(Rutas.LOGIN_USUARIO) }
            )
        }

        /* ---------- LOGIN USUARIO ---------- */
        composable(Rutas.LOGIN_USUARIO) {
            LoginUsuarioScreen(
                usuarioViewModel = usuarioViewModel,
                onLoginExitoso = {
                    navController.navigate(Rutas.HOME) {
                        popUpTo(Rutas.LOGIN_USUARIO) { inclusive = true }
                    }
                },
                onVolverClick = { navController.popBackStack() },
                onIrRegistro = {
                    navController.navigate(Rutas.REGISTRO_USUARIO)
                }
            )
        }

        /* ---------- LOGIN ADMIN ---------- */
        composable(Rutas.LOGIN_ADMIN) {
            LoginAdminScreen(
                onLoginExitoso = {
                    navController.navigate(Rutas.PANEL_ADMIN) {
                        popUpTo(Rutas.LOGIN_ADMIN) { inclusive = true }
                    }
                },
                onVolverClick = { navController.popBackStack() },
                onValidarCredenciales = preferenciasManager::validarCredencialesAdmin,
                onGuardarSesion = preferenciasManager::guardarSesionAdmin
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
                onVolver = { navController.popBackStack() }
            )
        }

        /* ---------- PANEL ADMIN ---------- */
        composable(Rutas.PANEL_ADMIN) {

            if (!preferenciasManager.estaAdminLogueado()) {
                LaunchedEffect(Unit) {
                    navController.navigate(Rutas.LOGIN_ADMIN) { popUpTo(0) }
                }
                return@composable
            }

            val productos by productoViewModel.uiState.collectAsState()

            AdminPanelScreen(
                productos = productos.productos,
                usernameAdmin = preferenciasManager.obtenerUsernameAdmin() ?: "Admin",
                onAgregarProducto = {
                    navController.navigate("formulario_producto?productoId=-1")
                },
                onEditarProducto = { producto ->
                    navController.navigate(Rutas.formularioEditar(producto.id))
                },
                onEliminarProducto = { producto ->
                    productoViewModel.eliminarProducto(producto)
                },
                onCerrarSesion = {
                    preferenciasManager.cerrarSesionAdmin()
                    navController.navigate(Rutas.PORTADA) { popUpTo(0) }
                },
                usuarioViewModel = usuarioViewModel,
                onVerProductosApi = {
                    navController.navigate(Rutas.PRODUCTOS_API)
                }
            )
        }

        /* ---------- PRODUCTOS API ---------- */
        composable(Rutas.PRODUCTOS_API) {

            if (!preferenciasManager.estaAdminLogueado()) {
                LaunchedEffect(Unit) {
                    navController.navigate(Rutas.PORTADA) { popUpTo(0) }
                }
                return@composable
            }

            ProductosApiScreen(
                onVolver = {
                    navController.popBackStack()
                }
            )
        }
    }
}
