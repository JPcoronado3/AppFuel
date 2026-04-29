package co.edu.unipiloto.scrumbacklog.database.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class InventarioDAO {

    private SQLiteDatabase db;

    public InventarioDAO(SQLiteDatabase db) {
        this.db = db;
    }

    public double obtenerInventario(String tipo, String ciudad, String zona) {

        Cursor cursor = null;
        double resultado = 0;

        try {
            cursor = db.rawQuery(
                    "SELECT cantidad FROM inventario i " +
                            "JOIN combustible c ON i.id_combustible=c.id_combustible " +
                            "JOIN ubicacion u ON i.id_ubicacion=u.id_ubicacion " +
                            "WHERE c.nombre=? AND u.ciudad=? AND u.localidad=?",
                    new String[]{tipo, ciudad, zona}
            );

            if (cursor.moveToFirst()) {
                resultado = cursor.getDouble(0);
            }

        } finally {
            if (cursor != null) cursor.close();
        }

        return resultado;
    }

    public double obtenerInventarioTotalPorCiudad(String tipo, String ciudad) {

        Cursor cursor = null;
        double resultado = 0;

        try {
            cursor = db.rawQuery(
                    "SELECT SUM(i.cantidad) " +
                            "FROM inventario i " +
                            "JOIN combustible c ON i.id_combustible=c.id_combustible " +
                            "JOIN ubicacion u ON i.id_ubicacion=u.id_ubicacion " +
                            "WHERE c.nombre=? AND u.ciudad=?",
                    new String[]{tipo, ciudad}
            );

            if (cursor.moveToFirst() && !cursor.isNull(0)) {
                resultado = cursor.getDouble(0);
            }

        } finally {
            if (cursor != null) cursor.close();
        }

        return resultado;
    }

    public long insertarInventario(int cantidad, int idCombustible, int idUbicacion) {

        ContentValues values = new ContentValues();
        values.put("cantidad", cantidad);
        values.put("id_combustible", idCombustible);
        values.put("id_ubicacion", idUbicacion);

        return db.insert("inventario", null, values);
    }

    public List<String> obtenerHistorial() {

        List<String> lista = new ArrayList<>();
        Cursor cursor = null;

        try {
            cursor = db.rawQuery(
                    "SELECT i.cantidad, c.nombre, u.ciudad, u.localidad " +
                            "FROM inventario i " +
                            "JOIN combustible c ON i.id_combustible=c.id_combustible " +
                            "JOIN ubicacion u ON i.id_ubicacion=u.id_ubicacion " +
                            "ORDER BY i.id DESC",
                    null
            );

            while (cursor.moveToNext()) {
                lista.add(
                        "+" + cursor.getInt(0) + " | " +
                                cursor.getString(1) + " | " +
                                cursor.getString(2) + " - " +
                                cursor.getString(3)
                );
            }

        } finally {
            if (cursor != null) cursor.close();
        }

        return lista;
    }

    public double obtenerInventarioPorUbicacion(String tipo, int idUbicacion) {

        Cursor cursor = null;
        double resultado = 0;

        try {
            cursor = db.rawQuery(
                    "SELECT cantidad FROM inventario i " +
                            "JOIN combustible c ON i.id_combustible=c.id_combustible " +
                            "WHERE c.nombre=? AND i.id_ubicacion=?",
                    new String[]{tipo, String.valueOf(idUbicacion)}
            );

            if (cursor.moveToFirst()) {
                resultado = cursor.getDouble(0);
            }

        } finally {
            if (cursor != null) cursor.close();
        }

        return resultado;
    }

    public List<String> obtenerHistorialPorUbicacion(int idUbicacion) {

        List<String> lista = new ArrayList<>();
        Cursor cursor = null;

        try {
            cursor = db.rawQuery(
                    "SELECT i.cantidad, c.nombre, u.ciudad, u.localidad " +
                            "FROM inventario i " +
                            "JOIN combustible c ON i.id_combustible=c.id_combustible " +
                            "JOIN ubicacion u ON i.id_ubicacion=u.id_ubicacion " +
                            "WHERE i.id_ubicacion=? " +
                            "ORDER BY i.id DESC",
                    new String[]{String.valueOf(idUbicacion)}
            );

            while (cursor.moveToNext()) {
                lista.add(
                        "+" + cursor.getInt(0) + " | " +
                                cursor.getString(1) + " | " +
                                cursor.getString(2) + " - " +
                                cursor.getString(3)
                );
            }

        } finally {
            if (cursor != null) cursor.close();
        }

        return lista;
    }
}