package co.edu.unipiloto.scrumbacklog.activity.distribuidor;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import co.edu.unipiloto.scrumbacklog.R;
import co.edu.unipiloto.scrumbacklog.database.DatabaseHelper;
import co.edu.unipiloto.scrumbacklog.database.dao.PedidoDAO;

public class PedidosAEntregarActivity extends AppCompatActivity {

    private ListView listView;
    private PedidoDAO pedidoDAO;
    private PedidoEntregaAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedidos_aentregar);

        listView = findViewById(R.id.listViewEntregas);

        DatabaseHelper dbHelper = new DatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        pedidoDAO = new PedidoDAO(db);

        cargarPedidos();
    }

    private void cargarPedidos() {
        Cursor cursor = pedidoDAO.obtenerPedidosAceptados();
        adapter = new PedidoEntregaAdapter(this, cursor, pedidoDAO);
        listView.setAdapter(adapter);
    }
}