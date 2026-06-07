package com.clubdeportivo.app.db

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

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
        val actividadesIniciales = listOf("Natación", "Elongación", "Musculación", "Artes Marciales", "Tenis", "Yoga")
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
        val cursor = db.rawQuery("SELECT nombreActividad FROM actividad ORDER BY nombreActividad", null)
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
        val cursor = db.rawQuery("SELECT idActividad FROM actividad WHERE nombreActividad = ?", arrayOf(nombre))
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

    fun contarActividades(): Int {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT COUNT(*) FROM actividad", null)
        cursor.moveToFirst()
        val count = cursor.getInt(0)
        cursor.close()
        db.close()
        return count
    }

}