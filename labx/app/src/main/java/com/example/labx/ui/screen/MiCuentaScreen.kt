package com.example.labx.ui.screen

import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.labx.data.local.PreferenciasManager
import com.example.labx.ui.viewmodel.UsuarioViewModel
import com.example.labx.utils.guardarBitmapLocal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MiCuentaScreen(
    usuarioViewModel: UsuarioViewModel,
    preferenciasManager: PreferenciasManager,
    onCerrarSesion: () -> Unit,
    onVolver: () -> Unit
) {
    val authState by usuarioViewModel.authState.collectAsState()
    val usuario = authState.usuarioActual

    var fotoUri by remember { mutableStateOf(preferenciasManager.obtenerFotoPerfil()) }
    val scrollState = rememberScrollState()

    val launcherCamera = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        bitmap?.let {
            fotoUri = guardarBitmapLocal(it, preferenciasManager)
        }
    }

    val launcherGaleria = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            fotoUri = it.toString()
            preferenciasManager.guardarFotoPerfil(fotoUri!!)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi Cuenta") },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->

        if (usuario == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("No hay sesión activa", fontSize = 18.sp)
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // FOTO
            if (fotoUri != null) {
                AsyncImage(
                    model = fotoUri,
                    contentDescription = "Foto de perfil",
                    modifier = Modifier.size(120.dp).clip(CircleShape)
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Sin foto",
                    modifier = Modifier.size(120.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(onClick = { launcherCamera.launch(null) }) {
                    Text("Cámara")
                }
                Button(onClick = { launcherGaleria.launch("image/*") }) {
                    Text("Galería")
                }
            }

            Divider()

            Text(usuario.nombreCompleto, fontSize = 24.sp, fontWeight = FontWeight.Bold)

            Divider()
            DatosItem("Email:", usuario.email)
            DatosItem("Teléfono:", usuario.telefono)
            DatosItem("Dirección:", usuario.direccion)
            DatosItem("Año de nacimiento:", usuario.anioNacimiento.toString())

            Divider()
            DatosItem("Código Propio:", usuario.codigoPropio)

            usuario.codigoReferido?.let {
                DatosItem("Código Referido:", it)
            }

            Divider()
            DatosItem("Nivel:", usuario.nivel.toString())
            DatosItem("Puntos Level-Up:", usuario.puntosLevelUp.toString())

            Spacer(modifier = Modifier.height(30.dp))
            Button(
                onClick = {
                    usuarioViewModel.cerrarSesion()
                    preferenciasManager.cerrarSesionAdmin()
                    onCerrarSesion()
                },
                modifier = Modifier.fillMaxWidth(),
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Icon(Icons.Default.ExitToApp, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Cerrar Sesión")
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun DatosItem(titulo: String, valor: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(titulo, fontSize = 14.sp, color = MaterialTheme.colorScheme.primary)
        Text(valor, fontSize = 17.sp, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(8.dp))
    }
}
