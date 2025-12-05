package com.example.labx.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.labx.data.remote.RetrofitClient
import com.example.labx.data.remote.dto.ProductoDto
import com.example.labx.data.remote.dto.aModelos
import com.example.labx.domain.model.Producto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProductoApiViewModel : ViewModel() {

    private val _productos = MutableStateFlow<List<Producto>>(emptyList())
    val productos: StateFlow<List<Producto>> = _productos

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun cargarDesdeApi() {
        viewModelScope.launch {
            try {
                val respuesta: List<ProductoDto> = RetrofitClient.api.obtenerProductos()

                // Convertimos los DTO → Modelo interno que tú ya usas
                _productos.value = respuesta.aModelos()

            } catch (e: Exception) {
                e.printStackTrace()
                _error.value = "Error al cargar productos: ${e.message}"
            }
        }
    }
}
