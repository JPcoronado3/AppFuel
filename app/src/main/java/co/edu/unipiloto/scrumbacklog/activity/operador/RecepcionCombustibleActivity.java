package co.edu.unipiloto.scrumbacklog.activity.operador;

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

public class RecepcionCombustibleActivity extends AppCompatActivity {

    private ListView listView;
    private PedidoDAO pedidoDAO;
    private RecepcionAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recepcion_combustible);

        listView = findViewById(R.id.listViewRecepcion);

        DatabaseHelper dbHelper = new DatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        pedidoDAO = new PedidoDAO(db);

        cargarPedidos();
    }

    private void cargarPedidos() {
        Cursor cursor = pedidoDAO.obtenerPedidosEntregados();
        adapter = new RecepcionAdapter(this, cursor, pedidoDAO);
        listView.setAdapter(adapter);
    }
}