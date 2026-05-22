package co.edu.unipiloto.scrumbacklog.database.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import co.edu.unipiloto.scrumbacklog.model.Estacion;

public class UbicacionDAO {
    private SQLiteDatabase db;

    public UbicacionDAO(SQLiteDatabase db) {
        this.db = db;
    }

    public ArrayList<String> obtenerCiudades(){
        ArrayList<String> lista = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT DISTINCT ciudad FROM ubicacion", null);

        while(cursor.moveToNext()){
            lista.add(cursor.getString(0).trim());
        }
        cursor.close();
        return lista;
    }

    public ArrayList<String> obtenerZonas(String ciudad){
        ArrayList<String> lista = new ArrayList<>();
        Cursor cursor = db.rawQuery(
                "SELECT localidad FROM ubicacion WHERE ciudad=?",
                new String[]{ciudad}
        );

        while(cursor.moveToNext()){
            lista.add(cursor.getString(0).trim());
        }

        cursor.close();
        return lista;
    }

    public int obtenerIdUbicacion(String ciudad, String zona){
        Cursor cursor = db.rawQuery(
                "SELECT id_ubicacion FROM ubicacion WHERE ciudad=? AND localidad=?",
                new String[]{ciudad, zona}
        );

        if(cursor.moveToFirst()){
            int id = cursor.getInt(0);
            cursor.close();
            return id;
        }

        cursor.close();
        return -1;
    }

    public Cursor obtenerHorarios() {
        return db.rawQuery(
                "SELECT id_ubicacion AS _id, * FROM ubicacion",
                null
        );
    }

    public ArrayList<Estacion> obtenerEstaciones() {

        ArrayList<Estacion> lista = new ArrayList<>();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM ubicacion",
                null
        );

        while (cursor.moveToNext()) {

            String nombre = cursor.getString(
                    cursor.getColumnIndexOrThrow("nombre"));

            String ciudad = cursor.getString(
                    cursor.getColumnIndexOrThrow("ciudad"));

            String localidad = cursor.getString(
                    cursor.getColumnIndexOrThrow("localidad"));

            String direccion = cursor.getString(
                    cursor.getColumnIndexOrThrow("direccion"));

            String apertura = cursor.getString(
                    cursor.getColumnIndexOrThrow("hora_apertura"));

            String cierre = cursor.getString(
                    cursor.getColumnIndexOrThrow("hora_cierre"));

            double latitud = cursor.getDouble(
                    cursor.getColumnIndexOrThrow("latitud"));

            double longitud = cursor.getDouble(
                    cursor.getColumnIndexOrThrow("longitud"));

            // SIMULACIÓN disponibilidad
            boolean disponible = true;

            lista.add(
                    new Estacion(
                            nombre,
                            ciudad,
                            localidad,
                            latitud,
                            longitud,
                            direccion,
                            apertura,
                            cierre,
                            disponible
                    )
            );
        }

        cursor.close();

        return lista;
    }
    }