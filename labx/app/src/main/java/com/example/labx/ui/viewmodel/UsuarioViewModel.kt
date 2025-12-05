package com.example.labx.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.labx.data.local.SesionUsuarioManager
import com.example.labx.data.local.entity.UsuarioEntity
import com.example.labx.data.repository.UsuarioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

// ✅ Estado de autenticación de USUARIO NORMAL (NO ADMIN)
data class AuthState(
    val estaCargando: Boolean = false,
    val usuarioActual: UsuarioEntity? = null,
    val error: String? = null
)

class UsuarioViewModel(
    private val repository: UsuarioRepository,
    private val sesionUsuarioManager: SesionUsuarioManager
) : ViewModel() {

    // ✅ Estado de sesión de usuario normal
    private val _authState = MutableStateFlow(AuthState())
    val authState: StateFlow<AuthState> = _authState

    // ✅ Lista de usuarios para el panel admin
    private val _listaUsuarios = MutableStateFlow<List<UsuarioEntity>>(emptyList())
    val listaUsuarios: StateFlow<List<UsuarioEntity>> = _listaUsuarios

    init {
        // ✅ Rehidratar sesión guardada
        viewModelScope.launch {
            val idGuardado = sesionUsuarioManager.obtenerUsuarioActivoId()
            if (idGuardado != null) {
                val usuario = repository.obtenerUsuarioPorId(idGuardado)
                _authState.value = _authState.value.copy(usuarioActual = usuario)
            }
        }
    }

    // ✅ REGISTRO DE USUARIO NORMAL
    fun registrarUsuario(
        nombreCompleto: String,
        email: String,
        telefono: String,
        direccion: String,
        anioNacimiento: Int,
        esDuoc: Boolean,
        codigoReferido: String?
    ) {
        viewModelScope.launch {
            try {
                _authState.value =
                    _authState.value.copy(estaCargando = true, error = null)

                val codigoPropio = generarCodigo(nombreCompleto)

                val nuevoUsuario = UsuarioEntity(
                    nombreCompleto = nombreCompleto.trim(),
                    email = email.trim(),
                    telefono = telefono.trim(),
                    direccion = direccion.trim(),
                    anioNacimiento = anioNacimiento,
                    esDuoc = esDuoc,
                    codigoPropio = codigoPropio,
                    codigoReferido = codigoReferido?.takeIf { it.isNotBlank() },
                    puntosLevelUp = 0,
                    nivel = 1
                )

                val idNuevo = repository.registrarUsuario(nuevoUsuario)
                val usuarioConId = nuevoUsuario.copy(id = idNuevo)

                sesionUsuarioManager.guardarUsuarioActivo(idNuevo)

                _authState.value = AuthState(
                    estaCargando = false,
                    usuarioActual = usuarioConId,
                    error = null
                )
            } catch (e: Exception) {
                _authState.value = _authState.value.copy(
                    estaCargando = false,
                    error = e.message ?: "Error registrando usuario"
                )
            }
        }
    }

    // ✅ LOGIN SOLO POR EMAIL (USUARIO NORMAL)
    fun loginPorEmail(email: String) {
        viewModelScope.launch {
            try {
                _authState.value =
                    _authState.value.copy(estaCargando = true, error = null)

                val usuario = repository.obtenerUsuarioPorEmail(email.trim())

                if (usuario != null) {
                    sesionUsuarioManager.guardarUsuarioActivo(usuario.id)
                    _authState.value = AuthState(
                        estaCargando = false,
                        usuarioActual = usuario,
                        error = null
                    )
                } else {
                    _authState.value = AuthState(
                        estaCargando = false,
                        usuarioActual = null,
                        error = "No existe una cuenta con ese correo"
                    )
                }
            } catch (e: Exception) {
                _authState.value = _authState.value.copy(
                    estaCargando = false,
                    error = e.message ?: "Error iniciando sesión"
                )
            }
        }
    }

    // ✅ CERRAR SESIÓN USUARIO NORMAL
    fun cerrarSesion() {
        sesionUsuarioManager.cerrarSesion()
        _authState.value = AuthState()
    }

    // ✅ CARGAR USUARIOS PARA EL PANEL ADMIN
    fun cargarUsuarios() {
        viewModelScope.launch {
            try {
                _listaUsuarios.value = repository.obtenerUsuarios()
            } catch (e: Exception) {
                _listaUsuarios.value = emptyList()
            }
        }
    }

    // ✅ Generador de código tipo VER123
    private fun generarCodigo(nombre: String): String {
        val base = nombre
            .trim()
            .takeWhile { it.isLetter() }
            .take(3)
            .uppercase()
            .padEnd(3, 'X')

        val numeros = Random.nextInt(100, 999)
        return "$base$numeros"
    }
}

// ✅ FACTORY
class UsuarioViewModelFactory(
    private val repository: UsuarioRepository,
    private val sesionUsuarioManager: SesionUsuarioManager
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UsuarioViewModel::class.java)) {
            return UsuarioViewModel(repository, sesionUsuarioManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
