package co.edu.unipiloto.scrumbacklog.activity.cliente;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

import co.edu.unipiloto.scrumbacklog.R;
import co.edu.unipiloto.scrumbacklog.activity.logIn.LoginActivity;
import co.edu.unipiloto.scrumbacklog.database.DAOFactory;
import co.edu.unipiloto.scrumbacklog.database.dao.CombustibleDAO;
import co.edu.unipiloto.scrumbacklog.database.dao.InventarioDAO;
import co.edu.unipiloto.scrumbacklog.database.dao.MovimientoDAO;
import co.edu.unipiloto.scrumbacklog.database.dao.PrecioDAO;
import co.edu.unipiloto.scrumbacklog.database.dao.UbicacionDAO;
import co.edu.unipiloto.scrumbacklog.database.dao.UsuarioDAO;
import android.widget.*;
import androidx.appcompat.widget.Toolbar;

public class ConsultaActivity extends AppCompatActivity {

    Spinner spTipoCombustible, spCiudad, spZona;
    Button btnCalcular, btnCalcularGalones, btnVolver;
    TextView txtResultado, txtResultadoGalones;
    EditText etGalones;

    DAOFactory factory;
    CombustibleDAO combustibleDAO;
    InventarioDAO inventarioDAO;
    MovimientoDAO movimientoDAO;
    PrecioDAO precioDAO;
    UbicacionDAO ubicacionDAO;
    UsuarioDAO usuarioDAO;

    String rol;
    int idUbicacion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consulta);

        // ===== TOOLBAR =====
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Consulta");
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

        spTipoCombustible = findViewById(R.id.spTipoCombustible);
        spCiudad = findViewById(R.id.spCiudad);
        spZona = findViewById(R.id.spZona);
        btnCalcular = findViewById(R.id.btnCalcular);
        btnVolver = findViewById(R.id.btnVolver);
        btnCalcularGalones = findViewById(R.id.calcularGalones);
        etGalones = findViewById(R.id.etGalones);
        txtResultadoGalones = findViewById(R.id.txtResultadoGalones);
        txtResultado = findViewById(R.id.txtResultado);

        ArrayAdapter<String> adapterComb = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                combustibleDAO.obtenerCombustibles()
        );
        spTipoCombustible.setAdapter(adapterComb);

        if (rol.equalsIgnoreCase("OPERADOR")) {

            String[] ubicacion = usuarioDAO.obtenerUbicacionUsuario(idUbicacion);

            if (ubicacion != null) {

                String ciudadOperador = ubicacion[0];
                String zonaOperador = ubicacion[1];

                ArrayList<String> listaCiudad = new ArrayList<>();
                listaCiudad.add(ciudadOperador);

                spCiudad.setAdapter(new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_item,
                        listaCiudad
                ));

                ArrayList<String> listaZona = new ArrayList<>();
                listaZona.add(zonaOperador);

                spZona.setAdapter(new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_item,
                        listaZona
                ));

                spCiudad.setEnabled(false);
                spZona.setEnabled(false);
            }

        } else {
            spCiudad.setAdapter(new ArrayAdapter<>(
                    this,
                    android.R.layout.simple_spinner_item,
                    ubicacionDAO.obtenerCiudades()
            ));

            spCiudad.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String ciudad = spCiudad.getSelectedItem().toString();

                    spZona.setAdapter(new ArrayAdapter<>(
                            ConsultaActivity.this,
                            android.R.layout.simple_spinner_item,
                            ubicacionDAO.obtenerZonas(ciudad)
                    ));
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {}
            });
        }

        btnCalcular.setOnClickListener(view -> {

            String tipo = spTipoCombustible.getSelectedItem().toString();
            double precio;

            if (rol.equalsIgnoreCase("ADMIN")) {
                String ciudad = spCiudad.getSelectedItem().toString();
                String zona = spZona.getSelectedItem().toString();
                precio = precioDAO.obtenerPrecioZona(tipo, ciudad, zona);
            } else {
                precio = precioDAO.obtenerPrecioPorUbicacion(tipo, idUbicacion);
            }

            txtResultado.setText("Precio: $" + precio);
        });

        btnCalcularGalones.setOnClickListener(view -> {

            String galonesTexto = etGalones.getText().toString().trim();

            if (galonesTexto.isEmpty()) {
                Toast.makeText(this, "Ingrese galones", Toast.LENGTH_SHORT).show();
                return;
            }

            double galones = Double.parseDouble(galonesTexto);
            String tipo = spTipoCombustible.getSelectedItem().toString();

            double precio;

            if (rol.equalsIgnoreCase("ADMIN")) {
                String ciudad = spCiudad.getSelectedItem().toString();
                String zona = spZona.getSelectedItem().toString();
                precio = precioDAO.obtenerPrecioZona(tipo, ciudad, zona);
            } else {
                precio = precioDAO.obtenerPrecioPorUbicacion(tipo, idUbicacion);
            }

            txtResultadoGalones.setText("Total: $" + (galones * precio));
        });

        btnVolver.setOnClickListener(view -> finish());
    }

    // ===== FLECHA ← TOOLBAR =====
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    // ===== MENÚ =====
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_consulta, menu);
        return true;
    }

    // ===== ACCIONES =====
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_info) {
            Toast.makeText(this, "Consulta de precios", Toast.LENGTH_SHORT).show();
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
}