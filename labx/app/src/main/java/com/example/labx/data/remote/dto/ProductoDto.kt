package com.example.labx.data.remote.dto

import com.example.labx.domain.model.Producto
import com.google.gson.annotations.SerializedName

/**
 * Data Transfer Object para Producto
 *
 * Esta clase representa la estructura de datos recibida desde la API REST.
 * Utiliza anotaciones @SerializedName para mapear los nombres de campos JSON
 * a nombres de variables en español que usamos en la aplicación.
 *
 * Ejemplo de JSON de la API:
 * {
 *   "id": 1,
 *   "title": "Fjallraven Backpack",
 *   "description": "Your perfect pack...",
 *   "price": 109.95,
 *   "category": "men's clothing",
 *   "image": "https://..."
 * }
 *
 * @author Sting Parra Silva
 * @version 1.0
 */
data class ProductoDto(
    /**
     * Identificador único del producto
     * Campo en JSON: "id"
     */
    @SerializedName("id")
    val identificador: Int,

    /**
     * Nombre o título del producto
     * Campo en JSON: "title"
     */
    @SerializedName("title")
    val titulo: String,

    /**
     * Descripción detallada del producto
     * Campo en JSON: "description"
     */
    @SerializedName("description")
    val descripcion: String,

    /**
     * Precio del producto en la moneda de la API
     * Campo en JSON: "price"
     */
    @SerializedName("price")
    val precio: Double,

    /**
     * URL de la imagen del producto
     * Campo en JSON: "image"
     */
    @SerializedName("image")
    val urlImagen: String,

    /**
     * Categoría a la que pertenece el producto
     * Campo en JSON: "category"
     */
    @SerializedName("category")
    val categoria: String
)

/**
 * Convierte un ProductoDto (estructura de la API) a Producto (modelo de dominio)
 *
 * Esta función de extensión permite transformar la respuesta de la API
 * al modelo interno que usa la aplicación.
 *
 * @return Objeto Producto con los datos mapeados
 *
 * Nota: El campo 'stock' usa un valor por defecto de 10 ya que
 * la API FakeStoreAPI no proporciona información de inventario.
 */
fun ProductoDto.aModelo(): Producto {
    return Producto(
        id = this.identificador,
        nombre = this.titulo,
        descripcion = this.descripcion,
        precio = this.precio,
        imagenUrl = this.urlImagen,
        categoria = this.categoria,
        stock = 10
    )
}

/**
 * Convierte un ProductoDto a Producto con stock personalizado
 *
 * Esta versión permite especificar el stock manualmente, útil cuando
 * se obtiene información de inventario de otra fuente.
 *
 * @param stockDisponible Cantidad de stock del producto
 * @return Objeto Producto con el stock especificado
 */
fun ProductoDto.aModeloConStock(stockDisponible: Int): Producto {
    return Producto(
        id = this.identificador,
        nombre = this.titulo,
        descripcion = this.descripcion,
        precio = this.precio,
        imagenUrl = this.urlImagen,
        categoria = this.categoria,
        stock = stockDisponible
    )
}

/**
 * Convierte un Producto (modelo de dominio) a ProductoDto (estructura de API)
 *
 * Esta función de extensión es útil para operaciones POST y PUT
 * donde necesitamos enviar datos al servidor.
 *
 * @return Objeto ProductoDto listo para serializar a JSON
 *
 * Nota: El campo 'stock' no se incluye en el DTO porque
 * la API FakeStoreAPI no acepta este campo en sus endpoints.
 */
fun Producto.aDto(): ProductoDto {
    return ProductoDto(
        identificador = this.id,
        titulo = this.nombre,
        descripcion = this.descripcion,
        precio = this.precio,
        urlImagen = this.imagenUrl,
        categoria = this.categoria
    )
}

/**
 * Convierte una lista de ProductoDto a lista de Producto
 *
 * Función de utilidad para mapear colecciones completas de una sola vez.
 *
 * @return Lista de objetos Producto
 */
fun List<ProductoDto>.aModelos(): List<Producto> {
    return this.map { it.aModelo() }
}

/**
 * Convierte una lista de Producto a lista de ProductoDto
 *
 * @return Lista de objetos ProductoDto
 */
fun List<Producto>.aDtos(): List<ProductoDto> {
    return this.map { it.aDto() }
}
