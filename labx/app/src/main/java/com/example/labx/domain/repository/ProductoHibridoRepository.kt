package com.example.labx.domain.repository

import com.example.labx.data.remote.RetrofitClient
import com.example.labx.data.remote.dto.aModelos
import com.example.labx.domain.model.Producto
import kotlinx.coroutines.flow.first

class ProductoHibridoRepository(
    private val repositorioLocal: RepositorioProductos
) {

    suspend fun obtenerProductosCombinados(): List<Producto> {

        val productosLocales = repositorioLocal.obtenerProductos().first()

        return try {
            val productosApi = RetrofitClient.api.obtenerProductos()

            val productosConvertidos = productosApi.aModelos().map { producto ->
                producto.copy(
                    id = producto.id + 100_000, // ✅ ID SEGURO PARA ROOM
                    precio = producto.precio * 950,
                    descripcion = "Producto importado - Garantía nacional"
                )
            }

            val productosNuevos = productosConvertidos.filter { apiProducto ->
                productosLocales.none { localProducto ->
                    localProducto.id == apiProducto.id
                }
            }

            if (productosNuevos.isNotEmpty()) {
                repositorioLocal.insertarProductos(productosNuevos)
            }

            // ✅ SIEMPRE SE DEVUELVE DESDE ROOM
            repositorioLocal.obtenerProductos().first()

        } catch (e: Exception) {
            productosLocales
        }
    }
}

