package com.example.labx.ui.viewmodel

import app.cash.turbine.test
import com.example.labx.data.repository.ProductoRepositoryImpl
import com.example.labx.domain.model.Producto
import com.example.labx.ui.state.ProductoUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.*

/**
 * Pruebas unitarias para ProductoViewModel
 * 
 * Qué se prueba:
 * - Estados de carga (loading, success, error)
 * - Interacción con el repositorio
 * - Manejo de errores
 * - Operaciones CRUD desde el ViewModel
 * 
 * Frameworks usados:
 * - JUnit 5: Ejecución de pruebas
 * - Mockito: Creación de mocks
 * - Turbine: Testing de StateFlow
 * - Coroutines Test: Testing de código asíncrono
 * 
 * @author Sting Parra Silva
 */
@OptIn(ExperimentalCoroutinesApi::class)
class ProductoViewModelTest {

    // Mock de la dependencia
    @Mock
    private lateinit var repository: ProductoRepositoryImpl
    
    // Sistema bajo prueba
    private lateinit var viewModel: ProductoViewModel
    
    // Dispatcher para testing
    private val testDispatcher = StandardTestDispatcher()
    
    // Datos de prueba
    private lateinit var productosDePrueba: List<Producto>
    private lateinit var productoDePrueba: Producto
    
    @Before
    fun setup() {
        // Inicializar mocks
        MockitoAnnotations.openMocks(this)
        
        // Configurar dispatcher para testing
        Dispatchers.setMain(testDispatcher)
        
        // Crear sistema bajo prueba
        viewModel = ProductoViewModel(repository)
        
        // Configurar datos de prueba
        setupDatosDePrueba()
    }
    
    private fun setupDatosDePrueba() {
        productosDePrueba = listOf(
            Producto(
                id = 1,
                nombre = "Monitor Gaming",
                descripcion = "Monitor 27 pulgadas 144Hz",
                precio = 250000.0,
                imagenUrl = "https://ejemplo.com/monitor.jpg",
                categoria = "Monitores",
                stock = 10
            ),
            Producto(
                id = 2,
                nombre = "Mouse Gamer",
                descripcion = "Mouse RGB 16000 DPI",
                precio = 45000.0,
                imagenUrl = "https://ejemplo.com/mouse.jpg",
                categoria = "Periféricos",
                stock = 15
            )
        )
        
        productoDePrueba = productosDePrueba.first()
    }
    
    @Test
    fun `init - carga productos y actualiza estado correctamente`() = runTest {
        // Given: Configurar mock del repositorio
        whenever(repository.obtenerProductos())
            .thenReturn(flowOf(productosDePrueba))
        
        // When: El ViewModel se inicializa (ya ocurre en setup)
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then: Verificar estado final
        val estadoFinal = viewModel.uiState.value
        assertFalse(estadoFinal.estaCargando, "No debe estar cargando")
        assertNull(estadoFinal.error, "No debe haber error")
        assertEquals(productosDePrueba, estadoFinal.productos, "Productos deben coincidir")
        
        // Verificar que se llamó al repositorio
        verify(repository, times(1)).obtenerProductos()
    }
    
    @Test
    fun `cargarProductos - muestra estado de carga durante la operación`() = runTest {
        // Given: Configurar mock con delay para simular carga
        whenever(repository.obtenerProductos())
            .thenReturn(flow {
                emit(emptyList()) // Esto se ignora, solo para el test
            })
        
        // When: Ejecutar carga de productos
        viewModel.cargarProductos()
        
        // Then: Verificar estado de carga inmediato
        var estadoCarga = viewModel.uiState.value
        assertTrue(estadoCarga.estaCargando, "Debe estar cargando")
        
        // Avanzar tiempo para completar la coroutine
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Verificar estado final
        estadoCarga = viewModel.uiState.value
        assertFalse(estadoCarga.estaCargando, "No debe seguir cargando")
    }
    
    @Test
    fun `cargarProductos - maneja error del repositorio correctamente`() = runTest {
        // Given: Configurar mock para lanzar excepción
        val mensajeError = "Error de conexión"
        whenever(repository.obtenerProductos())
            .thenReturn(flow {
                throw Exception(mensajeError)
            })
        
        // When: Ejecutar carga de productos
        viewModel.cargarProductos()
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then: Verificar estado de error
        val estadoFinal = viewModel.uiState.value
        assertFalse(estadoFinal.estaCargando, "No debe estar cargando")
        assertEquals(mensajeError, estadoFinal.error, "Debe mostrar el error")
        assertTrue(estadoFinal.productos.isEmpty(), "Lista de productos debe estar vacía")
        
        // Verificar que se llamó al repositorio
        verify(repository, times(1)).obtenerProductos()
    }
    
    @Test
    fun `cargarProductos - maneja error sin mensaje correctamente`() = runTest {
        // Given: Configurar mock para lanzar excepción sin mensaje
        whenever(repository.obtenerProductos())
            .thenReturn(flow {
                throw RuntimeException()
            })
        
        // When: Ejecutar carga de productos
        viewModel.cargarProductos()
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then: Verificar estado de error con mensaje por defecto
        val estadoFinal = viewModel.uiState.value
        assertFalse(estadoFinal.estaCargando, "No debe estar cargando")
        assertEquals("Error desconocido", estadoFinal.error, "Debe mostrar error por defecto")
    }
    
    @Test
    fun `agregarProducto - llama al repositorio correctamente`() = runTest {
        // Given: Configurar mock
        whenever(repository.insertarProducto(any())).thenReturn(1L)
        
        // When: Agregar producto
        viewModel.agregarProducto(productoDePrueba)
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then: Verificar que se llamó al repositorio con el producto correcto
        verify(repository, times(1)).insertarProducto(productoDePrueba)
    }
    
    @Test
    fun `actualizarProducto - llama al repositorio correctamente`() = runTest {
        // Given: Configurar mock
        whenever(repository.actualizarProducto(any())).thenReturn(Unit)
        
        // When: Actualizar producto
        viewModel.actualizarProducto(productoDePrueba)
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then: Verificar que se llamó al repositorio con el producto correcto
        verify(repository, times(1)).actualizarProducto(productoDePrueba)
    }
    
    @Test
    fun `eliminarProducto - llama al repositorio correctamente`() = runTest {
        // Given: Configurar mock
        whenever(repository.eliminarProducto(any())).thenReturn(Unit)
        
        // When: Eliminar producto
        viewModel.eliminarProducto(productoDePrueba)
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then: Verificar que se llamó al repositorio con el producto correcto
        verify(repository, times(1)).eliminarProducto(productoDePrueba)
    }
    
    @Test
    fun `obtenerProductoPorId - llama al repositorio correctamente`() = runTest {
        // Given: Configurar mock
        whenever(repository.obtenerProductoPorId(1)).thenReturn(productoDePrueba)
        
        // When: Obtener producto por ID
        val resultado = viewModel.obtenerProductoPorId(1)
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then: Verificar resultado y llamada al repositorio
        assertEquals(productoDePrueba, resultado, "Debe retornar el producto correcto")
        verify(repository, times(1)).obtenerProductoPorId(1)
    }
    
    @Test
    fun `uiState - emite cambios de estado correctamente`() = runTest {
        // Given: Configurar mock inicial
        whenever(repository.obtenerProductos())
            .thenReturn(flowOf(emptyList()))
        
        // When: Observar cambios en el uiState
        viewModel.uiState.test {
            // Estado inicial: debe tener productos vacíos y no estar cargando
            val estadoInicial = awaitItem()
            assertTrue(estadoInicial.productos.isEmpty(), "Productos iniciales deben estar vacíos")
            
            // Trigger nueva carga
            viewModel.cargarProductos()
            
            // Estado de carga
            val estadoCarga = awaitItem()
            assertTrue(estadoCarga.estaCargando, "Debe estar cargando")
            
            // Avanzar tiempo para completar
            testDispatcher.scheduler.advanceUntilIdle()
            
            // Estado final
            val estadoFinal = awaitItem()
            assertFalse(estadoFinal.estaCargando, "No debe seguir cargando")
            assertTrue(estadoFinal.productos.isEmpty(), "Productos deben estar vacíos")
            
            cancelAndIgnoreRemainingEvents()
        }
    }
    
    @Test
    fun `multiple cargarProductos calls - maneja correctamente`() = runTest {
        // Given: Configurar mock
        whenever(repository.obtenerProductos())
            .thenReturn(flowOf(productosDePrueba))
        
        // When: Realizar múltiples llamadas a cargarProductos
        viewModel.cargarProductos()
        viewModel.cargarProductos()
        viewModel.cargarProductos()
        
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then: Verificar que se llamó al repositorio múltiples veces
        verify(repository, times(4)).obtenerProductos() // 1 del init + 3 explícitas
        
        // Verificar estado final
        val estadoFinal = viewModel.uiState.value
        assertEquals(productosDePrueba, estadoFinal.productos, "Productos deben coincidir")
    }
    
    @Test
    fun `estado inicial - valores por defecto correctos`() {
        // Given: Estado inicial del ViewModel
        
        // When: Obtener estado inicial
        val estadoInicial = viewModel.uiState.value
        
        // Then: Verificar valores por defecto
        assertFalse(estadoInicial.estaCargando, "No debe estar cargando inicialmente")
        assertTrue(estadoInicial.productos.isEmpty(), "Lista de productos debe estar vacía")
        assertNull(estadoInicial.error, "No debe haber error inicialmente")
    }
}
