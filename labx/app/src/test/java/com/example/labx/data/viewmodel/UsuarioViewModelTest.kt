package com.example.labx.data.viewmodel

import com.example.labx.data.local.SesionUsuarioManager
import com.example.labx.data.local.entity.UsuarioEntity
import com.example.labx.data.repository.UsuarioRepository
import com.example.labx.ui.viewmodel.UsuarioViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.*
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.mockito.kotlin.verify
import org.mockito.kotlin.times
import org.junit.Assert.*

@OptIn(ExperimentalCoroutinesApi::class)
class UsuarioViewModelTest {

    private val dispatcher = StandardTestDispatcher()

    private lateinit var repository: UsuarioRepository
    private lateinit var sesionManager: SesionUsuarioManager
    private lateinit var viewModel: UsuarioViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)

        repository = mock()
        sesionManager = mock()

        viewModel = UsuarioViewModel(repository, sesionManager)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `login correcto por email`() = runTest {

        val email = "test@correo.com"

        val usuarioMock = UsuarioEntity(
            id = 1L,
            nombreCompleto = "Usuario Test",
            email = email,
            telefono = "123456789",
            direccion = "Dirección Test",
            anioNacimiento = 2000,
            codigoPropio = "ABC123",
            codigoReferido = null,
            nivel = 1,
            puntosLevelUp = 100,
            esDuoc = false
        )

        whenever(repository.obtenerUsuarioPorEmail(email)).thenReturn(usuarioMock)

        viewModel.loginPorEmail(email)

        advanceUntilIdle()

        val state = viewModel.authState.value


        assertNotNull(state.usuarioActual)
        assertEquals(email, state.usuarioActual!!.email)

        verify(sesionManager, times(1))
            .guardarUsuarioActivo(usuarioMock.id)

    }
}
