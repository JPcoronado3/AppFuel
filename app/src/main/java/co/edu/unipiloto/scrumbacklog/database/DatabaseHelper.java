package co.edu.unipiloto.scrumbacklog.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "app.db";
    private static final int DATABASE_VERSION = 28;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // COMBUSTIBLE
        db.execSQL("CREATE TABLE combustible (" +
                "id_combustible INTEGER PRIMARY KEY AUTOINCREMENT," +
                "nombre TEXT UNIQUE)");

        // UBICACION
        db.execSQL("CREATE TABLE ubicacion (" +
                "id_ubicacion INTEGER PRIMARY KEY AUTOINCREMENT," +
                "nombre TEXT," +
                "ciudad TEXT," +
                "localidad TEXT," +
                "hora_apertura TEXT," +
                "hora_cierre TEXT)");

        // ALERTA
        db.execSQL("CREATE TABLE alerta (" +
                "id_alerta INTEGER PRIMARY KEY AUTOINCREMENT," +
                "id_combustible INTEGER," +
                "id_ubicacion INTEGER," +
                "nivel_minimo REAL," +
                "activa INTEGER DEFAULT 1," +
                "FOREIGN KEY(id_combustible) REFERENCES combustible(id_combustible)," +
                "FOREIGN KEY(id_ubicacion) REFERENCES ubicacion(id_ubicacion))");

        // INVENTARIO
        db.execSQL("CREATE TABLE inventario (" +
                "id_inventario INTEGER PRIMARY KEY AUTOINCREMENT," +
                "id_combustible INTEGER," +
                "id_ubicacion INTEGER," +
                "cantidad REAL," +
                "FOREIGN KEY(id_combustible) REFERENCES combustible(id_combustible)," +
                "FOREIGN KEY(id_ubicacion) REFERENCES ubicacion(id_ubicacion)," +
                "UNIQUE(id_combustible,id_ubicacion))");

        // PRECIOS
        db.execSQL("CREATE TABLE precio_combustible (" +
                "id_precio INTEGER PRIMARY KEY AUTOINCREMENT," +
                "id_combustible INTEGER," +
                "id_ubicacion INTEGER," +
                "precio REAL)");

        // MOVIMIENTOS
        db.execSQL("CREATE TABLE movimientos (" +
                "id_movimiento INTEGER PRIMARY KEY AUTOINCREMENT," +
                "id_combustible INTEGER," +
                "id_ubicacion INTEGER," +
                "tipo_movimiento TEXT," +
                "galones REAL," +
                "precio_unitario REAL," +
                "total REAL," +
                "fecha TEXT)");

        // USUARIO (COMPLETO)
        db.execSQL("CREATE TABLE usuario (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "nombre TEXT NOT NULL," +
                "usuario TEXT NOT NULL," +
                "correo TEXT NOT NULL UNIQUE," +
                "direccion TEXT," +
                "password TEXT NOT NULL," +
                "rol TEXT NOT NULL," +
                "id_ubicacion INTEGER," +
                "fecha_nacimiento TEXT," +
                "genero TEXT," +
                "latitud REAL," +
                "longitud REAL," +
                "verificado INTEGER DEFAULT 0," +
                "codigo_verificacion TEXT," +
                "FOREIGN KEY(id_ubicacion) REFERENCES ubicacion(id_ubicacion))");

        // PEDIDO
        db.execSQL("CREATE TABLE pedido (" +
                "id_pedido INTEGER PRIMARY KEY AUTOINCREMENT," +
                "id_ubicacion INTEGER," +
                "id_combustible INTEGER," +
                "cantidad REAL," +
                "fecha TEXT," +
                "estado TEXT DEFAULT 'PENDIENTE'," +
                "motivo_cancelacion TEXT," +
                "FOREIGN KEY(id_ubicacion) REFERENCES ubicacion(id_ubicacion)," +
                "FOREIGN KEY(id_combustible) REFERENCES combustible(id_combustible))");

        insertarDatosIniciales(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS pedido");
        db.execSQL("DROP TABLE IF EXISTS usuario");
        db.execSQL("DROP TABLE IF EXISTS movimientos");
        db.execSQL("DROP TABLE IF EXISTS inventario");
        db.execSQL("DROP TABLE IF EXISTS precio_combustible");
        db.execSQL("DROP TABLE IF EXISTS ubicacion");
        db.execSQL("DROP TABLE IF EXISTS combustible");
        db.execSQL("DROP TABLE IF EXISTS alerta");

        onCreate(db);
    }

    private void insertarDatosIniciales(SQLiteDatabase db) {

        // Combustibles
        db.execSQL("INSERT INTO combustible (nombre) VALUES ('Corriente')");
        db.execSQL("INSERT INTO combustible (nombre) VALUES ('Extra')");
        db.execSQL("INSERT INTO combustible (nombre) VALUES ('Diesel')");

        // Estaciones (SIN ID manual)
        db.execSQL("INSERT INTO ubicacion (nombre, ciudad, localidad, hora_apertura, hora_cierre) VALUES ('Estación Suba','Bogota','Suba','00:00','22:00')");
        db.execSQL("INSERT INTO ubicacion (nombre, ciudad, localidad, hora_apertura, hora_cierre) VALUES ('Estación Calle 80','Bogota','Calle 80','00:00','23:00')");
        db.execSQL("INSERT INTO ubicacion (nombre, ciudad, localidad, hora_apertura, hora_cierre) VALUES ('Estación Norte','Bogota','Norte','00:00','23:00')");
        db.execSQL("INSERT INTO ubicacion (nombre, ciudad, localidad, hora_apertura, hora_cierre) VALUES ('Estación Americas','Bogota','Americas','00:00','23:00')");
        db.execSQL("INSERT INTO ubicacion (nombre, ciudad, localidad, hora_apertura, hora_cierre) VALUES ('Estación Dorado','Bogota','Dorado','00:00','23:00')");
        db.execSQL("INSERT INTO ubicacion (nombre, ciudad, localidad, hora_apertura, hora_cierre) VALUES ('Estación 20 de Julio','Bogota','20 de Julio','00:00','21:00')");
        db.execSQL("INSERT INTO ubicacion (nombre, ciudad, localidad, hora_apertura, hora_cierre) VALUES ('Estación Tunal','Bogota','Tunal','00:00','23:00')");
        db.execSQL("INSERT INTO ubicacion (nombre, ciudad, localidad, hora_apertura, hora_cierre) VALUES ('Estación Usme','Bogota','Usme','00:00','23:00')");
        db.execSQL("INSERT INTO ubicacion (nombre, ciudad, localidad, hora_apertura, hora_cierre) VALUES ('Estación Sur','Bogota','Sur','00:00','23:00')");
        db.execSQL("INSERT INTO ubicacion (nombre, ciudad, localidad, hora_apertura, hora_cierre) VALUES ('Estación Caracas','Bogota','Caracas','00:00','23:00')");
        db.execSQL("INSERT INTO ubicacion (nombre, ciudad, localidad, hora_apertura, hora_cierre) VALUES ('Estación Centro','Bogota','Centro','00:00','23:00')");


        for (int u = 1; u <= 11; u++){
            db.execSQL("INSERT INTO inventario VALUES (NULL,1,"+u+",10000)");
            db.execSQL("INSERT INTO inventario VALUES (NULL,2,"+u+",8000)");
            db.execSQL("INSERT INTO inventario VALUES (NULL,3,"+u+",7500)");
        }
        db.execSQL("INSERT INTO precio_combustible VALUES (NULL,1,1,16000)");
        db.execSQL("INSERT INTO precio_combustible VALUES (NULL,2,1,22700)");
        db.execSQL("INSERT INTO precio_combustible VALUES (NULL,3,1,13200)");
        db.execSQL("INSERT INTO precio_combustible VALUES (NULL,1,2,15900)");
        db.execSQL("INSERT INTO precio_combustible VALUES (NULL,2,2,22600)");
        db.execSQL("INSERT INTO precio_combustible VALUES (NULL,3,2,13100)");
        db.execSQL("INSERT INTO precio_combustible VALUES (NULL,1,3,15800)");
        db.execSQL("INSERT INTO precio_combustible VALUES (NULL,2,3,22500)");
        db.execSQL("INSERT INTO precio_combustible VALUES (NULL,3,3,13000)");
        db.execSQL("INSERT INTO precio_combustible VALUES (NULL,1,4,15800)");
        db.execSQL("INSERT INTO precio_combustible VALUES (NULL,2,4,21500)");
        db.execSQL("INSERT INTO precio_combustible VALUES (NULL,3,4,13500)");
        db.execSQL("INSERT INTO precio_combustible VALUES (NULL,1,5,16800)");
        db.execSQL("INSERT INTO precio_combustible VALUES (NULL,2,5,22500)");
        db.execSQL("INSERT INTO precio_combustible VALUES (NULL,3,5,13900)");
        db.execSQL("INSERT INTO precio_combustible VALUES (NULL,1,6,13800)");
        db.execSQL("INSERT INTO precio_combustible VALUES (NULL,2,6,22500)");
        db.execSQL("INSERT INTO precio_combustible VALUES (NULL,3,6,15000)");
        db.execSQL("INSERT INTO precio_combustible VALUES (NULL,1,7,18800)");
        db.execSQL("INSERT INTO precio_combustible VALUES (NULL,2,7,20500)");
        db.execSQL("INSERT INTO precio_combustible VALUES (NULL,3,7,13000)");
        db.execSQL("INSERT INTO precio_combustible VALUES (NULL,1,8,15800)");
        db.execSQL("INSERT INTO precio_combustible VALUES (NULL,2,8,22900)");
        db.execSQL("INSERT INTO precio_combustible VALUES (NULL,3,8,14800)");
        db.execSQL("INSERT INTO precio_combustible VALUES (NULL,1,9,15800)");
        db.execSQL("INSERT INTO precio_combustible VALUES (NULL,2,9,24700)");
        db.execSQL("INSERT INTO precio_combustible VALUES (NULL,3,9,14650)");
        db.execSQL("INSERT INTO precio_combustible VALUES (NULL,1,10,13800)");
        db.execSQL("INSERT INTO precio_combustible VALUES (NULL,2,10,20500)");
        db.execSQL("INSERT INTO precio_combustible VALUES (NULL,3,10,14570)");
        db.execSQL("INSERT INTO precio_combustible VALUES (NULL,1,11,13800)");
        db.execSQL("INSERT INTO precio_combustible VALUES (NULL,2,11,25500)");
        db.execSQL("INSERT INTO precio_combustible VALUES (NULL,3,11,13999)");
    }
}