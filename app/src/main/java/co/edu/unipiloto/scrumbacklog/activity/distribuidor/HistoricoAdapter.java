package co.edu.unipiloto.scrumbacklog.activity.distribuidor;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import co.edu.unipiloto.scrumbacklog.R;

public class HistoricoAdapter extends CursorAdapter {

    public HistoricoAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        return LayoutInflater.from(context)
                .inflate(R.layout.item_historico, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        TextView tvHistorico = view.findViewById(R.id.tvHistorico);

        int idPedido = cursor.getInt(cursor.getColumnIndexOrThrow("id_pedido"));

        String ubicacion = cursor.getString(
                cursor.getColumnIndexOrThrow("ubicacion"));

        String combustible = cursor.getString(
                cursor.getColumnIndexOrThrow("combustible"));

        double cantidad = cursor.getDouble(
                cursor.getColumnIndexOrThrow("cantidad"));

        String estado = cursor.getString(
                cursor.getColumnIndexOrThrow("estado"));

        String fecha = cursor.getString(
                cursor.getColumnIndexOrThrow("fecha"));

        tvHistorico.setText(
                "Pedido #" + idPedido +
                        "\nUbicación: " + ubicacion +
                        "\nCombustible: " + combustible +
                        "\nCantidad: " + cantidad +
                        "\nEstado: " + estado +
                        "\nFecha: " + fecha
        );
    }
}