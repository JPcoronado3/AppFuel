package co.edu.unipiloto.scrumbacklog.activity.operador;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import co.edu.unipiloto.scrumbacklog.R;
import co.edu.unipiloto.scrumbacklog.activity.MainActivity;
import co.edu.unipiloto.scrumbacklog.database.dao.PedidoDAO;

public class RecepcionAdapter extends CursorAdapter {

    private PedidoDAO pedidoDAO;
    private int idUbicacionUsuario;

    public RecepcionAdapter(Context context, Cursor cursor, PedidoDAO pedidoDAO, int idUbicacionUsuario) {
        super(context, cursor, 0);
        this.pedidoDAO = pedidoDAO;
        this.idUbicacionUsuario = idUbicacionUsuario;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.item_recepcion, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        TextView tvInfo = view.findViewById(R.id.tvInfoRecepcion);
        Button btnConfirmar = view.findViewById(R.id.btnConfirmar);
        Button btnVolver = view.findViewById(R.id.btnVolver);

        int id = cursor.getInt(cursor.getColumnIndexOrThrow("id_pedido"));
        String ubicacion = cursor.getString(cursor.getColumnIndexOrThrow("ubicacion"));
        String combustible = cursor.getString(cursor.getColumnIndexOrThrow("combustible"));
        double cantidad = cursor.getDouble(cursor.getColumnIndexOrThrow("cantidad"));
        String fecha = cursor.getString(cursor.getColumnIndexOrThrow("fecha"));

        tvInfo.setText(
                "Pedido #" + id +
                        "\nUbicación: " + ubicacion +
                        "\nCombustible: " + combustible +
                        "\nCantidad: " + cantidad +
                        "\nFecha entrega: " + fecha
        );

        btnConfirmar.setOnClickListener(v -> {

            pedidoDAO.marcarComoRecibido(id);

            Toast.makeText(context, "Recepción confirmada", Toast.LENGTH_SHORT).show();

            // 🔥 RECARGAR SOLO LOS DE SU ESTACIÓN
            Cursor nuevoCursor = pedidoDAO.obtenerPedidosEntregadosPorUbicacion(idUbicacionUsuario);
            changeCursor(nuevoCursor);

            // 👉 REDIRECCIÓN
            Intent intent = new Intent(context, InventarioActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        });

        btnVolver.setOnClickListener(v -> {
            Intent intent = new Intent(context, MainActivity.class);
            context.startActivity(intent);
        });
    }
}