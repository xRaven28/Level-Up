package com.example.labx.data.remote

/**
 * Sealed class para representar los diferentes estados de una operación de red
 *
 * Esta clase utiliza el patrón Result para encapsular el estado de una petición HTTP,
 * permitiendo manejar de forma segura y explícita los casos de éxito, error y carga.
 *
 * Ventajas de usar sealed class:
 * - Exhaustividad en when: El compilador garantiza que se manejen todos los casos
 * - Type-safe: Cada estado tiene su propio tipo con datos específicos
 * - Legibilidad: El código es más claro y mantenible
 *
 * @param T Tipo de dato esperado en caso de éxito
 *
 * @author Sting Parra Silva
 * @version 1.0
 */
sealed class ResultadoApi<out T> {

    /**
     * Estado de éxito con datos
     *
     * Se usa cuando la petición HTTP fue exitosa (código 2xx)
     * y se recibieron datos válidos del servidor.
     *
     * @property datos Información obtenida de la API
     *
     * Ejemplo de uso:
     * ```
     * when (resultado) {
     *     is ResultadoApi.Exito -> {
     *         val productos = resultado.datos
     *         mostrarProductos(productos)
     *     }
     * }
     * ```
     */
    data class Exito<T>(val datos: T) : ResultadoApi<T>()

    /**
     * Estado de error con información del fallo
     *
     * Se usa cuando:
     * - La petición HTTP falló (código 4xx o 5xx)
     * - No hay conexión a internet
     * - Timeout de conexión
     * - Error al parsear JSON
     * - Cualquier excepción durante la operación
     *
     * @property mensajeError Descripción del error para mostrar al usuario
     * @property codigoHttp Código de estado HTTP (opcional), ej: 404, 500
     * @property excepcion Excepción original (opcional) para debugging
     *
     * Ejemplo de uso:
     * ```
     * when (resultado) {
     *     is ResultadoApi.Error -> {
     *         Log.e("API", resultado.mensajeError)
     *         mostrarMensajeError(resultado.mensajeError)
     *         if (resultado.codigoHttp == 404) {
     *             // Producto no encontrado
     *         }
     *     }
     * }
     * ```
     */
    data class Error(
        val mensajeError: String,
        val codigoHttp: Int? = null,
        val excepcion: Throwable? = null
    ) : ResultadoApi<Nothing>()

    /**
     * Estado de carga
     *
     * Se usa mientras la petición HTTP está en proceso.
     * Útil para mostrar indicadores de progreso en la UI.
     *
     * Es un object (singleton) porque no necesita datos adicionales,
     * todos los estados de carga son iguales.
     *
     * Ejemplo de uso:
     * ```
     * when (resultado) {
     *     is ResultadoApi.Cargando -> {
     *         mostrarProgressBar()
     *     }
     * }
     * ```
     */
    object Cargando : ResultadoApi<Nothing>()
}

/**
 * Función de extensión para obtener los datos o null
 *
 * Simplifica la extracción de datos cuando solo nos interesa el valor
 * y no necesitamos manejar otros estados.
 *
 * @return Datos si el estado es Exito, null en cualquier otro caso
 *
 * Ejemplo de uso:
 * ```
 * val productos = resultado.obtenerDatosONull()
 * if (productos != null) {
 *     // Usar productos
 * }
 * ```
 */
fun <T> ResultadoApi<T>.obtenerDatosONull(): T? {
    return when (this) {
        is ResultadoApi.Exito -> this.datos
        else -> null
    }
}

/**
 * Verifica si el resultado es un error
 *
 * @return true si el estado es Error, false en caso contrario
 *
 * Ejemplo de uso:
 * ```
 * if (resultado.estaEnError()) {
 *     mostrarMensajeError()
 * }
 * ```
 */
fun <T> ResultadoApi<T>.estaEnError(): Boolean {
    return this is ResultadoApi.Error
}

/**
 * Verifica si el resultado está en estado de carga
 *
 * @return true si el estado es Cargando, false en caso contrario
 *
 * Ejemplo de uso:
 * ```
 * if (resultado.estaCargando()) {
 *     mostrarProgressBar()
 * } else {
 *     ocultarProgressBar()
 * }
 * ```
 */
fun <T> ResultadoApi<T>.estaCargando(): Boolean {
    return this is ResultadoApi.Cargando
}

/**
 * Verifica si el resultado fue exitoso
 *
 * @return true si el estado es Exito, false en caso contrario
 *
 * Ejemplo de uso:
 * ```
 * if (resultado.fueExitoso()) {
 *     val datos = resultado.obtenerDatosONull()!!
 *     procesarDatos(datos)
 * }
 * ```
 */
fun <T> ResultadoApi<T>.fueExitoso(): Boolean {
    return this is ResultadoApi.Exito
}

/**
 * Ejecuta un bloque de código solo si el resultado es exitoso
 *
 * @param bloque Función que recibe los datos y no retorna nada
 *
 * Ejemplo de uso:
 * ```
 * resultado.alSerExitoso { productos ->
 *     mostrarProductos(productos)
 * }
 * ```
 */
inline fun <T> ResultadoApi<T>.alSerExitoso(bloque: (T) -> Unit) {
    if (this is ResultadoApi.Exito) {
        bloque(this.datos)
    }
}

/**
 * Ejecuta un bloque de código solo si el resultado es un error
 *
 * @param bloque Función que recibe el mensaje de error
 *
 * Ejemplo de uso:
 * ```
 * resultado.alSerError { mensaje ->
 *     Toast.makeText(context, mensaje, Toast.LENGTH_LONG).show()
 * }
 * ```
 */
inline fun <T> ResultadoApi<T>.alSerError(bloque: (String) -> Unit) {
    if (this is ResultadoApi.Error) {
        bloque(this.mensajeError)
    }
}

/**
 * Transforma los datos de un ResultadoApi exitoso
 *
 * Permite mapear el tipo de dato sin perder el estado del resultado.
 * Si el resultado es Error o Cargando, se mantiene ese estado.
 *
 * @param R Tipo de dato de salida
 * @param transformacion Función que transforma T en R
 * @return Nuevo ResultadoApi con el tipo transformado
 *
 * Ejemplo de uso:
 * ```
 * val resultadoDto: ResultadoApi<List<ProductoDto>> = ...
 * val resultadoModelo: ResultadoApi<List<Producto>> = resultadoDto.mapear { dtos ->
 *     dtos.map { it.aModelo() }
 * }
 * ```
 */
inline fun <T, R> ResultadoApi<T>.mapear(transformacion: (T) -> R): ResultadoApi<R> {
    return when (this) {
        is ResultadoApi.Exito -> ResultadoApi.Exito(transformacion(this.datos))
        is ResultadoApi.Error -> ResultadoApi.Error(this.mensajeError, this.codigoHttp, this.excepcion)
        is ResultadoApi.Cargando -> ResultadoApi.Cargando
    }
}

/**
 * Obtiene el mensaje de error o un texto por defecto
 *
 * @param mensajePorDefecto Mensaje a retornar si no hay error
 * @return Mensaje de error o el mensaje por defecto
 *
 * Ejemplo de uso:
 * ```
 * val mensaje = resultado.obtenerMensajeError("Todo correcto")
 * textView.text = mensaje
 * ```
 */
fun <T> ResultadoApi<T>.obtenerMensajeError(mensajePorDefecto: String = ""): String {
    return when (this) {
        is ResultadoApi.Error -> this.mensajeError
        else -> mensajePorDefecto
    }
}
