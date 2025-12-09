package com.example.labx.data.local

import android.content.Context
import com.example.labx.domain.model.Producto
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * ProductoInicializador: Carga productos de ejemplo en la BD
 *
 * Se ejecuta la primera vez que se abre la app
 * Permite tener datos de prueba sin conectarse a una API
 *
 * Autor: Prof. Sting Adams Parra Silva
 */
object ProductoInicializador {

    /**
     * Inserta productos de ejemplo si la base de datos está vacía
     */
    fun inicializarProductos(context: Context) {
        val database = AppDatabase.getDatabase(context)
        val productoDao = database.productoDao()

        // Ejecutar en background (no bloquear la UI)
        CoroutineScope(Dispatchers.IO).launch {
            // Solo insertar si no hay productos
            val productosExistentes = productoDao.obtenerProductoPorId(50)
            if (productosExistentes == null) {
                val productosDeEjemplo = listOf(
                    Producto(
                        id = 1,
                        nombre = "Mouse Gamer RGB",
                        descripcion = "Mouse óptico profesional con 6 botones programables, sensor de 6400 DPI y retroiluminación RGB personalizable. Ideal para gaming y trabajo.",
                        precio = 25000.0,
                        imagenUrl = "mouse_gamer",
                        categoria = "Periféricos",
                        stock = 15
                    ),
                    Producto(
                        id = 2,
                        nombre = "Teclado Mecánico",
                        descripcion = "Teclado mecánico con switches rojos, retroiluminación RGB por tecla, estructura de aluminio y reposamuñecas magnético.",
                        precio = 45000.0,
                        imagenUrl = "teclado_mecanico",
                        categoria = "Periféricos",
                        stock = 8
                    ),
                    Producto(
                        id = 3,
                        nombre = "Audífonos RGB",
                        descripcion = "Audífonos gaming over-ear con sonido envolvente 7.1, micrófono cancelación de ruido y almohadillas de espuma viscoelástica.",
                        precio = 35000.0,
                        imagenUrl = "audifonos",
                        categoria = "Audio",
                        stock = 12
                    ),
                    Producto(
                        id = 4,
                        nombre = "Webcam Full HD",
                        descripcion = "Cámara web 1080p 60fps con enfoque automático, micrófono estéreo integrado y corrección de luz baja.",
                        precio = 55000.0,
                        imagenUrl = "webcam", // Nombre del archivo en drawable/
                        categoria = "Video",
                        stock = 5
                    ),
                    Producto(
                        id = 5,
                        nombre = "Monitor 24\" 144Hz",
                        descripcion = "Monitor gaming IPS de 24 pulgadas, tasa de refresco 144Hz, tiempo de respuesta 1ms, compatible con FreeSync.",
                        precio = 180000.0,
                        imagenUrl = "monitor_p", // Nombre del archivo en drawable/
                        categoria = "Monitores",
                        stock = 3
                    ),
                    Producto(
                        id = 6,
                        nombre = "SSD NVMe 1TB",
                        descripcion = "Disco sólido NVMe Gen4 de 1TB, velocidades de lectura hasta 7000 MB/s, ideal para gaming y creación de contenido.",
                        precio = 85000.0,
                        imagenUrl = "ssd", // Nombre del archivo en drawable/
                        categoria = "Almacenamiento",
                        stock = 20
                    ),
                    Producto(
                        id = 7,
                        nombre = "Silla Gamer Ergonómica",
                        descripcion = "Silla ergonómica con soporte lumbar ajustable, reposabrazos 4D, reclinación 180°, base de aluminio y ruedas de goma.",
                        precio = 120000.0,
                        imagenUrl = "siilagamer", // Nombre del archivo en drawable/
                        categoria = "Mobiliario",
                        stock = 6
                    ),
                    Producto(
                        id = 8,
                        nombre = "Mousepad XXL",
                        descripcion = "Alfombrilla de escritorio tamaño XXL (90x40cm), superficie de tela suave, base antideslizante de goma natural.",
                        precio = 12000.0,
                        imagenUrl = "mousepad", // Nombre del archivo en drawable/
                        categoria = "Accesorios",
                        stock = 25
                    ),
                    Producto(
                        id = 9,
                        nombre = "Control Inalámbrico Pro",
                        descripcion = "Control inalámbrico con vibración háptica avanzada, conectividad Bluetooth 5.0 y batería de larga duración.",
                        precio = 49990.0,
                        imagenUrl = "controlpro",
                        categoria = "Accesorios",
                        stock = 18
                    ),
                    Producto(
                        id = 10,
                        nombre = "Luz LED RGB Ambiente",
                        descripcion = "Tira LED RGB de 5 metros con control remoto, modos dinámicos y sincronización con música.",
                        precio = 15990.0,
                        imagenUrl = "luz_led",
                        categoria = "Accesorios",
                        stock = 30
                    ),
                    Producto(
                        id = 11,
                        nombre = "Parlantes Gamer 2.1",
                        descripcion = "Sistema de sonido gamer 2.1 con subwoofer, RGB dinámico y sonido envolvente.",
                        precio = 39990.0,
                        imagenUrl = "parlante",
                        categoria = "Audio",
                        stock = 10
                    ),
                    Producto(
                        id = 12,
                        nombre = "Micrófono Profesional USB",
                        descripcion = "Micrófono condensador con patrón cardioide, filtro antipop y brazo articulado incluido.",
                        precio = 55000.0,
                        imagenUrl = "microfono",
                        categoria = "Audio",
                        stock = 14
                    ),
                    Producto(
                        id = 13,
                        nombre = "Capturadora de Video 4K",
                        descripcion = "Tarjeta de captura 4K 60fps ideal para streaming, compatible con OBS, PS5, Xbox y PC.",
                        precio = 140000.0,
                        imagenUrl = "capturadoravideo",
                        categoria = "Video",
                        stock = 4
                    ),
                    Producto(
                        id = 14,
                        nombre = "Router Gamer WiFi 6",
                        descripcion = "Router dual-band con WiFi 6, QoS para juegos, puertos 2.5Gbps y antenas de alto rendimiento.",
                        precio = 90000.0,
                        imagenUrl = "router",
                        categoria = "Red",
                        stock = 7
                    ),
                    Producto(
                        id = 15,
                        nombre = "Lámpara Gamer RGB 3D",
                        descripcion = "Lámpara decorativa 3D con iluminación RGB, efectos dinámicos, control táctil y base acrílica.",
                        precio = 28000.0,
                        imagenUrl = "lampara",
                        categoria = "Decoración",
                        stock = 15
                    ),

                    Producto(
                        id = 16,
                        nombre = "PlayStation 5 Control Camo",
                        descripcion = "Control PS5 edición Camo con gatillos adaptativos y vibración inmersiva.",
                        precio = 79990.0,
                        imagenUrl = "play5",
                        categoria = "Consolas",
                        stock = 9
                    ),
                    Producto(
                        id = 17,
                        nombre = "Xbox Series X Mini Fridge",
                        descripcion = "Mini refrigerador oficial Xbox con capacidad para 10 latas y luz verde interna.",
                        precio = 69000.0,
                        imagenUrl = "xbox",
                        categoria = "Consolas",
                        stock = 5
                    ),
                    Producto(
                        id = 18,
                        nombre = "Silla Gamer Premium XL",
                        descripcion = "Silla premium con memory foam, reposapiés, inclinación 180° y estructura metálica reforzada.",
                        precio = 185000.0,
                        imagenUrl = "silla_pro",
                        categoria = "Mobiliario",
                        stock = 3
                    ),
                    Producto(
                        id = 19,
                        nombre = "Case ATX RGB Cristal Templado",
                        descripcion = "Gabinete ATX con paneles de cristal templado, 4 ventiladores RGB incluidos y soporte watercooling.",
                        precio = 67000.0,
                        imagenUrl = "case_vidrio",
                        categoria = "Componentes",
                        stock = 11
                    ),
                    Producto(
                        id = 20,
                        nombre = "Tarjeta Gráfica RTX 3060",
                        descripcion = "GPU NVIDIA RTX 3060 con 12GB GDDR6, ideal para gaming 1080p y streaming.",
                        precio = 320000.0,
                        imagenUrl = "tarjetagrafica",
                        categoria = "Componentes",
                        stock = 2
                    ),
                    Producto(
                        id = 21,
                        nombre = "Hub USB 7 Puertos RGB",
                        descripcion = "Hub USB 3.0 con 7 puertos de alta velocidad e iluminación RGB personalizable.",
                        precio = 19990.0,
                        imagenUrl = "hub",
                        categoria = "Accesorios",
                        stock = 21
                    ),
                    Producto(
                        id = 22,
                        nombre = "Teclado 60% Compacto RGB",
                        descripcion = "Teclado compacto 60% con switches rojos, iluminación RGB y modo gaming.",
                        precio = 43000.0,
                        imagenUrl = "teclado",
                        categoria = "Periféricos",
                        stock = 13
                    ),
                    Producto(
                        id = 23,
                        nombre = "Volante Gamer Force Feedback",
                        descripcion = "Volante con pedales, vibración realista y giro de 900°, compatible con PC y consolas.",
                        precio = 160000.0,
                        imagenUrl = "volante",
                        categoria = "Consolas",
                        stock = 4
                    ),
                    Producto(
                        id = 24,
                        nombre = "SSD SATA 1TB UltraSpeed",
                        descripcion = "SSD de 1TB con lectura de 550MB/s y alta durabilidad.",
                        precio = 55000.0,
                        imagenUrl = "ssd_sata1tb",
                        categoria = "Almacenamiento",
                        stock = 12
                    ),

                    Producto(
                        id = 25,
                        nombre = "HDD 2TB 7200RPM",
                        descripcion = "Disco duro de 2TB con alto rendimiento y fiabilidad.",
                        precio = 48000.0,
                        imagenUrl = "hdd_2tb",
                        categoria = "Almacenamiento",
                        stock = 20
                    ),

                    Producto(
                        id = 26,
                        nombre = "Memoria USB 128GB 3.2",
                        descripcion = "Pendrive 128GB USB 3.2 de alta velocidad.",
                        precio = 15000.0,
                        imagenUrl = "usb_128gb",
                        categoria = "Almacenamiento",
                        stock = 35
                    ),

                    Producto(
                        id = 27,
                        nombre = "SSD Externo 1TB Portable",
                        descripcion = "SSD portátil resistente a impactos y con 1050MB/s.",
                        precio = 89000.0,
                        imagenUrl = "ssd_externo1tb",
                        categoria = "Almacenamiento",
                        stock = 10
                    ),

                    Producto(
                        id = 28,
                        nombre = "Tarjeta SD 256GB Pro",
                        descripcion = "Tarjeta SD de alta velocidad ideal para grabación 4K.",
                        precio = 38000.0,
                        imagenUrl = "sd_256gb",
                        categoria = "Almacenamiento",
                        stock = 25
                    ),
                    Producto(
                        id = 29,
                        nombre = "Monitor 32'' 4K IPS",
                        descripcion = "Pantalla UHD 4K con panel IPS y soporte HDR10.",
                        precio = 260000.0,
                        imagenUrl = "monitor_4k32",
                        categoria = "Monitores",
                        stock = 6
                    ),

                    Producto(
                        id = 30,
                        nombre = "Monitor UltraWide 34'' 144Hz",
                        descripcion = "Pantalla curva de 34'' con formato 21:9 y 144Hz.",
                        precio = 340000.0,
                        imagenUrl = "monitor_ultrawide34",
                        categoria = "Monitores",
                        stock = 4
                    ),

                    Producto(
                        id = 31,
                        nombre = "Monitor 24'' FullHD 75Hz",
                        descripcion = "Monitor económico con panel IPS y 75Hz.",
                        precio = 90000.0,
                        imagenUrl = "monitor_24fhd",
                        categoria = "Monitores",
                        stock = 15
                    ),

                    Producto(
                        id = 32,
                        nombre = "Monitor Curvo 32'' 165Hz",
                        descripcion = "Monitor gaming curvo de 165Hz con 1ms.",
                        precio = 220000.0,
                        imagenUrl = "monitor_curvo32",
                        categoria = "Monitores",
                        stock = 5
                    ),

                    Producto(
                        id = 33,
                        nombre = "Monitor Portátil 15.6'' USB-C",
                        descripcion = "Pantalla portátil ideal para trabajar con doble monitor.",
                        precio = 140000.0,
                        imagenUrl = "monitor_portatil",
                        categoria = "Monitores",
                        stock = 8
                    ),
                    Producto(
                        id = 34,
                        nombre = "PlayStation 5 Digital",
                        descripcion = "Consola PS5 edición digital con SSD ultrarrápido.",
                        precio = 550000.0,
                        imagenUrl = "ps5_digital",
                        categoria = "Consolas",
                        stock = 3
                    ),

                    Producto(
                        id = 35,
                        nombre = "Xbox Series S",
                        descripcion = "Consola compacta con 512GB SSD y hasta 120 FPS.",
                        precio = 350000.0,
                        imagenUrl = "xbox_series_s",
                        categoria = "Consolas",
                        stock = 5
                    ),

                    Producto(
                        id = 36,
                        nombre = "Nintendo Switch OLED",
                        descripcion = "Versión OLED con pantalla de colores vibrantes.",
                        precio = 420000.0,
                        imagenUrl = "switch_oled",
                        categoria = "Consolas",
                        stock = 7
                    ),

                    Producto(
                        id = 37,
                        nombre = "Control PS5 DualSense Negro",
                        descripcion = "Control con vibración háptica y gatillos adaptativos.",
                        precio = 78000.0,
                        imagenUrl = "dualsense_negro",
                        categoria = "Consolas",
                        stock = 14
                    ),
                    Producto(
                        id = 38,
                        nombre = "Router WiFi 6 Gaming AX3000",
                        descripcion = "Router con baja latencia y 6 antenas de alta potencia.",
                        precio = 115000.0,
                        imagenUrl = "router_ax3000",
                        categoria = "Red",
                        stock = 6
                    ),

                    Producto(
                        id = 39,
                        nombre = "Switch Gigabit 8 Puertos",
                        descripcion = "Switch Ethernet metálico con 8 puertos Gigabit.",
                        precio = 35000.0,
                        imagenUrl = "switch_8p",
                        categoria = "Red",
                        stock = 12
                    ),

                    Producto(
                        id = 40,
                        nombre = "Adaptador USB WiFi 6",
                        descripcion = "Adaptador WiFi de alta velocidad AX1800 para PC.",
                        precio = 22000.0,
                        imagenUrl = "adaptador_wifi6",
                        categoria = "Red",
                        stock = 25
                    ),
                    Producto(
                        id = 41,
                        nombre = "Luz LED RGB para Streaming",
                        descripcion = "Panel LED RGB con control remoto y brillo regulable.",
                        precio = 29000.0,
                        imagenUrl = "luz_streaming",
                        categoria = "Video",
                        stock = 18
                    ),

                    Producto(
                        id = 42,
                        nombre = "Proyector Full HD 1080p",
                        descripcion = "Proyector portátil con 3000 lúmenes y entrada HDMI.",
                        precio = 185000.0,
                        imagenUrl = "proyector_hd",
                        categoria = "Video",
                        stock = 4
                    ),

                    Producto(
                        id = 43,
                        nombre = "Capturadora 4K HDR External",
                        descripcion = "Capturadora externa con soporte 4K a 60FPS HDR.",
                        precio = 195000.0,
                        imagenUrl = "capturadora_4k",
                        categoria = "Video",
                        stock = 3
                    ),


                    )

                // Insertar en la base de datos
                productoDao.insertarProductos(productosDeEjemplo.map { it.toEntity() })
            }
        }
    }
}

// Extension function para convertir Producto a ProductoEntity
private fun Producto.toEntity() = com.example.labx.data.local.entity.ProductoEntity(
    id = id,
    nombre = nombre,
    descripcion = descripcion,
    precio = precio,
    imagenUrl = imagenUrl,
    categoria = categoria,
    stock = stock
)
