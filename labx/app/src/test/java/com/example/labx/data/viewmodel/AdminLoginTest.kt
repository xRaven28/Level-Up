package com.example.labx.data.viewmodel

import com.example.labx.data.local.PreferenciasManager

import org.junit.Before
import org.junit.Test
import org.junit.Assert.assertTrue
import org.junit.Assert.assertFalse

import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class AdminLoginTest {

    private lateinit var preferenciasManager: PreferenciasManager

    @Before
    fun setUp() {
        preferenciasManager = mock()
    }

    @Test
    fun `login admin correcto`() {
        whenever(preferenciasManager.validarCredencialesAdmin("admin", "admin123"))
            .thenReturn(true)

        val resultado = preferenciasManager.validarCredencialesAdmin("admin", "admin123")

        assertTrue(resultado)
    }

    @Test
    fun `login admin incorrecto`() {
        whenever(preferenciasManager.validarCredencialesAdmin("admin", "1234"))
            .thenReturn(false)

        val resultado = preferenciasManager.validarCredencialesAdmin("admin", "1234")

        assertFalse(resultado)
    }
}
