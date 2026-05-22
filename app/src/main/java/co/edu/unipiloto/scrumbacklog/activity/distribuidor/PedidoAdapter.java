package co.edu.unipiloto.scrumbacklog.activity.distribuidor;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.view.*;
import android.widget.*;

import co.edu.unipiloto.scrumbacklog.R;
import co.edu.unipiloto.scrumbacklog.activity.MainActivity;
import co.edu.unipiloto.scrumbacklog.database.dao.PedidoDAO;

public class PedidoAdapter extends CursorAdapter {

    private PedidoDAO pedidoDAO;

    public PedidoAdapter(Context context, Cursor cursor, PedidoDAO pedidoDAO) {
        super(context, cursor, 0);
        this.pedidoDAO = pedidoDAO;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.item_pedido, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        TextView tvInfo = view.findViewById(R.id.tvInfo);
        EditText etMotivo = view.findViewById(R.id.etMotivo);
        Button btnAceptar = view.findViewById(R.id.btnAceptar);
        Button btnCancelar = view.findViewById(R.id.btnCancelar);

        int id = cursor.getInt(cursor.getColumnIndexOrThrow("id_pedido"));
        int ubicacion = cursor.getInt(cursor.getColumnIndexOrThrow("id_ubicacion"));
        int combustible = cursor.getInt(cursor.getColumnIndexOrThrow("id_combustible"));
        double cantidad = cursor.getDouble(cursor.getColumnIndexOrThrow("cantidad"));
        String fecha = cursor.getString(cursor.getColumnIndexOrThrow("fecha"));

        tvInfo.setText("Pedido #" + id +
                "\nUbicación: " + ubicacion +
                "\nCombustible: " + combustible +
                "\nCantidad: " + cantidad +
                "\nFecha: " + fecha);

        btnAceptar.setOnClickListener(v -> {
            pedidoDAO.aceptarPedido(id); // 🔥 CAMBIO AQUÍ

            Toast.makeText(context, "Pedido aceptado", Toast.LENGTH_SHORT).show();

            Cursor nuevoCursor = pedidoDAO.obtenerPedidosPendientes();
            changeCursor(nuevoCursor);
        });

        btnCancelar.setOnClickListener(v -> {

            String motivo = etMotivo.getText().toString();

            if (motivo.isEmpty()) {
                Toast.makeText(context, "Ingrese motivo", Toast.LENGTH_SHORT).show();
                return;
            }

            pedidoDAO.cancelarPedido(id, motivo);

            Toast.makeText(context, "Pedido cancelado", Toast.LENGTH_SHORT).show();

            Cursor nuevoCursor = pedidoDAO.obtenerPedidosPendientes();
            changeCursor(nuevoCursor);
        });
    }
}
