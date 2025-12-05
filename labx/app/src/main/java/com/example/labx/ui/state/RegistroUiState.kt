package com.example.labx.ui.state

import com.example.labx.domain.model.ErroresFormulario
import com.example.labx.domain.model.FormularioRegistro

/**
 * Estado de la UI de registro
 * Incluye el formulario actual y los errores de validación
 * 
 * Autor: Prof. Sting Adams Parra Silva
 */
data class RegistroUiState(
    val formulario: FormularioRegistro = FormularioRegistro(),
    val errores: ErroresFormulario = ErroresFormulario(),
    val estaGuardando: Boolean = false,
    val registroExitoso: Boolean = false
)
