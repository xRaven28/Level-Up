package com.example.labx.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.labx.domain.repository.ProductoHibridoRepository
import com.example.labx.domain.repository.RepositorioProductos
import com.example.labx.ui.state.ProductoUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProductoViewModel(
    private val repositorioLocal: RepositorioProductos
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProductoUiState())
    val uiState: StateFlow<ProductoUiState> = _uiState.asStateFlow()

    private val repositorioHibrido = ProductoHibridoRepository(repositorioLocal)

    init {
        cargarProductos()
    }

    fun cargarProductos() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(estaCargando = true)

            try {
                val productos = repositorioHibrido.obtenerProductosCombinados()

                _uiState.value = _uiState.value.copy(
                    productos = productos,
                    estaCargando = false,
                    error = null
                )

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    estaCargando = false,
                    error = "Error al cargar productos"
                )
            }
        }
    }

    suspend fun obtenerProductoPorId(id: Int) =
        repositorioLocal.obtenerProductoPorId(id)

    fun agregarProducto(producto: com.example.labx.domain.model.Producto) {
        viewModelScope.launch {
            repositorioLocal.insertarProducto(producto)
        }
    }

    fun actualizarProducto(producto: com.example.labx.domain.model.Producto) {
        viewModelScope.launch {
            repositorioLocal.actualizarProducto(producto)
        }
    }

    fun eliminarProducto(producto: com.example.labx.domain.model.Producto) {
        viewModelScope.launch {
            repositorioLocal.eliminarProducto(producto)
        }
    }
}
