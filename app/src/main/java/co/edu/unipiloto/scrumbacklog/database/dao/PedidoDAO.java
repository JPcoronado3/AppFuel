package co.edu.unipiloto.scrumbacklog.database.dao;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.database.Cursor;

public class PedidoDAO {

    private SQLiteDatabase db;

    public PedidoDAO(SQLiteDatabase db){
        this.db = db;
    }

    public Cursor obtenerPedidosPendientes() {
        return db.rawQuery(
                "SELECT p.id_pedido AS _id, " +
                        "p.id_pedido, " +
                        "p.id_ubicacion, " +
                        "p.id_combustible," +
                        "u.nombre AS ubicacion, " +
                        "c.nombre AS combustible, " +
                        "p.cantidad," +
                        "p.fecha " +
                        "FROM pedido p " +
                        "JOIN ubicacion u ON p.id_ubicacion = u.id_ubicacion " +
                        "JOIN combustible c ON p.id_combustible = c.id_combustible " +
                        "WHERE p.estado = 'PENDIENTE'",
                null
        );
    }

    public Cursor obtenerPedidosEntregados() {
        return db.rawQuery(
                "SELECT p.id_pedido AS _id, " +
                        "p.id_pedido, " +
                        "u.nombre AS ubicacion, " +
                        "c.nombre AS combustible, " +
                        "p.cantidad, " +
                        "p.fecha " +
                        "FROM pedido p " +
                        "JOIN ubicacion u ON p.id_ubicacion = u.id_ubicacion " +
                        "JOIN combustible c ON p.id_combustible = c.id_combustible " +
                        "WHERE p.estado = 'ENTREGADO'",
                null
        );
    }

    public Cursor obtenerPedidosEntregadosPorUbicacion(int idUbicacion) {
        return db.rawQuery(
                "SELECT p.id_pedido AS _id, " +
                        "p.id_pedido, " +
                        "p.id_ubicacion, " +
                        "u.nombre AS ubicacion, " +
                        "c.nombre AS combustible, " +
                        "p.cantidad, " +
                        "p.fecha " +
                        "FROM pedido p " +
                        "JOIN ubicacion u ON p.id_ubicacion = u.id_ubicacion " +
                        "JOIN combustible c ON p.id_combustible = c.id_combustible " +
                        "WHERE p.estado = 'ENTREGADO' " +
                        "AND p.id_ubicacion = ?",
                new String[]{String.valueOf(idUbicacion)}
        );
    }

    public void marcarComoRecibido(int idPedido) {
        ContentValues values = new ContentValues();
        values.put("estado", "RECIBIDO");

        db.update("pedido", values, "id_pedido = ?", new String[]{String.valueOf(idPedido)});
    }

    public void marcarComoEntregado(int idPedido, String fechaEntrega) {
        ContentValues values = new ContentValues();
        values.put("estado", "ENTREGADO");
        values.put("fecha", fechaEntrega);

        db.update("pedido", values, "id_pedido = ?", new String[]{String.valueOf(idPedido)});
    }

    public void aceptarPedido(int idPedido) {
        ContentValues values = new ContentValues();
        values.put("estado", "ACEPTADO");

        db.update("pedido", values, "id_pedido = ?", new String[]{String.valueOf(idPedido)});
    }

    public void cancelarPedido(int idPedido, String motivo) {
        ContentValues values = new ContentValues();
        values.put("estado", "CANCELADO");
        values.put("motivo_cancelacion", motivo);

        db.update("pedido", values, "id_pedido = ?", new String[]{String.valueOf(idPedido)});
    }

    public Cursor obtenerPedidosCancelados() {
        return db.rawQuery(
                "SELECT p.id_pedido AS _id, " +
                        "u.nombre AS ubicacion, " +
                        "c.nombre AS combustible, " +
                        "p.cantidad, p.fecha, p.motivo_cancelacion " +
                        "FROM pedido p " +
                        "JOIN ubicacion u ON p.id_ubicacion = u.id_ubicacion " +
                        "JOIN combustible c ON p.id_combustible = c.id_combustible " +
                        "WHERE p.estado = 'CANCELADO'",
                null
        );
    }

    public void reagendarPedido(int idPedido, String nuevaFecha) {
        ContentValues values = new ContentValues();
        values.put("fecha", nuevaFecha);
        values.put("estado", "PENDIENTE");
        values.put("motivo_cancelacion", (String) null);

        db.update("pedido", values, "id_pedido = ?", new String[]{String.valueOf(idPedido)});
    }

    public void crearPedido(int idUbicacion ,int idCombustible,double cantidad,String fecha){
        ContentValues values = new ContentValues();
        values.put("id_ubicacion", idUbicacion);
        values.put("id_combustible", idCombustible);
        values.put("cantidad", cantidad);
        values.put("fecha", fecha);
        values.put("estado", "PENDIENTE");

        db.insert("pedido", null, values);
    }

    public Cursor obtenerPedidosAceptados() {
        return db.rawQuery(
                "SELECT p.id_pedido AS _id, " +
                        "p.id_pedido, p.id_ubicacion, p.id_combustible, " +
                        "u.nombre AS ubicacion, " +
                        "c.nombre AS combustible, " +
                        "p.cantidad, p.fecha " +
                        "FROM pedido p " +
                        "JOIN ubicacion u ON p.id_ubicacion = u.id_ubicacion " +
                        "JOIN combustible c ON p.id_combustible = c.id_combustible " +
                        "WHERE p.estado = 'ACEPTADO'",
                null
        );
    }
}