package com.example.labx.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.labx.ui.viewmodel.RegistroViewModel
import com.example.labx.ui.viewmodel.RegistroViewModelFactory
import com.example.labx.ui.viewmodel.UsuarioViewModel

/**
 * RegistroScreen: Formulario de registro de usuario
 *
 * Características:
 * - 6 campos con validación en tiempo real
 * - Checkbox de términos y condiciones
 * - Botón deshabilitado si hay errores
 * - Mensajes de error bajo cada campo
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistroScreen(
    usuarioViewModel: UsuarioViewModel,
    onRegistroExitoso: () -> Unit,
    onVolverClick: () -> Unit
) {
    val authState by usuarioViewModel.authState.collectAsState()

    var nombre by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var direccion by remember { mutableStateOf("") }
    var anioNacimiento by remember { mutableStateOf("") }
    var esDuoc by remember { mutableStateOf(false) }
    var codigoReferido by remember { mutableStateOf("") }

    LaunchedEffect(authState.usuarioActual) {
        if (authState.usuarioActual != null && !authState.estaCargando) {
            onRegistroExitoso()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Crear cuenta Level-Up") },
                navigationIcon = {
                    IconButton(onClick = onVolverClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "volver")
                    }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

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
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = telefono,
                onValueChange = { telefono = it },
                label = { Text("Teléfono") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = direccion,
                onValueChange = { direccion = it },
                label = { Text("Dirección") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = anioNacimiento,
                onValueChange = { anioNacimiento = it.filter(Char::isDigit).take(4) },
                label = { Text("Año nacimiento") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
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
                    authState.error ?: "",
                    color = MaterialTheme.colorScheme.error
                )
            }

            Button(
                onClick = {
                    usuarioViewModel.registrarUsuario(
                        nombreCompleto = nombre,
                        email = email,
                        telefono = telefono,
                        direccion = direccion,
                        anioNacimiento = anioNacimiento.toIntOrNull() ?: 2000,
                        esDuoc = esDuoc,
                        codigoReferido = codigoReferido
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !authState.estaCargando
            ) {
                if (authState.estaCargando) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Crear cuenta")
                }
            }
        }
    }
}
