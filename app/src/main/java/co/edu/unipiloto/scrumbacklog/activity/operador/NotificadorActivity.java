package co.edu.unipiloto.scrumbacklog.activity.operador;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.Collections;
import co.edu.unipiloto.scrumbacklog.R;
import co.edu.unipiloto.scrumbacklog.activity.logIn.LoginActivity;
import co.edu.unipiloto.scrumbacklog.database.DAOFactory;
import co.edu.unipiloto.scrumbacklog.database.dao.InventarioDAO;
import co.edu.unipiloto.scrumbacklog.database.dao.UbicacionDAO;
import co.edu.unipiloto.scrumbacklog.database.dao.UsuarioDAO;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.*;
import androidx.appcompat.widget.Toolbar;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class NotificadorActivity extends AppCompatActivity {
    private static final String CHANNEL_ID = "canal_alertas";

    Spinner spCiudad, spZona;
    Button btnVerificar;
    TextView txtAlerta;

    DAOFactory factory;
    InventarioDAO inventarioDAO;
    UbicacionDAO ubicacionDAO;
    UsuarioDAO usuarioDAO;

    String rol;
    int idUbicacion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

            requestPermissions(
                    new String[]{android.Manifest.permission.POST_NOTIFICATIONS},
                    1
            );
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notificador);

        // ===== TOOLBAR =====
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Notificador");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        // ===================

        SharedPreferences prefs = getSharedPreferences("sesion", MODE_PRIVATE);
        rol = prefs.getString("rol", "");
        idUbicacion = prefs.getInt("id_ubicacion", -1);

        spCiudad = findViewById(R.id.spCiudad);
        spZona = findViewById(R.id.spZona);
        btnVerificar = findViewById(R.id.btnVerificar);
        txtAlerta = findViewById(R.id.txtAlerta);

        factory = new DAOFactory(this);
        inventarioDAO = factory.getInventarioDAO();
        ubicacionDAO = factory.getUbicacionDAO();
        usuarioDAO = factory.getUsuarioDAO();

        configurarPorRol();

        btnVerificar.setOnClickListener(v -> verificarInventario());

        crearCanalNotificacion();
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
        getMenuInflater().inflate(R.menu.menu_notificador, menu);
        return true;
    }

    // ===== ACCIONES TOOLBAR =====
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_info) {
            Toast.makeText(this, "Verifica niveles críticos de inventario", Toast.LENGTH_SHORT).show();
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

    private void crearCanalNotificacion() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            CharSequence nombre = "Alertas Combustible";
            String descripcion = "Notificaciones de niveles críticos";
            int importancia = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel channel =
                    new NotificationChannel(CHANNEL_ID, nombre, importancia);

            channel.setDescription(descripcion);

            NotificationManager notificationManager =
                    getSystemService(NotificationManager.class);

            notificationManager.createNotificationChannel(channel);
        }
    }

    // =====================================================
    // CONTROL POR ROL
    // =====================================================
    private void configurarPorRol() {

        if (rol.equalsIgnoreCase("CLIENTE")) {
            finish();
            return;
        }

        if (rol.equalsIgnoreCase("ADMIN")) {

            cargarCiudadesSpinner();

            spCiudad.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, android.view.View view, int position, long id) {
                    String ciudad = spCiudad.getSelectedItem().toString();
                    cargarZonasSpinner(ciudad);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {}
            });

            return;
        }

        if (rol.equalsIgnoreCase("OPERADOR")) {

            String[] ubicacion = usuarioDAO.obtenerUbicacionUsuario(idUbicacion);

            if (ubicacion != null) {

                spCiudad.setAdapter(new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_dropdown_item,
                        Collections.singletonList(ubicacion[0])
                ));
                spCiudad.setEnabled(false);

                spZona.setAdapter(new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_dropdown_item,
                        Collections.singletonList(ubicacion[1])
                ));
                spZona.setEnabled(false);
            }
        }
    }

    // =====================================================
    // VERIFICAR INVENTARIO
    // =====================================================
    private void verificarInventario() {

        String ciudad;
        String zona;

        if (rol.equalsIgnoreCase("OPERADOR")) {

            String[] ubicacion = usuarioDAO.obtenerUbicacionUsuario(idUbicacion);
            ciudad = ubicacion[0];
            zona = ubicacion[1];

        } else {

            if (spCiudad.getSelectedItem() == null || spZona.getSelectedItem() == null) {
                txtAlerta.setText("Seleccione ubicación válida");
                return;
            }

            ciudad = spCiudad.getSelectedItem().toString();
            zona = spZona.getSelectedItem().toString();
        }

        double diesel = inventarioDAO.obtenerInventario("Diesel", ciudad, zona);
        double corriente = inventarioDAO.obtenerInventario("Corriente", ciudad, zona);
        double extra = inventarioDAO.obtenerInventario("Extra", ciudad, zona);

        StringBuilder mensaje = new StringBuilder();
        StringBuilder alertaPopup = new StringBuilder();

        mensaje.append("📍 ")
                .append(ciudad)
                .append(" - ")
                .append(zona)
                .append("\n\n");

        // =========================
        // DIESEL
        // =========================
        if (diesel < 1000) {

            mensaje.append("⚠ Diesel crítico: ")
                    .append(diesel)
                    .append(" galones\n");

            alertaPopup.append("• Diesel crítico: ")
                    .append(diesel)
                    .append(" galones\n");

            mostrarNotificacion("Diesel", diesel, ciudad, zona);
        }

        // =========================
        // CORRIENTE
        // =========================
        if (corriente < 1000) {

            mensaje.append("⚠ Corriente crítico: ")
                    .append(corriente)
                    .append(" galones\n");

            alertaPopup.append("• Corriente crítico: ")
                    .append(corriente)
                    .append(" galones\n");

            mostrarNotificacion("Corriente", corriente, ciudad, zona);
        }

        // =========================
        // EXTRA
        // =========================
        if (extra < 1000) {

            mensaje.append("⚠ Extra crítico: ")
                    .append(extra)
                    .append(" galones\n");

            alertaPopup.append("• Extra crítico: ")
                    .append(extra)
                    .append(" galones\n");

            mostrarNotificacion("Extra", extra, ciudad, zona);
        }

        // =========================
        // TODO NORMAL
        // =========================
        if (diesel >= 1000 &&
                corriente >= 1000 &&
                extra >= 1000) {

            mensaje.append("✔ Inventario en niveles normales");
        }

        txtAlerta.setText(mensaje.toString());

        // =========================
        // MOSTRAR POPUP CENTRAL
        // =========================
        if (alertaPopup.length() > 0) {

            new AlertDialog.Builder(this)
                    .setTitle("⚠ ALERTA DE INVENTARIO")
                    .setMessage(
                            "Ubicación: " + ciudad + " - " + zona + "\n\n" +
                                    alertaPopup.toString()
                    )
                    .setPositiveButton("Aceptar", null)
                    .show();
        }
    }

    // =====================================================
    // SPINNERS
    // =====================================================
    private void cargarCiudadesSpinner() {

        ArrayList<String> ciudades = ubicacionDAO.obtenerCiudades();
        if (ciudades == null) ciudades = new ArrayList<>();

        spCiudad.setAdapter(new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                ciudades
        ));
    }

    private void cargarZonasSpinner(String ciudad) {

        ArrayList<String> zonas = ubicacionDAO.obtenerZonas(ciudad);
        if (zonas == null) zonas = new ArrayList<>();

        spZona.setAdapter(new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                zonas
        ));
    }

    private void mostrarNotificacion(String combustible,
                                     double cantidad,
                                     String ciudad,
                                     String zona) {

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_launcher_foreground)
                        .setContentTitle("⚠ Combustible Crítico")
                        .setContentText(
                                combustible + " en " +
                                        ciudad + " - " + zona +
                                        " está por debajo de 1000 galones"
                        )
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setAutoCancel(true);

        NotificationManagerCompat notificationManager =
                NotificationManagerCompat.from(this);

        int idNotificacion = (int) System.currentTimeMillis();

        // ============================
        // VALIDAR PERMISO ANDROID 13+
        // ============================
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {

                return;
            }
        }

        notificationManager.notify(idNotificacion, builder.build());
    }
}