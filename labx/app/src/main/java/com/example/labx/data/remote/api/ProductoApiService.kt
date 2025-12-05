package com.example.labx.data.remote.api

import com.example.labx.data.remote.dto.ProductoDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Interface del servicio API para operaciones de productos
 *
 * Define todos los endpoints disponibles para interactuar con la API REST.
 * Retrofit genera automáticamente la implementación de esta interface en tiempo de ejecución.
 *
 * Todas las funciones son 'suspend' porque realizan operaciones de red asíncronas
 * que deben ejecutarse dentro de una coroutine.
 *
 * El tipo de retorno Response<T> envuelve el resultado y permite acceder a:
 * - Código de estado HTTP (200, 404, 500, etc.)
 * - Headers de la respuesta
 * - Cuerpo de la respuesta (body)
 * - Mensajes de error
 *
 * @author Sting Parra Silva
 * @version 1.0
 */
interface ProductoApiService {

    /**
     * Obtiene todos los productos disponibles en la API
     *
     * Endpoint: GET /products
     * URL completa: https://fakestoreapi.com/products
     *
     * @return Response con lista de ProductoDto
     *
     * Ejemplo de uso:
     * ```
     * val respuesta = apiService.obtenerTodosLosProductos()
     * if (respuesta.isSuccessful) {
     *     val productos = respuesta.body()
     * }
     * ```
     */
    @GET("products")
    suspend fun obtenerTodosLosProductos(): Response<List<ProductoDto>>

    /**
     * Obtiene un producto específico por su ID
     *
     * Endpoint: GET /products/{id}
     * URL completa ejemplo: https://fakestoreapi.com/products/5
     *
     * @param identificador ID único del producto
     * @return Response con ProductoDto individual
     *
     * La anotación @Path reemplaza {id} en la URL con el valor del parámetro
     */
    @GET("products/{id}")
    suspend fun obtenerProductoPorId(
        @Path("id") identificador: Int
    ): Response<ProductoDto>

    /**
     * Obtiene productos filtrados por categoría
     *
     * Endpoint: GET /products/category/{categoria}
     * URL completa ejemplo: https://fakestoreapi.com/products/category/electronics
     *
     * Categorías disponibles en FakeStoreAPI:
     * - electronics
     * - jewelery
     * - men's clothing
     * - women's clothing
     *
     * @param nombreCategoria Nombre de la categoría
     * @return Response con lista de ProductoDto de esa categoría
     */
    @GET("products/category/{categoria}")
    suspend fun obtenerProductosPorCategoria(
        @Path("categoria") nombreCategoria: String
    ): Response<List<ProductoDto>>

    /**
     * Obtiene la lista de todas las categorías disponibles
     *
     * Endpoint: GET /products/categories
     * URL completa: https://fakestoreapi.com/products/categories
     *
     * @return Response con lista de nombres de categorías (String)
     *
     * Útil para poblar filtros o menús desplegables de categorías
     */
    @GET("products/categories")
    suspend fun obtenerCategorias(): Response<List<String>>

    /**
     * Obtiene un número limitado de productos
     *
     * Endpoint: GET /products?limit={cantidad}
     * URL completa ejemplo: https://fakestoreapi.com/products?limit=5
     *
     * @param limite Cantidad máxima de productos a obtener
     * @return Response con lista limitada de ProductoDto
     *
     * La anotación @Query agrega el parámetro como query string (?limit=5)
     * Útil para paginación o carga inicial rápida
     */
    @GET("products")
    suspend fun obtenerProductosConLimite(
        @Query("limit") limite: Int
    ): Response<List<ProductoDto>>

    /**
     * Obtiene productos ordenados
     *
     * Endpoint: GET /products?sort={orden}
     * URL completa ejemplo: https://fakestoreapi.com/products?sort=desc
     *
     * @param orden Tipo de ordenamiento: "asc" (ascendente) o "desc" (descendente)
     * @return Response con lista ordenada de ProductoDto
     *
     * Por defecto la API ordena por ID
     */
    @GET("products")
    suspend fun obtenerProductosOrdenados(
        @Query("sort") orden: String = "asc"
    ): Response<List<ProductoDto>>

    /**
     * Crea un nuevo producto en la API
     *
     * Endpoint: POST /products
     * URL completa: https://fakestoreapi.com/products
     *
     * @param nuevoProducto Objeto ProductoDto con los datos del producto
     * @return Response con el ProductoDto creado (incluye ID asignado)
     *
     * La anotación @Body serializa el objeto a JSON y lo envía en el cuerpo de la petición
     *
     * IMPORTANTE: FakeStoreAPI simula la creación pero no persiste los datos realmente.
     * El ID retornado no será accesible en peticiones posteriores.
     *
     * Ejemplo de uso:
     * ```
     * val producto = ProductoDto(...)
     * val respuesta = apiService.agregarProducto(producto)
     * if (respuesta.isSuccessful) {
     *     val productoCreado = respuesta.body()
     *     Log.d("API", "Producto creado con ID: ${productoCreado?.identificador}")
     * }
     * ```
     */
    @POST("products")
    suspend fun agregarProducto(
        @Body nuevoProducto: ProductoDto
    ): Response<ProductoDto>

    /**
     * Actualiza un producto existente (reemplazo completo)
     *
     * Endpoint: PUT /products/{id}
     * URL completa ejemplo: https://fakestoreapi.com/products/7
     *
     * @param identificador ID del producto a actualizar
     * @param productoActualizado Objeto ProductoDto con todos los campos actualizados
     * @return Response con el ProductoDto actualizado
     *
     * PUT reemplaza todos los campos del producto. Si solo quieres actualizar
     * algunos campos, considera usar PATCH (si la API lo soporta).
     *
     * IMPORTANTE: FakeStoreAPI simula la actualización pero no persiste cambios.
     */
    @PUT("products/{id}")
    suspend fun modificarProducto(
        @Path("id") identificador: Int,
        @Body productoActualizado: ProductoDto
    ): Response<ProductoDto>

    /**
     * Elimina un producto de la API
     *
     * Endpoint: DELETE /products/{id}
     * URL completa ejemplo: https://fakestoreapi.com/products/6
     *
     * @param identificador ID del producto a eliminar
     * @return Response con Unit (respuesta vacía si es exitosa)
     *
     * IMPORTANTE: FakeStoreAPI simula la eliminación pero el producto
     * seguirá existiendo en peticiones posteriores.
     *
     * Para verificar éxito, revisar el código HTTP:
     * - 200 OK: Eliminación exitosa
     * - 404 Not Found: Producto no existe
     * - 500 Internal Server Error: Error del servidor
     */
    @DELETE("products/{id}")
    suspend fun borrarProducto(
        @Path("id") identificador: Int
    ): Response<Unit>

    /**
     * Obtiene productos con múltiples filtros
     *
     * Endpoint: GET /products?limit={limite}&sort={orden}
     * URL completa ejemplo: https://fakestoreapi.com/products?limit=5&sort=desc
     *
     * @param limite Cantidad máxima de productos
     * @param orden Tipo de ordenamiento ("asc" o "desc")
     * @return Response con lista filtrada y ordenada de ProductoDto
     *
     * Demuestra cómo combinar múltiples parámetros @Query
     */
    @GET("products")
    suspend fun obtenerProductosFiltrados(
        @Query("limit") limite: Int,
        @Query("sort") orden: String
    ): Response<List<ProductoDto>>
}

/**
 * NOTAS DE IMPLEMENTACIÓN PARA JSON SERVER LOCAL:
 *
 * Si usas JSON Server en lugar de FakeStoreAPI, los endpoints son similares
 * pero con estas diferencias:
 *
 * 1. Cambiar URL_BASE en RetrofitClient a:
 *    - Emulador: "http://10.0.2.2:3000/"
 *    - Dispositivo físico: "http://[TU_IP_LOCAL]:3000/"
 *
 * 2. JSON Server sí persiste los cambios (POST, PUT, DELETE son reales)
 *
 * 3. JSON Server soporta más operaciones de filtrado:
 *    @GET("products?_page={page}&_limit={limit}")  // Paginación
 *    @GET("products?precio_gte={min}&precio_lte={max}")  // Rango de precios
 *    @GET("products?q={busqueda}")  // Búsqueda full-text
 *
 * 4. Iniciar JSON Server:
 *    json-server --watch db.json --host 0.0.0.0 --port 3000
 */
