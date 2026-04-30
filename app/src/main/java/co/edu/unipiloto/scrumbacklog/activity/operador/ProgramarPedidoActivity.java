package co.edu.unipiloto.scrumbacklog.activity.operador;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Calendar;
import java.util.Collections;
import co.edu.unipiloto.scrumbacklog.R;
import co.edu.unipiloto.scrumbacklog.activity.logIn.LoginActivity;
import co.edu.unipiloto.scrumbacklog.database.DatabaseHelper;
import co.edu.unipiloto.scrumbacklog.database.dao.PedidoDAO;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.*;
import androidx.appcompat.widget.Toolbar;

public class ProgramarPedidoActivity extends AppCompatActivity {

    private EditText etCantidad, etFecha;
    private Button btnGuardar, btnFecha, btnVolver;
    private Spinner spUbicacion, spCombustible;

    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;
    private PedidoDAO pedidoDAO;

    private int idUbicacionUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_programar_pedido);

        // ===== TOOLBAR =====
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Programar Pedido");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        // ===================

        SharedPreferences prefs = getSharedPreferences("sesion", MODE_PRIVATE);
        idUbicacionUsuario = prefs.getInt("id_ubicacion", -1);

        spUbicacion = findViewById(R.id.spUbicacion);
        spCombustible = findViewById(R.id.spCombustible);

        etCantidad = findViewById(R.id.etCantidad);
        etFecha = findViewById(R.id.etFecha);

        btnGuardar = findViewById(R.id.btnGuardar);
        btnFecha = findViewById(R.id.btnSeleccionarFecha);
        btnVolver = findViewById(R.id.btnVolver);

        dbHelper = new DatabaseHelper(this);
        db = dbHelper.getWritableDatabase();
        pedidoDAO = new PedidoDAO(db);

        cargarSpinners();

        btnFecha.setOnClickListener(v -> mostrarDatePicker());
        btnGuardar.setOnClickListener(v -> guardarPedido());
        btnVolver.setOnClickListener(v -> finish());
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
        getMenuInflater().inflate(R.menu.menu_pedido, menu);
        return true;
    }

    // ===== ACCIONES TOOLBAR =====
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_info) {
            Toast.makeText(this, "Permite programar pedidos de combustible", Toast.LENGTH_SHORT).show();
            return true;

        } else if (item.getItemId() == R.id.action_logout) {
            cerrarSesion();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // ===== CERRAR SESIÓN =====
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
    // SPINNERS
    // =========================================================
    private void cargarSpinners() {

        String nombreEstacion = obtenerNombreEstacion(idUbicacionUsuario);

        spUbicacion.setAdapter(new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                Collections.singletonList(nombreEstacion)
        ));
        spUbicacion.setEnabled(false);

        String[] combustibles = {"Corriente", "Extra", "Diesel"};

        spCombustible.setAdapter(new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                combustibles
        ));
    }

    private String obtenerNombreEstacion(int idUbicacion) {

        Cursor cursor = db.rawQuery(
                "SELECT nombre FROM ubicacion WHERE id_ubicacion = ?",
                new String[]{String.valueOf(idUbicacion)}
        );

        if (cursor.moveToFirst()) {
            String nombre = cursor.getString(0);
            cursor.close();
            return nombre;
        }

        cursor.close();
        return "Desconocida";
    }

    // =========================================================
    // DATE PICKER
    // =========================================================
    private void mostrarDatePicker() {

        Calendar calendar = Calendar.getInstance();

        DatePickerDialog dialog = new DatePickerDialog(this,
                (view, year, month, day) -> {
                    String fecha = year + "-" + (month + 1) + "-" + day;
                    etFecha.setText(fecha);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        dialog.show();
    }

    // =========================================================
    // GUARDAR PEDIDO
    // =========================================================
    private void guardarPedido() {

        if (idUbicacionUsuario == -1) {
            Toast.makeText(this, "Error: usuario sin ubicación", Toast.LENGTH_SHORT).show();
            return;
        }

        String cantidadTexto = etCantidad.getText().toString().trim();
        String fecha = etFecha.getText().toString().trim();

        if (cantidadTexto.isEmpty()) {
            Toast.makeText(this, "Ingrese cantidad", Toast.LENGTH_SHORT).show();
            return;
        }

        if (fecha.isEmpty()) {
            Toast.makeText(this, "Seleccione una fecha", Toast.LENGTH_SHORT).show();
            return;
        }

        double cantidad;

        try {
            cantidad = Double.parseDouble(cantidadTexto);
        } catch (Exception e) {
            Toast.makeText(this, "Cantidad inválida", Toast.LENGTH_SHORT).show();
            return;
        }

        int idCombustible = spCombustible.getSelectedItemPosition() + 1;

        try {
            pedidoDAO.crearPedido(idUbicacionUsuario, idCombustible, cantidad, fecha);
            Toast.makeText(this, "Pedido programado correctamente", Toast.LENGTH_LONG).show();
            limpiarCampos();

        } catch (Exception e) {
            Toast.makeText(this, "Error al guardar el pedido", Toast.LENGTH_SHORT).show();
        }
    }

    private void limpiarCampos() {
        etCantidad.setText("");
        etFecha.setText("");
        spCombustible.setSelection(0);
    }
}