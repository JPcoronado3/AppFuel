package co.edu.unipiloto.scrumbacklog.activity.operador;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
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
import co.edu.unipiloto.scrumbacklog.database.dao.InventarioDAO;
import co.edu.unipiloto.scrumbacklog.database.dao.MovimientoDAO;
import co.edu.unipiloto.scrumbacklog.database.dao.PedidoDAO;

public class PedidoInventarioAdapter extends CursorAdapter {

    private MovimientoDAO movimientoDAO;
    private PedidoDAO pedidoDAO;
    private InventarioDAO inventarioDAO;
    private int idUbicacion;
    private Activity activity;

    public PedidoInventarioAdapter(
            Context context,
            Cursor cursor,
            MovimientoDAO movimientoDAO,
            PedidoDAO pedidoDAO,
            InventarioDAO inventarioDAO,
            int idUbicacion,
            Activity activity
    ) {
        super(context, cursor, 0);

        this.movimientoDAO = movimientoDAO;
        this.pedidoDAO = pedidoDAO;
        this.inventarioDAO = inventarioDAO;
        this.idUbicacion = idUbicacion;
        this.activity = activity;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        return LayoutInflater.from(context)
                .inflate(R.layout.item_pedido_inventario, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        TextView tvInfo = view.findViewById(R.id.tvInfoPedido);
        Button btnAgregar = view.findViewById(R.id.btnAgregarInventario);

        int idPedido = cursor.getInt(cursor.getColumnIndexOrThrow("id_pedido"));

        String combustible = cursor.getString(
                cursor.getColumnIndexOrThrow("combustible"));

        double cantidad = cursor.getDouble(
                cursor.getColumnIndexOrThrow("cantidad"));

        tvInfo.setText(
                "Pedido #" + idPedido +
                        "\nCombustible: " + combustible +
                        "\nCantidad: " + cantidad + " gal"
        );

        btnAgregar.setOnClickListener(v -> {

            String fecha = new SimpleDateFormat(
                    "dd/MM/yyyy HH:mm",
                    Locale.getDefault()
            ).format(new Date());

            boolean resultado =
                    movimientoDAO.registrarEntradaPorUbicacion(
                            combustible,
                            cantidad,
                            0,
                            fecha,
                            idUbicacion
                    );

            if (resultado) {

                pedidoDAO.marcarPedidoAgregadoInventario(idPedido);

                Toast.makeText(
                        context,
                        "Inventario actualizado",
                        Toast.LENGTH_SHORT
                ).show();

                activity.recreate();

            } else {

                Toast.makeText(
                        context,
                        "Error al agregar inventario",
                        Toast.LENGTH_SHORT
                ).show();
            }
        });
    }
}