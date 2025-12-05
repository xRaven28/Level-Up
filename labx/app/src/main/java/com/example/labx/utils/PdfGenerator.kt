package com.example.labx.utils

import android.content.Context
import android.print.PrintAttributes
import android.print.PrintManager
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import com.example.labx.ui.viewmodel.DatosOrden
import com.example.labx.ui.screen.formatearPrecio


object PdfGenerator {

    fun generarBoletaPdf(context: Context, orden: DatosOrden) {
        val htmlContent = """
            <html>
            <head>
                <style>
                    body { font-family: Helvetica, Arial, sans-serif; padding: 20px; color: #333; }
                    .header { text-align: center; margin-bottom: 30px; border-bottom: 2px solid #333; padding-bottom: 20px; }
                    .header h1 { margin: 0; font-size: 24px; text-transform: uppercase; }
                    .header p { margin: 5px 0; font-size: 14px; }
                    .datos-cliente { margin-bottom: 20px; padding: 15px; background-color: #f9f9f9; border: 1px solid #ddd; }
                    .datos-cliente h3 { margin-top: 0; border-bottom: 1px solid #ccc; padding-bottom: 10px; }
                    .tabla-items { width: 100%; border-collapse: collapse; margin-bottom: 30px; }
                    .tabla-items th { background-color: #f0f0f0; text-align: left; padding: 10px; border-bottom: 2px solid #ccc; }
                    .tabla-items td { padding: 10px; border-bottom: 1px solid #eee; }
                    .totales { float: right; width: 40%; }
                    .fila-total { display: flex; justify-content: space-between; padding: 5px 0; }
                    .total-final { font-weight: bold; font-size: 18px; border-top: 2px solid #333; padding-top: 10px; }
                    .footer { clear: both; text-align: center; margin-top: 50px; font-size: 12px; color: #777; border-top: 1px solid #ccc; padding-top: 20px; }
                    .timbre { text-align: center; margin-top: 30px; border: 3px double #333; padding: 10px; width: 200px; margin-left: auto; margin-right: auto; font-weight: bold; text-transform: uppercase; }
                </style>
            </head>
            <body>
                <div class="header">
                    <h1>Level-Up Store Ltda.</h1>
                    <p>RUT: 77.123.456-7</p>
                    <p>Giro: Venta de Artículos Electrónicos y Computación</p>
                    <p>Casa Matriz: Av. Siempre Viva 742, Santiago</p>
                    <br>
                    <h2>BOLETA ELECTRÓNICA N° ${orden.idTransaccion.takeLast(6)}</h2>
                    <p>Fecha Emisión: ${orden.fecha}</p>
                </div>

                <div class="datos-cliente">
                    <h3>Datos del Cliente</h3>
                    <p><strong>Señor(a):</strong> ${orden.cliente}</p>
                    <p><strong>Dirección:</strong> ${orden.direccion}</p>
                    <p><strong>Método de Pago:</strong> ${orden.metodoPago}</p>
                </div>

                <table class="tabla-items">
                    <thead>
                        <tr>
                            <th>Cant.</th>
                            <th>Descripción</th>
                            <th>Precio Unit.</th>
                            <th>Subtotal</th>
                        </tr>
                    </thead>
                    <tbody>
                        ${
            orden.items.joinToString("") { item ->
                """<tr>
                                <td style="text-align: center;">${item.cantidad}</td>
                                <td>${item.producto.nombre}</td>
                                <td style="text-align: right;">${formatearPrecio(item.producto.precio)}</td>
                                <td style="text-align: right;">${formatearPrecio(item.subtotal)}</td>
                            </tr>"""
            }
        }
                    </tbody>
                </table>

                <div class="totales">
                    <div class="fila-total">
                        <span>Subtotal:</span>
                        <span>${formatearPrecio(orden.subtotal)}</span>
                    </div>
                    ${
            if (orden.montoDescuento > 0) """
                    <div class="fila-total" style="color: #4CAF50;">
                        <span>Descuento Duoc:</span>
                        <span>-${formatearPrecio(orden.montoDescuento)}</span>
                    </div>
                    """ else ""
        }
                    <div class="fila-total">
                        <span>${formatearPrecio(orden.totalFinal)}</span>
                    </div>
                    <div class="fila-total total-final">
                        <span>TOTAL A PAGAR:</span>
                        <span>${formatearPrecio(orden.totalFinal)}</span>
                    </div>
                </div>
                
                <br><br><br>
                <div class="timbre">
                    Timbre Electrónico <br>
                    Res. 99 de 2024<br>
                </div>

                <div class="footer">
                    <p>¡Gracias por comprar en Level-Up!</p>
                    <p>Este documento es una representación impresa de una Boleta Electrónica.</p>
                </div>
            </body>
            </html>
        """

        val webView = WebView(context)
        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {
                // Una vez cargado el HTML, iniciamos el proceso de impresión
                crearPdfDesdeWebView(context, view, "Boleta_LevelUp_${orden.idTransaccion}")

            }
        }
        webView.loadDataWithBaseURL(null, htmlContent, "text/HTML", "UTF-8", null)
    }

    private fun crearPdfDesdeWebView(context: Context, webView: WebView, nombreArchivo: String) {
        val printManager = context.getSystemService(Context.PRINT_SERVICE) as PrintManager
        val printAdapter = webView.createPrintDocumentAdapter(nombreArchivo)

        val jobName = "LevelUp Documento - $nombreArchivo"

        val builder = PrintAttributes.Builder()
        builder.setMediaSize(PrintAttributes.MediaSize.ISO_A4)

        printManager.print(jobName, printAdapter, builder.build())

        Toast.makeText(context, "Generando PDF... Selecciona 'Guardar como PDF'", Toast.LENGTH_LONG)
            .show()
    }
}