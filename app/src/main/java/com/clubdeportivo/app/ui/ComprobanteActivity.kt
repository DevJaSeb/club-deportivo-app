package com.clubdeportivo.app.ui

import android.content.Intent
import android.graphics.pdf.PdfDocument
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.clubdeportivo.app.R
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class ComprobanteActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comprobante)

        val btnFinalizar = findViewById<Button>(R.id.btn_finalizar)
        val btnVerCarnet = findViewById<Button>(R.id.btn_carnet)

        // Datos recibidos
        val nombre = intent.getStringExtra("nombre") ?: ""
        val apellido = intent.getStringExtra("apellido") ?: ""
        val dni = intent.getStringExtra("dni") ?: ""
        val idSocio = intent.getStringExtra("idSocio") ?: ""
        val idNoSocio= intent.getStringExtra("idNoSocio") ?: ""
        val vencimiento = intent.getStringExtra("vencimiento") ?: ""
        val formaPago = intent.getStringExtra("formaDePago") ?: ""
        val monto = intent.getDoubleExtra("monto", 0.0)
        val actividad = intent.getStringExtra("actividad") ?: ""
        val fecha = intent.getStringExtra("fecha") ?: ""

        // TextView / Layouts a cambiar
        val tvMonto = findViewById<TextView>(R.id.tv_monto)
        val tvNombreApellido = findViewById<TextView>(R.id.tv_nombre_apellido)
        val tvTipoSocio = findViewById<TextView>(R.id.tv_tipo_socio)
        val tvId = findViewById<TextView>(R.id.tv_id)
        val tvVencimiento = findViewById<TextView>(R.id.tv_vencimiento)
        val tvFormaPago = findViewById<TextView>(R.id.tv_forma_pago)
        val tvActividad = findViewById<TextView>(R.id.tv_actividad)
        val actividadContenedor = findViewById<LinearLayout>(R.id.actividad_contenedor)
        val tvFecha = findViewById<TextView>(R.id.tv_fecha)

        // Contenedor de datos y boton
        val contenedorRecibo = findViewById<View>(R.id.contenedor_pdf_comprobante)
        val btnImprimirRecibo = findViewById<Button>(R.id.btn_imprimir_comprobante)

        // Reemplazamos la información
        tvNombreApellido.text = "${apellido}, ${nombre}"
        tvMonto.text = "$" + monto
        tvFormaPago.text = formaPago

        // Si es No Socio, aparecen las actividades y tvs correspondientes, mostrar botón ver carnet o finalizar
        if (idNoSocio != "") {
            btnVerCarnet.visibility = View.GONE
            btnFinalizar.visibility = View.VISIBLE
            actividadContenedor.visibility = View.VISIBLE
            tvActividad.text = actividad
            tvId.text = idNoSocio.padStart(3, '0')// asi se ve 001, 002, etc
            tvTipoSocio.text = "Nº de No-Socio"
            tvFecha.text = "Fecha válida"
            tvVencimiento.text = fecha

        } else {
            btnVerCarnet.visibility = View.VISIBLE
            btnFinalizar.visibility = View.GONE
            tvId.text = idSocio.padStart(3, '0') // asi se ve 001, 002, etc
            tvVencimiento.text = vencimiento
        }


        // Vuelve al Menú
        btnFinalizar.setOnClickListener {
            val intent = Intent(this, MenuActivity::class.java)
            startActivity(intent)
        }

        // Ir a Carnet - SOCIO
       btnVerCarnet.setOnClickListener {
            val intent = Intent(this, CarnetActivity::class.java).apply{
                putExtra("nombre", nombre)
                putExtra("apellido", apellido)
                putExtra("dni", dni)
                putExtra("idSocio", idSocio)
                putExtra("vencimiento", vencimiento)
            }
            startActivity(intent)
            finish()
        }
        btnImprimirRecibo.setOnClickListener {
            exportarYCompartirPdf(contenedorRecibo, "Recibo_Pago_$dni")
        }

        // Finalizar: ir al menú - NO SOCIO
        btnFinalizar.setOnClickListener {
            val intent = Intent(this, MenuActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    // Funcion para imprimir y compartir
    private fun exportarYCompartirPdf(vistaContenedor: View, nombreDelArchivo: String) {
        val pdfDocument = PdfDocument()

        // Hoja en blanco con las medidas del diseño
        val pageInfo = PdfDocument.PageInfo.Builder(vistaContenedor.width, vistaContenedor.height, 1).create()
        val page = pdfDocument.startPage(pageInfo)

        // Sacamos la "foto" vectorial al diseño y la pegamos en la hoja
        vistaContenedor.draw(page.canvas)
        pdfDocument.finishPage(page)

        // Carpeta temporal (caché)
        val carpetaCache = File(cacheDir, "comprobantes")
        if (!carpetaCache.exists()) carpetaCache.mkdirs()
        val archivoPdf = File(carpetaCache, "$nombreDelArchivo.pdf")

        // Guardamos el archivo físico
        try {
            val outputStream = FileOutputStream(archivoPdf)
            pdfDocument.writeTo(outputStream)
            outputStream.flush()
            outputStream.close()
        } catch (e: IOException) {
            Toast.makeText(this, "Error al generar el PDF", Toast.LENGTH_SHORT).show()
            pdfDocument.close()
            return
        }
        pdfDocument.close()

        // Usamos el FileProvider
        val uriSegura = FileProvider.getUriForFile(this, "com.clubdeportivo.app.fileprovider", archivoPdf)

        val intentCompartir = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uriSegura)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION) // Permiso temporal
        }

        // Menú para imprimir y compartir el archivo
        startActivity(Intent.createChooser(intentCompartir, "Compartir carnet vía:"))
    }

}