package com.example.labx.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.labx.ui.viewmodel.UsuarioViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistroUsuarioScreen(
    usuarioViewModel: UsuarioViewModel,
    onRegistroExitoso: () -> Unit,
    onIrLogin: () -> Unit
) {
    val authState by usuarioViewModel.authState.collectAsState()

    var nombre by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var direccion by remember { mutableStateOf("") }
    var anio by remember { mutableStateOf("") }
    var esDuoc by remember { mutableStateOf(false) }
    var codigoReferido by remember { mutableStateOf("") }

    // Navegar cuando quede logeado
    LaunchedEffect(authState.usuarioActual) {
        if (authState.usuarioActual != null && !authState.estaCargando) {
            onRegistroExitoso()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Crear cuenta Level-Up") }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Regístrate para guardar tus compras y puntos",
                fontSize = 16.sp
            )

            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre completo") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )

            OutlinedTextField(
                value = telefono,
                onValueChange = { telefono = it },
                label = { Text("Teléfono") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
            )

            OutlinedTextField(
                value = direccion,
                onValueChange = { direccion = it },
                label = { Text("Dirección") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = anio,
                onValueChange = { anio = it.filter { c -> c.isDigit() }.take(4) },
                label = { Text("Año de nacimiento") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(checked = esDuoc, onCheckedChange = { esDuoc = it })
                Text("Soy de Duoc UC")
            }

            OutlinedTextField(
                value = codigoReferido,
                onValueChange = { codigoReferido = it },
                label = { Text("Código referido (opcional)") },
                modifier = Modifier.fillMaxWidth()
            )

            if (authState.error != null) {
                Text(
                    text = authState.error ?: "",
                    color = MaterialTheme.colorScheme.error
                )
            }

            Button(
                onClick = {
                    val anioInt = anio.toIntOrNull() ?: 2000
                    usuarioViewModel.registrarUsuario(
                        nombreCompleto = nombre,
                        email = email,
                        telefono = telefono,
                        direccion = direccion,
                        anioNacimiento = anioInt,
                        esDuoc = esDuoc,
                        codigoReferido = codigoReferido
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !authState.estaCargando
            ) {
                if (authState.estaCargando) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Crear cuenta")
                }
            }

            TextButton(onClick = onIrLogin) {
                Text("¿Ya tienes cuenta? Inicia sesión")
            }
        }
    }
}
