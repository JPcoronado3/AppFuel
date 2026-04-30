package co.edu.unipiloto.scrumbacklog.activity.distribuidor;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;

import co.edu.unipiloto.scrumbacklog.R;
import co.edu.unipiloto.scrumbacklog.activity.logIn.LoginActivity;
import co.edu.unipiloto.scrumbacklog.database.DAOFactory;
import co.edu.unipiloto.scrumbacklog.database.dao.InventarioDAO;
import co.edu.unipiloto.scrumbacklog.database.dao.MovimientoDAO;
import co.edu.unipiloto.scrumbacklog.database.dao.UbicacionDAO;
import co.edu.unipiloto.scrumbacklog.database.dao.UsuarioDAO;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.*;
import androidx.appcompat.widget.Toolbar;

public class ControlInventarioActivity extends AppCompatActivity {

    private LinearLayout layoutInventario, layoutHistorial;
    private Spinner spFiltroCombustible, spFiltroCiudad, spFiltroEstacion;

    private ArrayList<String> ciudades;
    private ArrayList<String> estaciones;

    DAOFactory factory;
    InventarioDAO inventarioDAO;
    MovimientoDAO movimientoDAO;
    UbicacionDAO ubicacionDAO;
    UsuarioDAO usuarioDAO;

    String rol;
    int idUbicacionUsuario;

    boolean inicializado = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control_inventario);

        // ===== TOOLBAR =====
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Control Inventario");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        // ===================

        factory = new DAOFactory(this);

        inventarioDAO = factory.getInventarioDAO();
        movimientoDAO = factory.getMovimientoDAO();
        ubicacionDAO = factory.getUbicacionDAO();
        usuarioDAO = factory.getUsuarioDAO();

        SharedPreferences prefs = getSharedPreferences("sesion", MODE_PRIVATE);
        rol = prefs.getString("rol", "");
        idUbicacionUsuario = prefs.getInt("id_ubicacion", -1);

        spFiltroCombustible = findViewById(R.id.spFiltroCombustible);
        spFiltroCiudad = findViewById(R.id.spFiltroCiudad);
        spFiltroEstacion = findViewById(R.id.spFiltroEstacion);
        layoutInventario = findViewById(R.id.layoutInventario);
        layoutHistorial = findViewById(R.id.layoutHistorial);

        configurarPorRol();
        cargarCombustibles();
        configurarListeners();

        inicializado = true;

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
        getMenuInflater().inflate(R.menu.menu_control_inventario, menu);
        return true;
    }

    // ===== ACCIONES TOOLBAR =====
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_info) {
            Toast.makeText(this, "Consulta de inventario y movimientos", Toast.LENGTH_SHORT).show();
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
    // CONFIGURACIÓN POR ROL
    // =========================================================
    private void configurarPorRol() {

        if (rol.equalsIgnoreCase("CLIENTE")) {
            finish();
            return;
        }

        if (rol.equalsIgnoreCase("ADMIN") || rol.equalsIgnoreCase("DISTRIBUIDOR")) {

            cargarCiudades();

            spFiltroCiudad.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String ciudad = spFiltroCiudad.getSelectedItem().toString();
                    cargarEstaciones(ciudad);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {}
            });

            spFiltroEstacion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (inicializado) refrescarVista();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {}
            });

            return;
        }

        if (rol.equalsIgnoreCase("OPERADOR")) {

            String[] ubicacion = usuarioDAO.obtenerUbicacionUsuario(idUbicacionUsuario);

            if (ubicacion != null) {

                spFiltroCiudad.setAdapter(new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_dropdown_item,
                        Collections.singletonList(ubicacion[0])
                ));
                spFiltroCiudad.setEnabled(false);

                spFiltroEstacion.setAdapter(new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_dropdown_item,
                        Collections.singletonList(ubicacion[1])
                ));
                spFiltroEstacion.setEnabled(false);
            }
        }
    }

    private void cargarCombustibles() {

        String[] combustibles = {"Todos", "Corriente", "Extra", "Diesel"};

        spFiltroCombustible.setAdapter(new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                combustibles
        ));
    }

    private void cargarCiudades() {
        ciudades = ubicacionDAO.obtenerCiudades();
        if (ciudades == null) ciudades = new ArrayList<>();

        spFiltroCiudad.setAdapter(new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                ciudades
        ));
    }

    private void cargarEstaciones(String ciudad) {
        estaciones = ubicacionDAO.obtenerZonas(ciudad);
        if (estaciones == null) estaciones = new ArrayList<>();

        spFiltroEstacion.setAdapter(new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                estaciones
        ));
    }

    private void configurarListeners() {

        spFiltroCombustible.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (inicializado) refrescarVista();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void refrescarVista() {

        if (spFiltroCiudad.getSelectedItem() == null ||
                spFiltroEstacion.getSelectedItem() == null) return;

        mostrarInventario();
        mostrarHistorial();
    }

    private void mostrarInventario() {

        layoutInventario.removeAllViews();

        String combustible = spFiltroCombustible.getSelectedItem().toString();
        String ciudad = spFiltroCiudad.getSelectedItem().toString();
        String estacion = spFiltroEstacion.getSelectedItem().toString();

        String[] tipos = combustible.equals("Todos")
                ? new String[]{"Corriente", "Extra", "Diesel"}
                : new String[]{combustible};

        for (String tipo : tipos) {

            double cantidad = inventarioDAO.obtenerInventario(tipo, ciudad, estacion);

            TextView tv = new TextView(this);
            tv.setText(tipo + ": " + cantidad + " galones");
            tv.setTextSize(16f);

            layoutInventario.addView(tv);
        }
    }

    private void mostrarHistorial() {

        layoutHistorial.removeAllViews();

        String ciudad = spFiltroCiudad.getSelectedItem().toString();
        String estacion = spFiltroEstacion.getSelectedItem().toString();

        int idUbicacion = ubicacionDAO.obtenerIdUbicacion(ciudad, estacion);

        ArrayList<String> movimientos =
                movimientoDAO.obtenerMovimientosPorUbicacion(idUbicacion);

        if (movimientos == null) return;

        int limit = Math.min(movimientos.size(), 10);

        for (int i = 0; i < limit; i++) {

            TextView tv = new TextView(this);
            tv.setText(movimientos.get(i));

            layoutHistorial.addView(tv);
        }
    }
}