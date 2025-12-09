import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.labx.data.local.PreferenciasManager
import com.example.labx.ui.viewmodel.UsuarioViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginUniversalScreen(
    usuarioViewModel: UsuarioViewModel,
    preferenciasManager: PreferenciasManager,
    onLoginUsuario: () -> Unit,
    onLoginAdmin: () -> Unit,
    onVolverClick: () -> Unit,
    onIrRegistro: () -> Unit
) {
    val authState by usuarioViewModel.authState.collectAsState()

    var input by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var mensajeError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(authState.usuarioActual) {
        if (authState.usuarioActual != null && !authState.estaCargando) {
            onLoginUsuario()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ingreso") },
                navigationIcon = {
                    IconButton(onClick = onVolverClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text("Level-Up Store", fontSize = 22.sp, fontWeight = FontWeight.Bold)

            OutlinedTextField(
                value = input,
                onValueChange = {
                    input = it
                    mensajeError = null
                },
                label = { Text("Email o Usuario Admin") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    mensajeError = null
                },
                label = { Text("Contraseña (solo admin)") },
                modifier = Modifier.fillMaxWidth()
            )

            if (mensajeError != null) {
                Text(mensajeError!!, color = MaterialTheme.colorScheme.error)
            }

            Button(
                onClick = {

                    // ✅ INTENTO LOGIN ADMIN
                    if (preferenciasManager.validarCredencialesAdmin(input, password)) {
                        preferenciasManager.guardarSesionAdmin(input)
                        onLoginAdmin()
                        return@Button
                    }

                    // ✅ INTENTO LOGIN USUARIO
                    if (input.isNotBlank()) {
                        usuarioViewModel.loginPorEmail(input)
                    } else {
                        mensajeError = "Completa los datos"
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Ingresar")
            }

            TextButton(onClick = onIrRegistro) {
                Text("¿No tienes cuenta? Regístrate")
            }

            Text(
                "Admin demo: admin / admin123",
                fontSize = 12.sp,
                color = Color.Gray
            )
        }
    }
}
