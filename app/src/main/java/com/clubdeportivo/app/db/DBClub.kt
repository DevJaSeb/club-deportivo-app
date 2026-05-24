package com.clubdeportivo.app.db

import android.content.Context
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
                "usuario TEXT, " +
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
}