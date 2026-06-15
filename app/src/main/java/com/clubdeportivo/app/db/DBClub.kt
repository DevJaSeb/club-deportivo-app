package com.clubdeportivo.app.db

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.clubdeportivo.app.models.UsuarioActivo
import com.clubdeportivo.app.models.Vencimiento
import java.text.SimpleDateFormat
import java.util.Locale

class DBClub (context: Context) : SQLiteOpenHelper(context, "ClubDeportivo.db", null, 1) {

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(CREATE_USUARIO_TABLE)
        db?.execSQL(CREATE_PERSONA_TABLE)
        db?.execSQL(CREATE_SOCIO_TABLE)
        db?.execSQL(CREATE_NOSOCIO_TABLE)
        db?.execSQL(CREATE_ACTIVIDAD_TABLE)
        db?.execSQL(CREATE_CUOTA_MENSUAL_TABLE)
        db?.execSQL(CREATE_CUOTA_DIARIA_TABLE)

        // Insertamos datos de prueba para poder hacer login
        db?.execSQL("INSERT INTO usuario (usuario, clave) VALUES ('admin', 'admin123')")

        // Cargar Actividades para el selector de actividades
        val actividadesIniciales =
            listOf("Natación", "Elongación", "Musculación", "Artes Marciales", "Tenis", "Yoga")
        for (act in actividadesIniciales) {
            db?.execSQL("INSERT INTO actividad (nombreActividad) VALUES ('$act')")
        }
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS cuotadiaria")
        db?.execSQL("DROP TABLE IF EXISTS cuotamensual")
        db?.execSQL("DROP TABLE IF EXISTS actividad")
        db?.execSQL("DROP TABLE IF EXISTS nosocio")
        db?.execSQL("DROP TABLE IF EXISTS socio")
        db?.execSQL("DROP TABLE IF EXISTS persona")
        db?.execSQL("DROP TABLE IF EXISTS usuario")

        onCreate(db)
    }

    companion object {
        private const val CREATE_USUARIO_TABLE = "CREATE TABLE usuario (" +
                "idUsuario INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "usuario TEXT UNIQUE, " +
                "clave TEXT)"

        private const val CREATE_PERSONA_TABLE = "CREATE TABLE persona (" +
                "idPersona INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "nombre TEXT, " +
                "apellido TEXT, " +
                "dni TEXT, " +
                "telefono TEXT, " +
                "direccion TEXT, " +
                "email TEXT, " +
                "fichaMedica INTEGER, " +
                "fechaInscripcion TEXT)"

        private const val CREATE_SOCIO_TABLE = "CREATE TABLE socio (" +
                "idSocio INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "idPersona INTEGER, " +
                "fechaVencimiento TEXT, " +
                "FOREIGN KEY(idPersona) REFERENCES persona(idPersona))"

        private const val CREATE_NOSOCIO_TABLE = "CREATE TABLE nosocio (" +
                "idNoSocio INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "idPersona INTEGER, " +
                "FOREIGN KEY(idPersona) REFERENCES persona(idPersona))"

        private const val CREATE_ACTIVIDAD_TABLE = "CREATE TABLE actividad (" +
                "idActividad INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "nombreActividad TEXT)"

        private const val CREATE_CUOTA_MENSUAL_TABLE = "CREATE TABLE cuotamensual (" +
                "idCuotaMensual INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "idSocio INTEGER, " +
                "monto REAL, " +
                "fechaPago TEXT, " +
                "fechaVencimiento TEXT, " +
                "formaPago TEXT, " +
                "FOREIGN KEY(idSocio) REFERENCES socio(idSocio))"

        private const val CREATE_CUOTA_DIARIA_TABLE = "CREATE TABLE cuotadiaria (" +
                "idCuotaDiaria INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "idNoSocio INTEGER, " +
                "idActividad INTEGER, " +
                "monto REAL, " +
                "fechaPago TEXT, " +
                "formaPago TEXT, " +
                "FOREIGN KEY(idNoSocio) REFERENCES nosocio(idNoSocio), " +
                "FOREIGN KEY(idActividad) REFERENCES actividad(idActividad))"
    }

    // Cargar actividades al selector
    fun obtenerActividades(): List<String> {
        val db = readableDatabase
        val cursor =
            db.rawQuery("SELECT nombreActividad FROM actividad ORDER BY nombreActividad", null)
        val lista = mutableListOf<String>()
        while (cursor.moveToNext()) {
            lista.add(cursor.getString(0))
        }
        cursor.close()
        db.close()
        return lista
    }

    fun obtenerIdActividadPorNombre(nombre: String): Long? {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT idActividad FROM actividad WHERE nombreActividad = ?",
            arrayOf(nombre)
        )
        val id = if (cursor.moveToFirst()) cursor.getLong(0) else null
        cursor.close()
        db.close()
        return id
    }

    fun registrarUsuario(usuario: String, clave: String): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("usuario", usuario)
            put("clave", clave)
        }
        return try {
            db.insertOrThrow("usuario", null, values)
            true
        } catch (e: SQLiteConstraintException) {
            // Usuario duplicado (viola la restricción UNIQUE)
            false
        } finally {
            db.close()
        }
    }

    fun verificarLogin(usuario: String, clave: String): Boolean {
        val db = readableDatabase
        // Consulta que busca el usuario y la clave exactamente
        val query = "SELECT 1 FROM usuario WHERE usuario = ? AND clave = ? LIMIT 1"
        val cursor = db.rawQuery(query, arrayOf(usuario, clave))
        val existe = cursor.count > 0
        cursor.close()
        db.close()
        return existe
    }

    fun existePersonaPorDni(dni: String): Boolean {
        val db = readableDatabase
        val query = "SELECT 1 FROM persona WHERE dni = ? LIMIT 1"
        val cursor = db.rawQuery(query, arrayOf(dni))
        val existe = cursor.count > 0
        cursor.close()
        db.close()
        return existe
    }

    fun insertarPersona(
        nombre: String,
        apellido: String,
        dni: String,
        telefono: String,
        direccion: String,
        email: String,
        fichaMedica: Boolean,   // true = tiene ficha médica, false = no
        fechaInscripcion: String
    ): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("nombre", nombre)
            put("apellido", apellido)
            put("dni", dni)
            put("telefono", telefono)
            put("direccion", direccion)
            put("email", email)
            put("fichaMedica", if (fichaMedica) 1 else 0)
            put("fechaInscripcion", fechaInscripcion)
        }
        val id = db.insert("persona", null, values)
        db.close()
        return id  // retorna el idPersona generado, o -1 si error
    }

    fun insertarSocio(idPersona: Long, fechaVencimiento: String): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("idPersona", idPersona)
            put("fechaVencimiento", fechaVencimiento)
        }
        val id = db.insert("socio", null, values)
        db.close()
        return id  // retorna idSocio generado, o -1 si error
    }

    fun insertarNoSocio(idPersona: Long): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("idPersona", idPersona)
        }
        val id = db.insert("nosocio", null, values)
        db.close()
        return id
    }

    // Insertar Cuota Mensual (para Socio)
    fun insertarCuotaMensual(
        idSocio: Long,
        monto: Double,
        fechaPago: String,
        fechaVencimiento: String,
        formaPago: String
    ): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("idSocio", idSocio)
            put("monto", monto)
            put("fechaPago", fechaPago)
            put("fechaVencimiento", fechaVencimiento)
            put("formaPago", formaPago)
        }
        val id = db.insert("cuotamensual", null, values)
        db.close()
        return id
    }

    // Insertar Cuota Diaria (para No Socio)
    fun insertarCuotaDiaria(
        idNoSocio: Long,
        idActividad: Long,
        monto: Double,
        fechaPago: String,
        formaPago: String
    ): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("idNoSocio", idNoSocio)
            put("idActividad", idActividad)
            put("monto", monto)
            put("fechaPago", fechaPago)
            put("formaPago", formaPago)
        }
        val id = db.insert("cuotadiaria", null, values)
        db.close()
        return id
    }

    // Lista todos los socios y no socios, junto con documento, tipo de membresía, id
    // Para el searchbar de Pagar Cuota
    fun obtenerTodosLosMiembros(): List<UsuarioActivo> {
        val db = readableDatabase
        val query = """
        SELECT 
            persona.nombre || ' ' || persona.apellido AS nombre_completo, 
            persona.dni, 
            'Socio' AS tipo,
            socio.idSocio AS idSocio,
            NULL AS idNoSocio
        FROM socio 
        INNER JOIN persona ON socio.idPersona = persona.idPersona
        
        UNION ALL
        
        SELECT 
            persona.nombre || ' ' || persona.apellido AS nombre_completo, 
            persona.dni, 
            'No Socio' AS tipo,
            NULL AS idSocio,
            nosocio.idNoSocio AS idNoSocio
        FROM nosocio 
        INNER JOIN persona ON nosocio.idPersona = persona.idPersona
    """
        val cursor = db.rawQuery(query, null)
        val lista = mutableListOf<UsuarioActivo>()
        while (cursor.moveToNext()) {
            val nombre = cursor.getString(0)
            val dni = cursor.getString(1)
            val tipo = cursor.getString(2)
            val idSocio = if (cursor.isNull(3)) null else cursor.getInt(3)
            val idNoSocio = if (cursor.isNull(4)) null else cursor.getInt(4)
            lista.add(UsuarioActivo(nombre, dni, tipo, idSocio, idNoSocio))
        }
        cursor.close()
        db.close()
        return lista
    }

    fun obtenerVencimientos(fechaFiltro: String, filtrarPorDiaExacto: Boolean): List<Vencimiento> {
        val db = this.readableDatabase
        val lista = mutableListOf<Vencimiento>()

        val sql =
            "SELECT s.idSocio, s.fechaVencimiento, p.apellido, p.nombre FROM socio s INNER JOIN persona p ON s.idPersona = p.idPersona"
        val cursor = db.rawQuery(sql, null)

        val formato = SimpleDateFormat("d/M/yyyy", Locale.getDefault())
        val fechaSeleccionada = formato.parse(fechaFiltro)

        if (cursor.moveToFirst()) {
            do {
                val idSocio = cursor.getInt(0)
                val fechaVencStr = cursor.getString(1) ?: ""
                val apellido = cursor.getString(2)
                val nombre = cursor.getString(3)

                try {
                    val fechaVencimiento = formato.parse(fechaVencStr)

                    if (fechaVencimiento != null && fechaSeleccionada != null) {

                        var agregarALista = false

                        if (filtrarPorDiaExacto) {
                            // Las fechas deben ser exactamente idénticas
                            if (fechaVencimiento.compareTo(fechaSeleccionada) == 0) {
                                agregarALista = true
                            }
                        } else {
                            // El vencimiento debe ser menor o igual a la fecha del calendario
                            if (fechaVencimiento <= fechaSeleccionada) {
                                agregarALista = true
                            }
                        }

                        if (agregarALista) {
                            val nombreCompleto = "$apellido, $nombre"
                            val idFormateado = "ID: ${idSocio.toString().padStart(3, '0')}"
                            lista.add(Vencimiento(nombreCompleto, idFormateado, fechaVencStr))
                        }
                    }
                } catch (e: Exception) {
                }

            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()

        return lista
    }


    // Obtiene nombre, apellido y dni de un Socio o No Socio a partir de su id
    // Usado en Pagar Cuota para armar el comprobante
    fun obtenerDatosPersona(id: Int, esSocio: Boolean): Triple<String, String, String>? {
        val db = readableDatabase
        val query = if (esSocio) {
            "SELECT p.nombre, p.apellido, p.dni FROM persona p " +
                    "INNER JOIN socio s ON s.idPersona = p.idPersona WHERE s.idSocio = ?"
        } else {
            "SELECT p.nombre, p.apellido, p.dni FROM persona p " +
                    "INNER JOIN nosocio n ON n.idPersona = p.idPersona WHERE n.idNoSocio = ?"
        }
        val cursor = db.rawQuery(query, arrayOf(id.toString()))
        val datos = if (cursor.moveToFirst()) {
            Triple(cursor.getString(0), cursor.getString(1), cursor.getString(2))
        } else null
        cursor.close()
        db.close()
        return datos
    }

    // Actualiza la fecha de vencimiento del Socio luego de registrar el pago de la cuota mensual
    fun actualizarVencimientoSocio(idSocio: Long, nuevaFechaVencimiento: String): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("fechaVencimiento", nuevaFechaVencimiento)
        }
        val filasActualizadas =
            db.update("socio", values, "idSocio = ?", arrayOf(idSocio.toString()))
        db.close()
        return filasActualizadas
    }

    fun obtenerVencimientoActualSocio(idSocio: Long): String? {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT fechaVencimiento FROM socio WHERE idSocio =?",
            arrayOf(idSocio.toString())
        )
        val fecha = if (cursor.moveToFirst()) cursor.getString(0) else null
        cursor.close()
        db.close()
        return fecha
    }
}