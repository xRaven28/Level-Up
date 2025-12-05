package com.example.labx.data.remote.dto

import com.example.labx.domain.model.Producto
import org.junit.Assert.*
import org.junit.Test


/**
 * Pruebas unitarias para ProductoDto y extension functions
 * 
 * Qué se prueba:
 * - Mapeo correcto de DTO a modelo de dominio
 * - Mapeo correcto de modelo de dominio a DTO
 * - Manejo de valores por defecto
 * - Preservación de datos durante conversión
 * - Casos límite y especiales
 * 
 * Frameworks usados:
 * - JUnit 5: Ejecución de pruebas
 * - Assert: Verificaciones de resultados
 * 
 * @author Sting Parra Silva
 */
class ProductoDtoTest {

    @Test
    fun `aModelo - convierte correctamente DTO a Producto`() {
        // Given: DTO con datos completos
        val dto = ProductoDto(
            identificador = 1,
            titulo = "Monitor Gaming",
            descripcion = "Monitor 27 pulgadas 144Hz",
            precio = 250000.0,
            urlImagen = "https://ejemplo.com/monitor.jpg",
            categoria = "Monitores"
        )

        // When: Convertir a modelo de dominio
        val producto = dto.aModelo()

        // Then: Verificar mapeo correcto
        assertEquals(1, producto.id, "ID debe coincidir")
        assertEquals("Monitor Gaming", producto.nombre, "Nombre debe coincidir")
        assertEquals("Monitor 27 pulgadas 144Hz", producto.descripcion, "Descripción debe coincidir")
        assertEquals(250000.0, producto.precio, 0.001, "Precio debe coincidir")
        assertEquals("https://ejemplo.com/monitor.jpg", producto.imagenUrl, "URL de imagen debe coincidir")
        assertEquals("Monitores", producto.categoria, "Categoría debe coincidir")
        assertEquals(10, producto.stock, "Stock debe ser valor por defecto (10)")
    }

    @Test
    fun `aModelo - maneja valores nulos o vacíos correctamente`() {
        // Given: DTO con valores de borde
        val dto = ProductoDto(
            identificador = 0,
            titulo = "",
            descripcion = "",
            precio = 0.0,
            urlImagen = "",
            categoria = ""
        )

        // When: Convertir a modelo de dominio
        val producto = dto.aModelo()

        // Then: Verificar que mantiene los valores
        assertEquals(0, producto.id, "ID cero debe mantenerse")
        assertEquals("", producto.nombre, "Nombre vacío debe mantenerse")
        assertEquals("", producto.descripcion, "Descripción vacía debe mantenerse")
        assertEquals(0.0, producto.precio, 0.001, "Precio cero debe mantenerse")
        assertEquals("", producto.imagenUrl, "URL vacía debe mantenerse")
        assertEquals("", producto.categoria, "Categoría vacía debe mantenerse")
        assertEquals(10, producto.stock, "Stock siempre debe ser 10 por defecto")
    }

    @Test
    fun `aModelo - convierte decimales correctamente`() {
        // Given: DTO con precios decimales
        val dto = ProductoDto(
            identificador = 1,
            titulo = "Producto con decimales",
            descripcion = "Test decimales",
            precio = 99.99,
            urlImagen = "http://test.com/img.jpg",
            categoria = "Test"
        )

        // When: Convertir a modelo de dominio
        val producto = dto.aModelo()

        // Then: Verificar precisión del decimal
        assertEquals(99.99, producto.precio, 0.0001, "Precio decimal debe mantener precisión")
    }

    @Test
    fun `aModelo - IDs negativos se mantienen`() {
        // Given: DTO con ID negativo (caso borde)
        val dto = ProductoDto(
            identificador = -1,
            titulo = "Producto con ID negativo",
            descripcion = "Test ID negativo",
            precio = 100.0,
            urlImagen = "http://test.com/img.jpg",
            categoria = "Test"
        )

        // When: Convertir a modelo de dominio
        val producto = dto.aModelo()

        // Then: Verificar que mantiene el ID negativo
        assertEquals(-1, producto.id, "ID negativo debe mantenerse")
    }

    @Test
    fun `aDto - convierte correctamente Producto a DTO`() {
        // Given: Producto con datos completos
        val producto = Producto(
            id = 1,
            nombre = "Mouse Gamer",
            descripcion = "Mouse RGB 16000 DPI",
            precio = 45000.0,
            imagenUrl = "https://ejemplo.com/mouse.jpg",
            categoria = "Periféricos",
            stock = 15
        )

        // When: Convertir a DTO
        val dto = producto.aDto()

        // Then: Verificar mapeo correcto
        assertEquals(1, dto.identificador, "identificador debe coincidir")
        assertEquals("Mouse Gamer", dto.titulo, "titulo debe coincidir")
        assertEquals("Mouse RGB 16000 DPI", dto.descripcion, "descripción debe coincidir")
        assertEquals(45000.0, dto.precio, 0.001, "precio debe coincidir")
        assertEquals("https://ejemplo.com/mouse.jpg", dto.urlImagen, "urlImagen debe coincidir")
        assertEquals("Periféricos", dto.categoria, "categoria debe coincidir")
    }

    @Test
    fun `aDto - ignora stock del Producto`() {
        // Given: Producto con stock específico
        val producto = Producto(
            id = 1,
            nombre = "Teclado Mecánico",
            descripcion = "Teclado RGB mecánico",
            precio = 80000.0,
            imagenUrl = "https://ejemplo.com/teclado.jpg",
            categoria = "Periféricos",
            stock = 25 // Este valor no debe aparecer en el DTO
        )

        // When: Convertir a DTO
        val dto = producto.aDto()

        // Then: Verificar que DTO no tiene campo stock
        // El DTO no tiene campo stock, así que verificamos que los demás campos coincidan
        assertEquals(1, dto.identificador)
        assertEquals("Teclado Mecánico", dto.titulo)
        assertEquals("Teclado RGB mecánico", dto.descripcion)
        assertEquals(80000.0, dto.precio, 0.001)
        assertEquals("https://ejemplo.com/teclado.jpg", dto.urlImagen)
        assertEquals("Periféricos", dto.categoria)
    }

    @Test
    fun `conversión doble - Producto a DTO y vuelta a Producto mantiene datos`() {
        // Given: Producto original
        val productoOriginal = Producto(
            id = 1,
            nombre = "Monitor Curvo",
            descripcion = "Monitor 34 pulgadas ultra-wide",
            precio = 500000.0,
            imagenUrl = "https://ejemplo.com/monitor-curvo.jpg",
            categoria = "Monitores",
            stock = 5
        )

        // When: Convertir Producto → DTO → Producto
        val dto = productoOriginal.aDto()
        val productoFinal = dto.aModelo()

        // Then: Verificar que los datos se mantienen (excepto stock)
        assertEquals(productoOriginal.id, productoFinal.id)
        assertEquals(productoOriginal.nombre, productoFinal.nombre)
        assertEquals(productoOriginal.descripcion, productoFinal.descripcion)
        assertEquals(productoOriginal.precio, productoFinal.precio, 0.001)
        assertEquals(productoOriginal.imagenUrl, productoFinal.imagenUrl)
        assertEquals(productoOriginal.categoria, productoFinal.categoria)
        
        // El stock siempre será 10 después de la conversión doble
        assertEquals(10, productoFinal.stock)
        assertNotEquals(productoOriginal.stock, productoFinal.stock, "Stock no se mantiene en conversión doble")
    }

    @Test
    fun `conversión doble - DTO a Producto y vuelta a DTO mantiene datos`() {
        // Given: DTO original
        val dtoOriginal = ProductoDto(
            identificador = 2,
            titulo = "Auriculares Gaming",
            descripcion = "Auriculares con micrófono 7.1",
            precio = 35000.0,
            urlImagen = "https://ejemplo.com/auriculares.jpg",
            categoria = "Audio"
        )

        // When: Convertir DTO → Producto → DTO
        val producto = dtoOriginal.aModelo()
        val dtoFinal = producto.aDto()

        // Then: Verificar que todos los datos se mantienen
        assertEquals(dtoOriginal.identificador, dtoFinal.identificador)
        assertEquals(dtoOriginal.titulo, dtoFinal.titulo)
        assertEquals(dtoOriginal.descripcion, dtoFinal.descripcion)
        assertEquals(dtoOriginal.precio, dtoFinal.precio, 0.001)
        assertEquals(dtoOriginal.urlImagen, dtoFinal.urlImagen)
        assertEquals(dtoOriginal.categoria, dtoFinal.categoria)
    }

    @Test
    fun `aModelo - caracteres especiales se mantienen correctamente`() {
        // Given: DTO con caracteres especiales y acentos
        val dto = ProductoDto(
            identificador = 1,
            titulo = "Silla Gaming Ergonómica",
            descripcion = "Silla reclinable con soporte lumbar",
            precio = 150000.0,
            urlImagen = "https://ejemplo.com/silla-gaming.jpg",
            categoria = "Muebles"
        )

        // When: Convertir a modelo de dominio
        val producto = dto.aModelo()

        // Then: Verificar que los caracteres especiales se mantienen
        assertEquals("Silla Gaming Ergonómica", producto.nombre)
        assertEquals("Silla reclinable con soporte lumbar", producto.descripcion)
        assertEquals("Muebles", producto.categoria)
    }

    @Test
    fun `aDto - caracteres especiales se mantienen correctamente`() {
        // Given: Producto con caracteres especiales
        val producto = Producto(
            id = 1,
            nombre = "Mousepad XXL",
            descripcion = "Alfombrilla grande para gaming",
            precio = 12000.0,
            imagenUrl = "https://ejemplo.com/mousepad.jpg",
            categoria = "Accesorios",
            stock = 20
        )

        // When: Convertir a DTO
        val dto = producto.aDto()

        // Then: Verificar que los caracteres especiales se mantienen
        assertEquals("Mousepad XXL", dto.titulo)
        assertEquals("Alfombrilla grande para gaming", dto.descripcion)
        assertEquals("Accesorios", dto.categoria)
    }

    @Test
    fun `aModelo - valores muy grandes se mantienen`() {
        // Given: DTO con precio muy grande
        val dto = ProductoDto(
            identificador = 1,
            titulo = "Producto Premium",
            descripcion = "Producto con precio muy alto",
            precio = Double.MAX_VALUE,
            urlImagen = "https://ejemplo.com/premium.jpg",
            categoria = "Premium"
        )

        // When: Convertir a modelo de dominio
        val producto = dto.aModelo()

        // Then: Verificar que mantiene el valor grande
        assertEquals(Double.MAX_VALUE, producto.precio, 0.0, "Precio muy grande debe mantenerse")
    }

    @Test
    fun `aDto - valores muy grandes se mantienen`() {
        // Given: Producto con precio muy grande
        val producto = Producto(
            id = 1,
            nombre = "Producto Premium",
            descripcion = "Producto con precio muy alto",
            precio = Double.MAX_VALUE,
            imagenUrl = "https://ejemplo.com/premium.jpg",
            categoria = "Premium",
            stock = 1
        )

        // When: Convertir a DTO
        val dto = producto.aDto()

        // Then: Verificar que mantiene el valor grande
        assertEquals(Double.MAX_VALUE, dto.precio, 0.0, "Precio muy grande debe mantenerse")
    }
}
