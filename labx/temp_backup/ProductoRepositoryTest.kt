package com.example.labx.data.repository

import app.cash.turbine.test
import com.example.labx.data.local.dao.ProductoDao
import com.example.labx.data.local.entity.ProductoEntity
import com.example.labx.data.remote.api.ProductoApiService
import com.example.labx.data.remote.dto.ProductoDto
import com.example.labx.domain.model.Producto
import com.example.labx.domain.model.toEntity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.*
import retrofit2.Response
import java.io.IOException
import java.net.UnknownHostException

/**
 * Pruebas unitarias para ProductoRepositoryImpl
 * 
 * Qué se prueba:
 * - Obtener productos desde API (caso exitoso)
 * - Fallback a datos locales cuando API falla
 * - Manejo de diferentes tipos de errores
 * - Operaciones CRUD individuales
 * 
 * Frameworks usados:
 * - JUnit 5: Ejecución de pruebas
 * - Mockito: Creación de mocks/dobles
 * - Turbine: Testing de Flows
 * - Coroutines Test: Testing de código asíncrono
 * 
 * @author Sting Parra Silva
 */
@OptIn(ExperimentalCoroutinesApi::class)
class ProductoRepositoryTest {

    // Mocks de las dependencias
    @Mock
    private lateinit var productoDao: ProductoDao
    
    @Mock
    private lateinit var apiService: ProductoApiService
    
    // Sistema bajo prueba
    private lateinit var repository: ProductoRepositoryImpl
    
    // Datos de prueba
    private lateinit var productosDePrueba: List<Producto>
    private lateinit var productosDtoDePrueba: List<ProductoDto>
    private lateinit var entidadesDePrueba: List<ProductoEntity>
    
    @Before
    fun setup() {
        // Inicializar mocks
        MockitoAnnotations.openMocks(this)
        
        // Crear sistema bajo prueba
        repository = ProductoRepositoryImpl(productoDao, apiService)
        
        // Configurar datos de prueba
        setupDatosDePrueba()
    }
    
    private fun setupDatosDePrueba() {
        // Productos de dominio (lo que usa la app)
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
        
        // DTOs (lo que viene de la API)
        productosDtoDePrueba = listOf(
            ProductoDto(
                identificador = 1,
                titulo = "Monitor Gaming",
                descripcion = "Monitor 27 pulgadas 144Hz",
                precio = 250000.0,
                urlImagen = "https://ejemplo.com/monitor.jpg",
                categoria = "Monitores"
            ),
            ProductoDto(
                identificador = 2,
                titulo = "Mouse Gamer",
                descripcion = "Mouse RGB 16000 DPI",
                precio = 45000.0,
                urlImagen = "https://ejemplo.com/mouse.jpg",
                categoria = "Periféricos"
            )
        )
        
        // Entidades (lo que guarda Room)
        entidadesDePrueba = productosDePrueba.map { it.toEntity() }
    }
    
    @Test
    fun `obtenerProductos - API exitosa retorna productos de API`() = runTest {
        // Given: Configurar mocks
        // API responde exitosamente con productos
        val respuestaExitosa = Response.success(productosDtoDePrueba)
        whenever(apiService.obtenerTodosLosProductos()).thenReturn(respuestaExitosa)
        
        // When: Ejecutar el método bajo prueba
        val resultado = repository.obtenerProductos().toList()
        
        // Then: Verificar resultados
        assertEquals(1, resultado.size, "Debe emitir una sola lista")
        assertEquals(productosDePrueba, resultado.first(), "Productos deben coincidir")
        
        // Verificar que se llamó a la API
        verify(apiService, times(1)).obtenerTodosLosProductos()
        
        // Verificar que NO se llamó al DAO (porque API funcionó)
        verify(productoDao, never()).obtenerTodosLosProductos()
    }
    
    @Test
    fun `obtenerProductos - sin internet usa datos locales`() = runTest {
        // Given: Configurar mocks
        // API lanza excepción de sin internet
        whenever(apiService.obtenerTodosLosProductos())
            .thenThrow(UnknownHostException("No internet"))
        
        // DAO retorna productos locales
        whenever(productoDao.obtenerTodosLosProductos())
            .thenReturn(flowOf(entidadesDePrueba))
        
        // When: Ejecutar el método bajo prueba
        val resultado = repository.obtenerProductos().toList()
        
        // Then: Verificar resultados
        assertEquals(1, resultado.size, "Debe emitir una sola lista")
        assertEquals(productosDePrueba, resultado.first(), "Debe retornar productos locales")
        
        // Verificar que se intentó llamar a la API
        verify(apiService, times(1)).obtenerTodosLosProductos()
        
        // Verificar que se llamó al DAO como fallback
        verify(productoDao, times(1)).obtenerTodosLosProductos()
    }
    
    @Test
    fun `obtenerProductos - error HTTP usa datos locales`() = runTest {
        // Given: Configurar mocks
        // API responde con error 500
        val respuestaError = Response.error<List<ProductoDto>>(
            500, 
            okhttp3.ResponseBody.create(null, "Server Error")
        )
        whenever(apiService.obtenerTodosLosProductos()).thenReturn(respuestaError)
        
        // DAO retorna productos locales
        whenever(productoDao.obtenerTodosLosProductos())
            .thenReturn(flowOf(entidadesDePrueba))
        
        // When: Ejecutar el método bajo prueba
        val resultado = repository.obtenerProductos().toList()
        
        // Then: Verificar resultados
        assertEquals(1, resultado.size, "Debe emitir una sola lista")
        assertEquals(productosDePrueba, resultado.first(), "Debe retornar productos locales")
        
        // Verificar que se intentó llamar a la API
        verify(apiService, times(1)).obtenerTodosLosProductos()
        
        // Verificar que se llamó al DAO como fallback
        verify(productoDao, times(1)).obtenerTodosLosProductos()
    }
    
    @Test
    fun `obtenerProductos - timeout usa datos locales`() = runTest {
        // Given: Configurar mocks
        // API lanza IOException (timeout)
        whenever(apiService.obtenerTodosLosProductos())
            .thenThrow(IOException("Timeout"))
        
        // DAO retorna productos locales
        whenever(productoDao.obtenerTodosLosProductos())
            .thenReturn(flowOf(entidadesDePrueba))
        
        // When: Ejecutar el método bajo prueba
        val resultado = repository.obtenerProductos().toList()
        
        // Then: Verificar resultados
        assertEquals(1, resultado.size, "Debe emitir una sola lista")
        assertEquals(productosDePrueba, resultado.first(), "Debe retornar productos locales")
        
        // Verificar que se intentó llamar a la API
        verify(apiService, times(1)).obtenerTodosLosProductos()
        
        // Verificar que se llamó al DAO como fallback
        verify(productoDao, times(1)).obtenerTodosLosProductos()
    }
    
    @Test
    fun `obtenerProductos - respuesta vacia usa datos locales`() = runTest {
        // Given: Configurar mocks
        // API responde exitosamente pero con body null
        val respuestaVacia = Response.success<List<ProductoDto>>(null)
        whenever(apiService.obtenerTodosLosProductos()).thenReturn(respuestaVacia)
        
        // DAO retorna productos locales
        whenever(productoDao.obtenerTodosLosProductos())
            .thenReturn(flowOf(entidadesDePrueba))
        
        // When: Ejecutar el método bajo prueba
        val resultado = repository.obtenerProductos().toList()
        
        // Then: Verificar resultados
        assertEquals(1, resultado.size, "Debe emitir una sola lista")
        assertEquals(productosDePrueba, resultado.first(), "Debe retornar productos locales")
        
        // Verificar que se intentó llamar a la API
        verify(apiService, times(1)).obtenerTodosLosProductos()
        
        // Verificar que se llamó al DAO como fallback
        verify(productoDao, times(1)).obtenerTodosLosProductos()
    }
    
    @Test
    fun `obtenerProductoPorId - API exitosa retorna producto de API`() = runTest {
        // Given: Configurar mocks
        val productoId = 1
        val productoDto = productosDtoDePrueba.first()
        val respuestaExitosa = Response.success(productoDto)
        
        whenever(apiService.obtenerProductoPorId(productoId))
            .thenReturn(respuestaExitosa)
        
        // When: Ejecutar el método bajo prueba
        val resultado = repository.obtenerProductoPorId(productoId)
        
        // Then: Verificar resultados
        assertNotNull(resultado, "Resultado no debe ser null")
        assertEquals(productosDePrueba.first(), resultado, "Producto debe coincidir")
        
        // Verificar que se llamó a la API
        verify(apiService, times(1)).obtenerProductoPorId(productoId)
        
        // Verificar que NO se llamó al DAO
        verify(productoDao, never()).obtenerProductoPorId(any())
    }
    
    @Test
    fun `obtenerProductoPorId - API falla busca en base local`() = runTest {
        // Given: Configurar mocks
        val productoId = 1
        
        // API lanza excepción
        whenever(apiService.obtenerProductoPorId(productoId))
            .thenThrow(UnknownHostException("No internet"))
        
        // DAO tiene el producto
        val entidad = entidadesDePrueba.first()
        whenever(productoDao.obtenerProductoPorId(productoId))
            .thenReturn(entidad)
        
        // When: Ejecutar el método bajo prueba
        val resultado = repository.obtenerProductoPorId(productoId)
        
        // Then: Verificar resultados
        assertNotNull(resultado, "Resultado no debe ser null")
        assertEquals(productosDePrueba.first(), resultado, "Debe retornar producto local")
        
        // Verificar que se intentó llamar a la API
        verify(apiService, times(1)).obtenerProductoPorId(productoId)
        
        // Verificar que se llamó al DAO como fallback
        verify(productoDao, times(1)).obtenerProductoPorId(productoId)
    }
    
    @Test
    fun `insertarProducto - API exitosa guarda en ambos lugares`() = runTest {
        // Given: Configurar mocks
        val nuevoProducto = productosDePrueba.first()
        val productoDto = productosDtoDePrueba.first()
        val respuestaExitosa = Response.success(productoDto)
        val idLocalEsperado = 1L
        
        whenever(apiService.agregarProducto(any()))
            .thenReturn(respuestaExitosa)
        whenever(productoDao.insertarProducto(any()))
            .thenReturn(idLocalEsperado)
        
        // When: Ejecutar el método bajo prueba
        val resultado = repository.insertarProducto(nuevoProducto)
        
        // Then: Verificar resultados
        assertEquals(idLocalEsperado, resultado, "Debe retornar ID local")
        
        // Verificar que se llamó a la API con el DTO correcto
        verify(apiService, times(1)).agregarProducto(any())
        
        // Verificar que se guardó localmente
        verify(productoDao, times(1)).insertarProducto(any())
    }
    
    @Test
    fun `insertarProducto - API falla guarda solo localmente`() = runTest {
        // Given: Configurar mocks
        val nuevoProducto = productosDePrueba.first()
        val idLocalEsperado = 1L
        
        // API lanza excepción
        whenever(apiService.agregarProducto(any()))
            .thenThrow(UnknownHostException("No internet"))
        
        whenever(productoDao.insertarProducto(any()))
            .thenReturn(idLocalEsperado)
        
        // When: Ejecutar el método bajo prueba
        val resultado = repository.insertarProducto(nuevoProducto)
        
        // Then: Verificar resultados
        assertEquals(idLocalEsperado, resultado, "Debe retornar ID local")
        
        // Verificar que se intentó llamar a la API
        verify(apiService, times(1)).agregarProducto(any())
        
        // Verificar que se guardó localmente igualmente
        verify(productoDao, times(1)).insertarProducto(any())
    }
    
    @Test
    fun `actualizarProducto - siempre actualiza localmente`() = runTest {
        // Given: Configurar mocks
        val productoActualizado = productosDePrueba.first()
        val respuestaExitosa = Response.success(productosDtoDePrueba.first())
        
        whenever(apiService.modificarProducto(any(), any()))
            .thenReturn(respuestaExitosa)
        
        // When: Ejecutar el método bajo prueba
        repository.actualizarProducto(productoActualizado)
        
        // Then: Verificar que se llamó a la API
        verify(apiService, times(1)).modificarProducto(productoActualizado.id, any())
        
        // Verificar que SIEMPRE se actualiza localmente
        verify(productoDao, times(1)).actualizarProducto(any())
    }
    
    @Test
    fun `actualizarProducto - API falla actualiza localmente igual`() = runTest {
        // Given: Configurar mocks
        val productoActualizado = productosDePrueba.first()
        
        // API lanza excepción
        whenever(apiService.modificarProducto(any(), any()))
            .thenThrow(IOException("Timeout"))
        
        // When: Ejecutar el método bajo prueba
        repository.actualizarProducto(productoActualizado)
        
        // Then: Verificar que se intentó llamar a la API
        verify(apiService, times(1)).modificarProducto(productoActualizado.id, any())
        
        // Verificar que se actualizó localmente igualmente
        verify(productoDao, times(1)).actualizarProducto(any())
    }
    
    @Test
    fun `eliminarProducto - siempre elimina localmente`() = runTest {
        // Given: Configurar mocks
        val productoAEliminar = productosDePrueba.first()
        val respuestaExitosa = Response.success<Unit>(null)
        
        whenever(apiService.borrarProducto(any()))
            .thenReturn(respuestaExitosa)
        
        // When: Ejecutar el método bajo prueba
        repository.eliminarProducto(productoAEliminar)
        
        // Then: Verificar que se llamó a la API
        verify(apiService, times(1)).borrarProducto(productoAEliminar.id)
        
        // Verificar que SIEMPRE se elimina localmente
        verify(productoDao, times(1)).eliminarProducto(any())
    }
    
    @Test
    fun `eliminarProducto - API falla elimina localmente igual`() = runTest {
        // Given: Configurar mocks
        val productoAEliminar = productosDePrueba.first()
        
        // API lanza excepción
        whenever(apiService.borrarProducto(any()))
            .thenThrow(IOException("Timeout"))
        
        // When: Ejecutar el método bajo prueba
        repository.eliminarProducto(productoAEliminar)
        
        // Then: Verificar que se intentó llamar a la API
        verify(apiService, times(1)).borrarProducto(productoAEliminar.id)
        
        // Verificar que se eliminó localmente igualmente
        verify(productoDao, times(1)).eliminarProducto(any())
    }
    
    @Test
    fun `insertarProductos - solo guarda localmente`() = runTest {
        // Given: Configurar mocks
        val listaProductos = productosDePrueba.take(1)
        
        // When: Ejecutar el método bajo prueba
        repository.insertarProductos(listaProductos)
        
        // Then: Verificar que NO se llamó a la API
        verify(apiService, never()).agregarProducto(any())
        
        // Verificar que se guardó localmente
        verify(productoDao, times(1)).insertarProductos(any())
    }
    
    @Test
    fun `eliminarTodosLosProductos - solo elimina localmente`() = runTest {
        // When: Ejecutar el método bajo prueba
        repository.eliminarTodosLosProductos()
        
        // Then: Verificar que NO se llamó a la API
        verify(apiService, never()).borrarProducto(any())
        
        // Verificar que se eliminó localmente
        verify(productoDao, times(1)).eliminarTodosLosProductos()
    }
}
