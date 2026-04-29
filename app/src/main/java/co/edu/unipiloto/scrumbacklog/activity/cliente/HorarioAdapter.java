package co.edu.unipiloto.scrumbacklog.activity.cliente;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.view.*;
import android.widget.*;

import java.text.SimpleDateFormat;
import java.util.Date;

import co.edu.unipiloto.scrumbacklog.R;
import co.edu.unipiloto.scrumbacklog.activity.MainActivity;

public class HorarioAdapter extends CursorAdapter {

    public HorarioAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.item_horario, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        TextView tvNombre = view.findViewById(R.id.tvNombre);
        TextView tvHorario = view.findViewById(R.id.tvHorario);
        TextView tvEstado = view.findViewById(R.id.tvEstado);
        Button btnVolver = view.findViewById(R.id.btnVolver);


        String nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre"));
        String apertura = cursor.getString(cursor.getColumnIndexOrThrow("hora_apertura"));
        String cierre = cursor.getString(cursor.getColumnIndexOrThrow("hora_cierre"));

        tvNombre.setText(nombre);
        tvHorario.setText("Horario: " + apertura + " - " + cierre);

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            Date ahora = sdf.parse(sdf.format(new Date()));
            Date horaApertura = sdf.parse(apertura);
            Date horaCierre = sdf.parse(cierre);

            if (ahora.after(horaApertura) && ahora.before(horaCierre)) {
                tvEstado.setText("🟢 Abierto");
            } else {
                tvEstado.setText("🔴 Cerrado");
            }

        } catch (Exception e) {
            tvEstado.setText("Horario no disponible");
        }

        btnVolver.setOnClickListener(v -> {
            Intent intent = new Intent(context, MainActivity.class);
            context.startActivity(intent);
        });
    }
}