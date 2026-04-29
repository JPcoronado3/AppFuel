package co.edu.unipiloto.scrumbacklog.activity.distribuidor;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import co.edu.unipiloto.scrumbacklog.R;
import co.edu.unipiloto.scrumbacklog.activity.MainActivity;
import co.edu.unipiloto.scrumbacklog.database.dao.PedidoDAO;

public class PedidoEntregaAdapter extends CursorAdapter {

    private PedidoDAO pedidoDAO;

    public PedidoEntregaAdapter(Context context, Cursor cursor, PedidoDAO pedidoDAO) {
        super(context, cursor, 0);
        this.pedidoDAO = pedidoDAO;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.item_pedido_entrega, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        TextView tvInfo = view.findViewById(R.id.tvInfoEntrega);
        TextView tvContador = view.findViewById(R.id.tvContador);
        Button btnIniciar = view.findViewById(R.id.btnIniciarEntrega);
        Button btnCompletar = view.findViewById(R.id.btnCompletarEntrega);
        Button btnVolver = view.findViewById(R.id.btnVolver);

        int id = cursor.getInt(cursor.getColumnIndexOrThrow("id_pedido"));
        int idUbicacion = cursor.getInt(cursor.getColumnIndexOrThrow("id_ubicacion")); // 🔥 CLAVE
        String ubicacion = cursor.getString(cursor.getColumnIndexOrThrow("ubicacion"));
        String combustible = cursor.getString(cursor.getColumnIndexOrThrow("combustible"));
        double cantidad = cursor.getDouble(cursor.getColumnIndexOrThrow("cantidad"));

        tvInfo.setText(
                "Pedido #" + id +
                        "\nUbicación: " + ubicacion +
                        "\nCombustible: " + combustible +
                        "\nCantidad: " + cantidad
        );

        tvContador.setText("Pendiente");
        btnCompletar.setEnabled(false);

        btnIniciar.setOnClickListener(v -> {

            btnIniciar.setEnabled(false);

            int tiempoTotal = 3000;
            int interval = 100;

            new CountDownTimer(tiempoTotal, interval) {

                double restante = cantidad;

                @Override
                public void onTick(long millisUntilFinished) {
                    restante -= (cantidad / (tiempoTotal / interval));
                    if (restante < 0) restante = 0;

                    tvContador.setText("Surtido: " + (int) restante + " galones");
                }

                @Override
                public void onFinish() {
                    tvContador.setText("Completado");
                    btnCompletar.setEnabled(true);
                }

            }.start();
        });

        btnCompletar.setOnClickListener(v -> {

            String fechaActual = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                    .format(new Date());

            // 🔥 Se marca SOLO este pedido (con su ubicación asociada)
            pedidoDAO.marcarComoEntregado(id, fechaActual);

            Toast.makeText(context,
                    "Entrega enviada a: " + ubicacion,
                    Toast.LENGTH_SHORT).show();

            Cursor nuevoCursor = pedidoDAO.obtenerPedidosAceptados();
            changeCursor(nuevoCursor);
        });

        btnVolver.setOnClickListener(v -> {
            Intent intent = new Intent(context, MainActivity.class);
            context.startActivity(intent);
        });
    }
}