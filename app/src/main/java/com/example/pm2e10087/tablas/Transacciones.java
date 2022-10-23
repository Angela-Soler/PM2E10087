package com.example.pm2e10087.tablas;

public class Transacciones {
    //Nombre de la Base de Datos
    public static final String NameDatabase = "PME1DB";

    /*Creacion de la base de Datos*/
    public static final String TablaContactos = "contactos";

    /*Creación de la tabla contactos*/
    public static final String id = "id";
    public static final String nombre = "nombre";
    public static final String telefono = "telefono";
    public static final String pais = "pais";
    public static final String nota = "nota";
    public static final String image = "image";

    //DDL
    public static final String createTableContactos = "CREATE TABLE "+Transacciones.TablaContactos+
            " (id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "nombre TEXT," +
            "telefono INTEGER," +
            "pais TEXT," +
            "nota TEXT," +
            "image TEXT)";

    public static final String GetContactos = "SELECT * FROM "+Transacciones.TablaContactos;

    public static final String DropTableContactos = "DROP TABLE IF EXISTS contactos";

    public static final String DeleteRegistro = "DELETE FROM "+TablaContactos;

}

