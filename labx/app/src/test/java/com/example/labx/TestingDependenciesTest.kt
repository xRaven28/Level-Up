package com.example.labx

import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

/**
 * Test para verificar que todas las dependencias de testing están configuradas correctamente
 */
class TestingDependenciesTest {

    private lateinit var mockService: MockService

    @Before
    fun setup() {
        mockService = mock()
    }

    @Test
    fun `assertEquals basico funciona correctamente`() {
        val resultado = 2 + 2
        assertEquals(4, resultado)
    }

    @Test
    fun `mockito inline mock funciona correctamente`() {
        whenever(mockService.obtenerValor()).thenReturn("Mock funcionando")
        val resultado = mockService.obtenerValor()
        assertEquals("Mock funcionando", resultado)
    }

    @Test
    fun `coroutines runTest funciona correctamente`() = runTest {
        val resultado = suspenderFuncion()
        assertEquals("Coroutine funcionando", resultado)
    }

    @Test
    fun `flow simple funciona correctamente sin turbine`() = runTest {
        val flow = flowOf("Item 1", "Item 2", "Item 3")

        val primerValor = flow.first()

        assertEquals("Item 1", primerValor)
    }

    interface MockService {
        fun obtenerValor(): String
    }

    private suspend fun suspenderFuncion(): String {
        return "Coroutine funcionando"
    }
}
