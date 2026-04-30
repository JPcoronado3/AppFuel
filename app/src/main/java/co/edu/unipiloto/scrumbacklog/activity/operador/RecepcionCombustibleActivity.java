package co.edu.unipiloto.scrumbacklog.activity.operador;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.*;
import androidx.appcompat.widget.Toolbar;
import co.edu.unipiloto.scrumbacklog.R;
import co.edu.unipiloto.scrumbacklog.activity.logIn.LoginActivity;
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

        // ===== TOOLBAR =====
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Recepción Combustible");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        // ===================

        listView = findViewById(R.id.listViewRecepcion);

        SharedPreferences prefs = getSharedPreferences("sesion", MODE_PRIVATE);
        idUbicacionUsuario = prefs.getInt("id_ubicacion", -1);

        DatabaseHelper dbHelper = new DatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        pedidoDAO = new PedidoDAO(db);

        cargarPedidos();
    }

    // ===== BOTÓN ← TOOLBAR =====
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    // ===== MENÚ TOOLBAR =====
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_recepcion, menu);
        return true;
    }

    // ===== ACCIONES TOOLBAR =====
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_info) {
            Toast.makeText(this, "Listado de pedidos entregados para recepción", Toast.LENGTH_SHORT).show();
            return true;

        } else if (item.getItemId() == R.id.action_refresh) {
            cargarPedidos();
            Toast.makeText(this, "Lista actualizada", Toast.LENGTH_SHORT).show();
            return true;
        } else if (item.getItemId() == R.id.action_logout) {
            cerrarSesion();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void cerrarSesion() {

        SharedPreferences prefs = getSharedPreferences("sesion", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();

        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

        finish();
    }

    // =========================================================
    // CARGAR PEDIDOS
    // =========================================================
    private void cargarPedidos() {

        Cursor cursor = pedidoDAO.obtenerPedidosEntregadosPorUbicacion(idUbicacionUsuario);

        adapter = new RecepcionAdapter(this, cursor, pedidoDAO, idUbicacionUsuario);
        listView.setAdapter(adapter);
    }
}