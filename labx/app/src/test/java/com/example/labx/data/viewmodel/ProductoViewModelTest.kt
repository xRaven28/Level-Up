package com.example.labx.data.viewmodel

import com.example.labx.data.repository.ProductoRepositoryImpl
import com.example.labx.domain.model.Producto
import com.example.labx.ui.viewmodel.ProductoViewModel

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain

import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

@OptIn(ExperimentalCoroutinesApi::class)
class ProductoViewModelTest {

    private val dispatcher = StandardTestDispatcher()

    private lateinit var repository: ProductoRepositoryImpl
    private lateinit var viewModel: ProductoViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        repository = mock()
        viewModel = ProductoViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `agregar producto`() = runTest {
        val producto = Producto(
            id = 1,
            nombre = "Mouse",
            categoria = "Periféricos",
            descripcion = "Mouse gamer RGB",
            precio = 5000.0,
            stock = 10,
            imagenUrl = "https://imagen.com/mouse.png"
        )

        viewModel.agregarProducto(producto)
        advanceUntilIdle()

        verify(repository).insertarProducto(producto)
    }

    @Test
    fun `eliminar producto`() = runTest {
        val producto = Producto(
            id = 2,
            nombre = "Teclado",
            categoria = "Periféricos",
            descripcion = "Teclado mecánico RGB",
            precio = 8000.0,
            stock = 5,
            imagenUrl = "https://imagen.com/teclado.png"
        )

        viewModel.eliminarProducto(producto)
        advanceUntilIdle()

        verify(repository).eliminarProducto(producto)
    }
}
