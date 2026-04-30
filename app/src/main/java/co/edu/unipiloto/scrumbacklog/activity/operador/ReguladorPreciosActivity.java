package co.edu.unipiloto.scrumbacklog.activity.operador;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Collections;
import co.edu.unipiloto.scrumbacklog.R;
import co.edu.unipiloto.scrumbacklog.activity.logIn.LoginActivity;
import co.edu.unipiloto.scrumbacklog.database.DAOFactory;
import co.edu.unipiloto.scrumbacklog.database.dao.CombustibleDAO;
import co.edu.unipiloto.scrumbacklog.database.dao.PrecioDAO;
import co.edu.unipiloto.scrumbacklog.database.dao.UbicacionDAO;
import co.edu.unipiloto.scrumbacklog.database.dao.UsuarioDAO;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.*;
import androidx.appcompat.widget.Toolbar;
import java.util.ArrayList;

public class ReguladorPreciosActivity extends AppCompatActivity {

    private Spinner spCiudad, spLocalidad, spCombustible;
    private TextView txtPrecioActual;
    private EditText etNuevoPrecio;
    private Button btnActualizarPrecio;

    DAOFactory factory;
    CombustibleDAO combustibleDAO;
    PrecioDAO precioDAO;
    UbicacionDAO ubicacionDAO;
    UsuarioDAO usuarioDAO;

    String rol;
    int idUbicacion;

    boolean inicializado = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regulador_precios);

        // ===== TOOLBAR =====
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Regulador de Precios");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        // ===================

        SharedPreferences prefs = getSharedPreferences("sesion", MODE_PRIVATE);
        rol = prefs.getString("rol", "");
        idUbicacion = prefs.getInt("id_ubicacion", -1);

        factory = new DAOFactory(this);
        combustibleDAO = factory.getCombustibleDAO();
        precioDAO = factory.getPrecioDAO();
        ubicacionDAO = factory.getUbicacionDAO();
        usuarioDAO = factory.getUsuarioDAO();

        spCiudad = findViewById(R.id.spCiudad);
        spLocalidad = findViewById(R.id.spZona);
        spCombustible = findViewById(R.id.spCombustible);

        txtPrecioActual = findViewById(R.id.txtPrecioActual);
        etNuevoPrecio = findViewById(R.id.etNuevoPrecio);

        btnActualizarPrecio = findViewById(R.id.btnActualizarPrecio);

        cargarCombustibles();
        configurarSegunRol();
        configurarListeners();

        inicializado = true;

        btnActualizarPrecio.setOnClickListener(v -> actualizarPrecio());
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
        getMenuInflater().inflate(R.menu.menu_regulador, menu);
        return true;
    }

    // ===== ACCIONES TOOLBAR =====
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_info) {
            Toast.makeText(this, "Gestión de precios por ubicación", Toast.LENGTH_SHORT).show();
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

    // ---------------- ROLES ----------------

    private void configurarSegunRol() {

        if (rol.equalsIgnoreCase("OPERADOR")) {

            String[] ubicacion = usuarioDAO.obtenerUbicacionUsuario(idUbicacion);

            if (ubicacion != null) {

                spCiudad.setAdapter(new ArrayAdapter<>(this,
                        android.R.layout.simple_spinner_item,
                        Collections.singletonList(ubicacion[0])));

                spLocalidad.setAdapter(new ArrayAdapter<>(this,
                        android.R.layout.simple_spinner_item,
                        Collections.singletonList(ubicacion[1])));

                spCiudad.setEnabled(false);
                spLocalidad.setEnabled(false);
            }

        } else {
            // ADMIN → carga completa
            cargarCiudades();

            spCiudad.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String ciudad = spCiudad.getSelectedItem().toString();
                    cargarZonas(ciudad);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {}
            });
        }
    }

    // ---------------- LISTENERS ----------------

    private void configurarListeners() {

        AdapterView.OnItemSelectedListener listener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (inicializado) mostrarPrecioActual();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        };

        spCiudad.setOnItemSelectedListener(listener);
        spLocalidad.setOnItemSelectedListener(listener);
        spCombustible.setOnItemSelectedListener(listener);
    }

    // ---------------- CARGA ----------------

    private void cargarCombustibles() {
        spCombustible.setAdapter(new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                combustibleDAO.obtenerCombustibles()
        ));
    }

    private void cargarCiudades() {
        ArrayList<String> ciudades = ubicacionDAO.obtenerCiudades();
        if (ciudades == null) ciudades = new ArrayList<>();

        spCiudad.setAdapter(new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                ciudades
        ));
    }

    private void cargarZonas(String ciudad) {
        ArrayList<String> zonas = ubicacionDAO.obtenerZonas(ciudad);
        if (zonas == null) zonas = new ArrayList<>();

        spLocalidad.setAdapter(new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                zonas
        ));
    }

    // ---------------- LOGICA ----------------

    private void mostrarPrecioActual() {

        if (spCombustible.getSelectedItem() == null) return;

        String combustible = spCombustible.getSelectedItem().toString();
        double precio;

        if (rol.equalsIgnoreCase("OPERADOR")) {

            precio = precioDAO.obtenerPrecioPorUbicacion(combustible, idUbicacion);

        } else {

            if (spCiudad.getSelectedItem() == null || spLocalidad.getSelectedItem() == null)
                return;

            precio = precioDAO.obtenerPrecioZona(
                    combustible,
                    spCiudad.getSelectedItem().toString(),
                    spLocalidad.getSelectedItem().toString()
            );
        }

        txtPrecioActual.setText(
                precio < 0 ? "Precio no disponible" : "Precio actual: $" + precio
        );
    }

    // ---------------- UPDATE ----------------

    private void actualizarPrecio() {

        if (spCombustible.getSelectedItem() == null) return;

        double nuevoPrecio;

        try {
            nuevoPrecio = Double.parseDouble(etNuevoPrecio.getText().toString());
        } catch (Exception e) {
            Toast.makeText(this, "Precio inválido", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean ok;

        if (rol.equalsIgnoreCase("OPERADOR")) {

            ok = actualizarPrecioPorUbicacion(
                    spCombustible.getSelectedItem().toString(),
                    idUbicacion,
                    nuevoPrecio
            );

        } else {

            if (spCiudad.getSelectedItem() == null || spLocalidad.getSelectedItem() == null)
                return;

            ok = actualizarPrecioPorZona(
                    spCombustible.getSelectedItem().toString(),
                    spCiudad.getSelectedItem().toString(),
                    spLocalidad.getSelectedItem().toString(),
                    nuevoPrecio
            );
        }

        Toast.makeText(this, ok ? "Actualizado" : "Error", Toast.LENGTH_SHORT).show();

        mostrarPrecioActual();
    }

    private boolean actualizarPrecioPorZona(String tipo, String ciudad, String localidad, double precio) {
        try {
            SQLiteDatabase db = factory.getDatabase();

            db.execSQL(
                    "UPDATE precio_combustible SET precio=? " +
                            "WHERE id_combustible=(SELECT id_combustible FROM combustible WHERE nombre=?) " +
                            "AND id_ubicacion=(SELECT id_ubicacion FROM ubicacion WHERE ciudad=? AND localidad=?)",
                    new Object[]{precio, tipo, ciudad, localidad}
            );

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean actualizarPrecioPorUbicacion(String tipo, int idUbicacion, double precio) {
        try {
            SQLiteDatabase db = factory.getDatabase();

            db.execSQL(
                    "UPDATE precio_combustible SET precio=? " +
                            "WHERE id_combustible=(SELECT id_combustible FROM combustible WHERE nombre=?) " +
                            "AND id_ubicacion=?",
                    new Object[]{precio, tipo, idUbicacion}
            );

            return true;
        } catch (Exception e) {
            return false;
        }
    }
}