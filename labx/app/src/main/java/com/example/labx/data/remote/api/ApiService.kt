package com.example.labx.data.remote.api

import com.example.labx.data.remote.dto.ProductoDto
import retrofit2.http.GET

interface ApiService {

    @GET("products")
    suspend fun obtenerProductos(): List<ProductoDto>
}
