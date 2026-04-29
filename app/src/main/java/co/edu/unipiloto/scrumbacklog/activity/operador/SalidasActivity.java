package co.edu.unipiloto.scrumbacklog.activity.operador;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Collections;

import co.edu.unipiloto.scrumbacklog.R;
import co.edu.unipiloto.scrumbacklog.database.DAOFactory;
import co.edu.unipiloto.scrumbacklog.database.dao.InventarioDAO;
import co.edu.unipiloto.scrumbacklog.database.dao.MovimientoDAO;
import co.edu.unipiloto.scrumbacklog.database.dao.PrecioDAO;
import co.edu.unipiloto.scrumbacklog.database.dao.UbicacionDAO;
import co.edu.unipiloto.scrumbacklog.database.dao.UsuarioDAO;

public class SalidasActivity extends AppCompatActivity {

    TextView txtInventarioDisponible;
    Spinner spTipoCombustible, spCiudad, spZona;
    EditText etSalida;
    Button btnRetirar, btnVolver;
    ListView listHistorial;

    DAOFactory factory;
    InventarioDAO inventarioDAO;
    MovimientoDAO movimientoDAO;
    PrecioDAO precioDAO;
    UbicacionDAO ubicacionDAO;
    UsuarioDAO usuarioDAO;

    ArrayList<String> historial = new ArrayList<>();
    ArrayAdapter<String> adapterHistorial;

    String rol;
    int idUbicacion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_salidas);

        SharedPreferences prefs = getSharedPreferences("sesion", MODE_PRIVATE);
        rol = prefs.getString("rol", "");
        idUbicacion = prefs.getInt("id_ubicacion", -1);

        factory = new DAOFactory(this);
        inventarioDAO = factory.getInventarioDAO();
        movimientoDAO = factory.getMovimientoDAO();
        precioDAO = factory.getPrecioDAO();
        ubicacionDAO = factory.getUbicacionDAO();
        usuarioDAO = factory.getUsuarioDAO();

        txtInventarioDisponible = findViewById(R.id.txtInventarioDisponible);
        spTipoCombustible = findViewById(R.id.spTipoCombustible);
        spCiudad = findViewById(R.id.spCiudad);
        spZona = findViewById(R.id.spZona);
        etSalida = findViewById(R.id.etSalida);
        btnRetirar = findViewById(R.id.btnRetirar);
        btnVolver = findViewById(R.id.btnVolver);
        listHistorial = findViewById(R.id.listHistorial);

        String[] tipos = {"Corriente", "Extra", "Diesel"};

        ArrayAdapter<String> adapterTipo = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                tipos
        );
        spTipoCombustible.setAdapter(adapterTipo);

        configurarPorRol();

        // =========================
        // ADMIN / DISTRIBUIDOR / OPERADOR CIUDADES
        // =========================
        if (!rol.equalsIgnoreCase("OPERADOR")) {

            ArrayList<String> ciudades = ubicacionDAO.obtenerCiudades();

            ArrayAdapter<String> adapterCiudad = new ArrayAdapter<>(
                    this,
                    android.R.layout.simple_spinner_item,
                    ciudades
            );

            spCiudad.setAdapter(adapterCiudad);
        }

        // =========================
        // LISTENERS
        // =========================
        spCiudad.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, android.view.View view, int position, long id) {

                if (rol.equalsIgnoreCase("OPERADOR")) return;

                String ciudad = spCiudad.getSelectedItem().toString();

                ArrayList<String> zonas = ubicacionDAO.obtenerZonas(ciudad);

                ArrayAdapter<String> adapterZona = new ArrayAdapter<>(
                        SalidasActivity.this,
                        android.R.layout.simple_spinner_item,
                        zonas
                );

                spZona.setAdapter(adapterZona);

                actualizarInventarioUI();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        spTipoCombustible.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, android.view.View view, int position, long id) {
                actualizarInventarioUI();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        spZona.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, android.view.View view, int position, long id) {
                actualizarInventarioUI();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        adapterHistorial = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                historial
        );
        listHistorial.setAdapter(adapterHistorial);

        btnRetirar.setOnClickListener(v -> registrarSalida());
        btnVolver.setOnClickListener(v -> finish());
    }

    // =========================================================
    // CONTROL DE ROLES
    // =========================================================
    private void configurarPorRol() {

        if (rol.equalsIgnoreCase("OPERADOR")) {

            String[] ubicacion = usuarioDAO.obtenerUbicacionUsuario(idUbicacion);

            if (ubicacion != null) {

                spCiudad.setAdapter(new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_item,
                        Collections.singletonList(ubicacion[0])
                ));
                spCiudad.setEnabled(false);

                spZona.setAdapter(new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_item,
                        Collections.singletonList(ubicacion[1])
                ));
                spZona.setEnabled(false);
            }
        }

        // =====================================================
        // BLOQUEO DISTRIBUIDOR (NUEVO)
        // =====================================================
        if (rol.equalsIgnoreCase("DISTRIBUIDOR")) {

            btnRetirar.setEnabled(false);
            btnRetirar.setAlpha(0.4f);

            etSalida.setEnabled(false);

            Toast.makeText(this,
                    "Modo consulta: solo lectura",
                    Toast.LENGTH_SHORT).show();
        }

        if (rol.equalsIgnoreCase("CLIENTE")) {
            finish();
        }
    }

    // =========================================================
    // REGISTRAR SALIDA (BLOQUEO REAL)
    // =========================================================
    private void registrarSalida() {

        // 🔥 BLOQUEO REAL
        if (rol.equalsIgnoreCase("DISTRIBUIDOR")) {
            Toast.makeText(this,
                    "No tiene permisos para registrar salidas",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        String cantidadTexto = etSalida.getText().toString().trim();

        if (cantidadTexto.isEmpty()) {
            Toast.makeText(this, "Ingrese cantidad", Toast.LENGTH_SHORT).show();
            return;
        }

        double galones = Double.parseDouble(cantidadTexto);
        String tipo = spTipoCombustible.getSelectedItem().toString();

        String fecha = new java.text.SimpleDateFormat(
                "dd/MM/yyyy HH:mm",
                java.util.Locale.getDefault()
        ).format(new java.util.Date());

        boolean resultado;

        if (rol.equalsIgnoreCase("OPERADOR")) {

            String[] ubicacion = usuarioDAO.obtenerUbicacionUsuario(idUbicacion);

            double precio = precioDAO.obtenerPrecioZona(tipo, ubicacion[0], ubicacion[1]);

            double inventario = inventarioDAO.obtenerInventarioPorUbicacion(tipo, idUbicacion);

            if (galones > inventario) {
                Toast.makeText(this, "Inventario insuficiente", Toast.LENGTH_SHORT).show();
                return;
            }

            resultado = movimientoDAO.registrarSalidaPorUbicacion(
                    tipo, galones, precio, fecha, idUbicacion
            );

        } else {

            String ciudad = spCiudad.getSelectedItem().toString();
            String zona = spZona.getSelectedItem().toString();

            double precio = precioDAO.obtenerPrecioZona(tipo, ciudad, zona);

            double inventario = inventarioDAO.obtenerInventario(tipo, ciudad, zona);

            if (galones > inventario) {
                Toast.makeText(this, "Inventario insuficiente", Toast.LENGTH_SHORT).show();
                return;
            }

            int idUbic = ubicacionDAO.obtenerIdUbicacion(ciudad, zona);

            resultado = movimientoDAO.registrarSalidaPorUbicacion(
                    tipo, galones, precio, fecha, idUbic
            );
        }

        if (resultado) {

            historial.add(0, fecha + " | " + tipo + " | " + galones + " gal");
            adapterHistorial.notifyDataSetChanged();

            etSalida.setText("");
            actualizarInventarioUI();

            Toast.makeText(this, "Salida registrada", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Error al registrar", Toast.LENGTH_SHORT).show();
        }
    }

    // =========================================================
    // INVENTARIO UI
    // =========================================================
    private void actualizarInventarioUI() {

        String tipo = spTipoCombustible.getSelectedItem().toString();

        double inventario;

        if (rol.equalsIgnoreCase("OPERADOR")) {

            inventario = inventarioDAO.obtenerInventarioPorUbicacion(tipo, idUbicacion);

        } else {

            if (spCiudad.getSelectedItem() == null || spZona.getSelectedItem() == null) return;

            String ciudad = spCiudad.getSelectedItem().toString();
            String zona = spZona.getSelectedItem().toString();

            inventario = inventarioDAO.obtenerInventario(tipo, ciudad, zona);
        }

        txtInventarioDisponible.setText(inventario + " galones disponibles");
    }
}