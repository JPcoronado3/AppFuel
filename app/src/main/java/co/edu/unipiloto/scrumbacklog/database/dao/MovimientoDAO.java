package co.edu.unipiloto.scrumbacklog.database.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

public class MovimientoDAO {

    private SQLiteDatabase db;
    private CombustibleDAO combustibleDAO;
    private UbicacionDAO ubicacionDAO;
    private InventarioDAO inventarioDAO;

    public MovimientoDAO(SQLiteDatabase db) {
        this.db = db;
        combustibleDAO = new CombustibleDAO(db);
        ubicacionDAO = new UbicacionDAO(db);
        inventarioDAO = new InventarioDAO(db);
    }

    public boolean registrarEntradaPorUbicacion(String tipo, double galones, double precio, String fecha, int idUbic) {

        int idComb = combustibleDAO.obtenerIdCombustible(tipo);
        if (idComb == -1 || idUbic == -1) return false;

        double total = galones * precio;

        ContentValues values = new ContentValues();
        values.put("id_combustible", idComb);
        values.put("id_ubicacion", idUbic);
        values.put("tipo_movimiento", "ENTRADA");
        values.put("galones", galones);
        values.put("precio_unitario", precio);
        values.put("total", total);
        values.put("fecha", fecha);

        long res = db.insert("movimientos", null, values);

        if (res != -1) {
            db.execSQL(
                    "UPDATE inventario SET cantidad=cantidad+? WHERE id_combustible=? AND id_ubicacion=?",
                    new Object[]{galones, idComb, idUbic}
            );

            verificarAlerta(idComb, idUbic);
            return true;
        }

        return false;
    }

    public boolean registrarSalidaPorUbicacion(String tipo, double galones, double precio, String fecha, int idUbic) {

        int idComb = combustibleDAO.obtenerIdCombustible(tipo);
        if (idComb == -1 || idUbic == -1) return false;

        double disponible = inventarioDAO.obtenerInventarioPorUbicacion(tipo, idUbic);

        if (disponible < galones) return false;

        double total = galones * precio;

        ContentValues values = new ContentValues();
        values.put("id_combustible", idComb);
        values.put("id_ubicacion", idUbic);
        values.put("tipo_movimiento", "SALIDA");
        values.put("galones", galones);
        values.put("precio_unitario", precio);
        values.put("total", total);
        values.put("fecha", fecha);

        long res = db.insert("movimientos", null, values);

        if (res != -1) {
            db.execSQL(
                    "UPDATE inventario SET cantidad=cantidad-? WHERE id_combustible=? AND id_ubicacion=?",
                    new Object[]{galones, idComb, idUbic}
            );

            return true;
        }

        return false;
    }

    public ArrayList<String> obtenerMovimientosPorUbicacion(int idUbicacion) {

        ArrayList<String> lista = new ArrayList<>();

        Cursor cursor = db.rawQuery(
                "SELECT m.tipo_movimiento, c.nombre, m.galones, m.total, m.fecha " +
                        "FROM movimientos m " +
                        "JOIN combustible c ON m.id_combustible=c.id_combustible " +
                        "WHERE m.id_ubicacion=? " +
                        "ORDER BY m.id_movimiento DESC",
                new String[]{String.valueOf(idUbicacion)}
        );

        while (cursor.moveToNext()) {
            lista.add(
                    cursor.getString(4) + " | " +
                            cursor.getString(0) + " | " +
                            cursor.getString(1) + " | " +
                            cursor.getDouble(2) + " gal | $" +
                            cursor.getDouble(3)
            );
        }

        cursor.close();
        return lista;
    }

    private void verificarAlerta(int idComb, int idUbic) {

        Cursor cursor = db.rawQuery(
                "SELECT nivel_minimo FROM alerta WHERE id_combustible=? AND id_ubicacion=? AND activa=1",
                new String[]{String.valueOf(idComb), String.valueOf(idUbic)}
        );

        if (cursor.moveToFirst()) {

            double nivelMin = cursor.getDouble(0);

            Cursor inv = db.rawQuery(
                    "SELECT cantidad FROM inventario WHERE id_combustible=? AND id_ubicacion=?",
                    new String[]{String.valueOf(idComb), String.valueOf(idUbic)}
            );

            if (inv.moveToFirst()) {
                double actual = inv.getDouble(0);

                if (actual < nivelMin) {
                    System.out.println("⚠ ALERTA: Nivel bajo combustible");
                }
            }

            inv.close();
        }

        cursor.close();
    }
}