package co.edu.unipiloto.scrumbacklog.activity.operador;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import co.edu.unipiloto.scrumbacklog.R;

public class MovimientoAdapter extends CursorAdapter {

    public MovimientoAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        return LayoutInflater.from(context)
                .inflate(R.layout.item_movimiento, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        TextView txtTipo = view.findViewById(R.id.txtTipo);
        TextView txtCombustible = view.findViewById(R.id.txtCombustible);
        TextView txtGalones = view.findViewById(R.id.txtGalones);
        TextView txtTotal = view.findViewById(R.id.txtTotal);
        TextView txtFecha = view.findViewById(R.id.txtFecha);

        String tipo = cursor.getString(cursor.getColumnIndexOrThrow("tipo_movimiento"));
        String combustible = cursor.getString(cursor.getColumnIndexOrThrow("combustible"));
        double galones = cursor.getDouble(cursor.getColumnIndexOrThrow("galones"));
        double total = cursor.getDouble(cursor.getColumnIndexOrThrow("total"));
        String fecha = cursor.getString(cursor.getColumnIndexOrThrow("fecha"));

        txtTipo.setText("Movimiento: " + tipo);
        txtCombustible.setText("Combustible: " + combustible);
        txtGalones.setText("Galones: " + galones);
        txtTotal.setText("Total: $" + total);
        txtFecha.setText("Fecha: " + fecha);
    }
}