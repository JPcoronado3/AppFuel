package co.edu.unipiloto.scrumbacklog.activity.operador;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import co.edu.unipiloto.scrumbacklog.R;
import co.edu.unipiloto.scrumbacklog.activity.logIn.LoginActivity;
import co.edu.unipiloto.scrumbacklog.database.DAOFactory;
import co.edu.unipiloto.scrumbacklog.database.DatabaseHelper;
import co.edu.unipiloto.scrumbacklog.database.dao.CombustibleDAO;
import co.edu.unipiloto.scrumbacklog.database.dao.InventarioDAO;
import co.edu.unipiloto.scrumbacklog.database.dao.MovimientoDAO;
import co.edu.unipiloto.scrumbacklog.database.dao.PedidoDAO;
import co.edu.unipiloto.scrumbacklog.database.dao.PrecioDAO;
import co.edu.unipiloto.scrumbacklog.database.dao.UbicacionDAO;
import co.edu.unipiloto.scrumbacklog.database.dao.UsuarioDAO;
import android.widget.*;
import androidx.appcompat.widget.Toolbar;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


public class InventarioActivity extends AppCompatActivity {

    Spinner spCombustible, spCiudad, spZona;
    TextView txtInventarioTotal, txtInventarioDiesel, txtInventarioCorriente, txtInventarioExtra;

    DAOFactory factory;
    CombustibleDAO combustibleDAO;
    InventarioDAO inventarioDAO;
    MovimientoDAO movimientoDAO;
    PrecioDAO precioDAO;
    UbicacionDAO ubicacionDAO;
    UsuarioDAO usuarioDAO;
    ListView listPedidosRecibidos;

    PedidoDAO pedidoDAO;

    String rol;
    int idUbicacion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventario);

        // ===== TOOLBAR =====
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Inventario");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        // ===================

        SharedPreferences prefs = getSharedPreferences("sesion", MODE_PRIVATE);
        rol = prefs.getString("rol", "");
        idUbicacion = prefs.getInt("id_ubicacion", -1);

        factory = new DAOFactory(this);
        inventarioDAO = factory.getInventarioDAO();
        combustibleDAO = factory.getCombustibleDAO();
        movimientoDAO = factory.getMovimientoDAO();
        precioDAO = factory.getPrecioDAO();
        ubicacionDAO = factory.getUbicacionDAO();
        usuarioDAO = factory.getUsuarioDAO();

        spCombustible = findViewById(R.id.spCombustible);
        spCiudad = findViewById(R.id.spCiudad);
        spZona = findViewById(R.id.spZona);
        txtInventarioTotal = findViewById(R.id.txtInventarioTotal);
        txtInventarioDiesel = findViewById(R.id.txtInventarioDiesel);
        txtInventarioCorriente = findViewById(R.id.txtInventarioCorriente);
        txtInventarioExtra = findViewById(R.id.txtInventarioExtra);
        listPedidosRecibidos = findViewById(R.id.listPedidosRecibidos);

        DatabaseHelper dbHelper = new DatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        pedidoDAO = new PedidoDAO(db);

        cargarCombustiblesSpinner();

        if (rol.equalsIgnoreCase("ADMIN")) {

            cargarCiudadesSpinner();

            spCiudad.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String ciudad = spCiudad.getSelectedItem().toString();
                    cargarZonasSpinner(ciudad);
                    actualizarInventarioAdmin();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {}
            });

        } else if (rol.equalsIgnoreCase("OPERADOR")) {

            String[] ubicacion = usuarioDAO.obtenerUbicacionUsuario(idUbicacion);

            if (ubicacion != null) {
                ArrayList<String> ciudadList = new ArrayList<>();
                ciudadList.add(ubicacion[0]);

                spCiudad.setAdapter(new ArrayAdapter<>(this,
                        android.R.layout.simple_spinner_item, ciudadList));

                ArrayList<String> zonaList = new ArrayList<>();
                zonaList.add(ubicacion[1]);

                spZona.setAdapter(new ArrayAdapter<>(this,
                        android.R.layout.simple_spinner_item, zonaList));

                spCiudad.setEnabled(false);
                spZona.setEnabled(false);
            }

            actualizarInventarioOperador();
        }
        cargarPedidosPendientes();

    }

    // ===== TOOLBAR BACK =====
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    // ===== MENÚ TOOLBAR =====
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_inventario, menu);
        return true;
    }

    // ===== ACCIONES TOOLBAR =====
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_info) {
            Toast.makeText(this, "Gestión de inventario de combustible", Toast.LENGTH_SHORT).show();
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

    private void actualizarInventarioAdmin() {
        String ciudad = spCiudad.getSelectedItem().toString();

        double diesel = inventarioDAO.obtenerInventarioTotalPorCiudad("Diesel", ciudad);
        double corriente = inventarioDAO.obtenerInventarioTotalPorCiudad("Corriente", ciudad);
        double extra = inventarioDAO.obtenerInventarioTotalPorCiudad("Extra", ciudad);

        actualizarTextos(diesel, corriente, extra);
    }

    private void actualizarInventarioOperador() {
        double diesel = inventarioDAO.obtenerInventarioPorUbicacion("Diesel", idUbicacion);
        double corriente = inventarioDAO.obtenerInventarioPorUbicacion("Corriente", idUbicacion);
        double extra = inventarioDAO.obtenerInventarioPorUbicacion("Extra", idUbicacion);

        actualizarTextos(diesel, corriente, extra);
    }

    private void actualizarUI() {
        if (rol.equalsIgnoreCase("ADMIN")) {
            actualizarInventarioAdmin();
        } else {
            actualizarInventarioOperador();
        }
    }

    private void actualizarTextos(double diesel, double corriente, double extra) {
        double total = diesel + corriente + extra;

        txtInventarioDiesel.setText("Diesel: " + diesel + " gal");
        txtInventarioCorriente.setText("Corriente: " + corriente + " gal");
        txtInventarioExtra.setText("Extra: " + extra + " gal");
        txtInventarioTotal.setText("Total: " + total + " gal");
    }

    private void cargarCombustiblesSpinner() {
        spCombustible.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                combustibleDAO.obtenerCombustibles()));
    }

    private void cargarCiudadesSpinner() {
        spCiudad.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                ubicacionDAO.obtenerCiudades()));
    }

    private void cargarZonasSpinner(String ciudad) {
        spZona.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                ubicacionDAO.obtenerZonas(ciudad)));
    }

    private void cargarPedidosPendientes() {

        Cursor cursor = pedidoDAO.obtenerPedidosRecibidosPendientes(idUbicacion);

        PedidoInventarioAdapter adapter =
                new PedidoInventarioAdapter(
                        this,
                        cursor,
                        movimientoDAO,
                        pedidoDAO,
                        inventarioDAO,
                        idUbicacion,
                        this
                );

        listPedidosRecibidos.setAdapter(adapter);
    }
}