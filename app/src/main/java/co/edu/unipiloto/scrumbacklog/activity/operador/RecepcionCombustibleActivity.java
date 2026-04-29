package co.edu.unipiloto.scrumbacklog.activity.operador;

import android.content.SharedPreferences;
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

    private int idUbicacionUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recepcion_combustible);

        listView = findViewById(R.id.listViewRecepcion);

        SharedPreferences prefs = getSharedPreferences("sesion", MODE_PRIVATE);
        idUbicacionUsuario = prefs.getInt("id_ubicacion", -1);

        DatabaseHelper dbHelper = new DatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        pedidoDAO = new PedidoDAO(db);

        cargarPedidos();
    }

    private void cargarPedidos() {
        Cursor cursor = pedidoDAO.obtenerPedidosEntregadosPorUbicacion(idUbicacionUsuario);

        adapter = new RecepcionAdapter(this, cursor, pedidoDAO, idUbicacionUsuario);
        listView.setAdapter(adapter);
    }
}