package com.clubdeportivo.app.ui

import android.content.Intent
import android.graphics.pdf.PdfDocument
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.clubdeportivo.app.R
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class CarnetActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_carnet)

        val btnFinalizar = findViewById<Button>(R.id.btn_finalizar)

        // Datos recibidos
        val nombre = intent.getStringExtra("nombre") ?: ""
        val apellido = intent.getStringExtra("apellido") ?: ""
        val dni = intent.getStringExtra("dni") ?: ""
        val idSocio = intent.getStringExtra("idSocio") ?: ""
        val vencimiento = intent.getStringExtra("vencimiento") ?: ""


        // TextView / Layouts a cambiar
        val tvNombreApellido = findViewById<TextView>(R.id.tv_nombre_apellido)
        val tvDni = findViewById<TextView>(R.id.tv_dni)
        val tvId = findViewById<TextView>(R.id.tv_id)
        val tvVencimiento = findViewById<TextView>(R.id.tv_vencimiento)
        // Contenedor de Datos y boton
        val contenedorCarnet = findViewById<View>(R.id.contenedor_pdf)
        val btnImprimir = findViewById<Button>(R.id.btn_imprimir_carnet)

        // Reemplazamos los datos
        tvNombreApellido.text = "${apellido}, ${nombre}"
        tvDni.text = dni
        tvId.text = idSocio.padStart(3, '0') // asi se ve 001, 002, etc
        tvVencimiento.text = vencimiento


        // Boton para imprimir y compartir
        btnImprimir.setOnClickListener {
            exportarYCompartirPdf(contenedorCarnet, "Carnet_Socio_$dni")
        }
        btnFinalizar.setOnClickListener {
            val intent = Intent(this, MenuActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
        }
    }

    // Funcion para compartir el pdf
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