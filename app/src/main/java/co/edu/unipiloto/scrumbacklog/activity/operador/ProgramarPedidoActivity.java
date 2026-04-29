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
import co.edu.unipiloto.scrumbacklog.activity.MainActivity;
import co.edu.unipiloto.scrumbacklog.database.DatabaseHelper;
import co.edu.unipiloto.scrumbacklog.database.dao.PedidoDAO;

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

        btnVolver.setOnClickListener(v -> {
            Intent intent = new Intent(ProgramarPedidoActivity.this, MainActivity.class);
            startActivity(intent);
        });
    }

    private void cargarSpinners() {

        // =========================
        // UBICACIÓN BLOQUEADA
        // =========================
        String nombreEstacion = obtenerNombreEstacion(idUbicacionUsuario);

        ArrayAdapter<String> adapterUbicacion = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                Collections.singletonList(nombreEstacion)
        );

        spUbicacion.setAdapter(adapterUbicacion);
        spUbicacion.setEnabled(false);

        // =========================
        // COMBUSTIBLE NORMAL
        // =========================
        String[] combustibles = {
                "Corriente",
                "Extra",
                "Diesel"
        };

        ArrayAdapter<String> adapterCombustible = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                combustibles
        );

        spCombustible.setAdapter(adapterCombustible);
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

    private void mostrarDatePicker() {
        Calendar calendar = Calendar.getInstance();

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year1, month1, dayOfMonth) -> {

                    String fecha = year1 + "-" + (month1 + 1) + "-" + dayOfMonth;
                    etFecha.setText(fecha);

                }, year, month, day);

        datePickerDialog.show();
    }

    private void guardarPedido() {
        try {

            if (idUbicacionUsuario == -1) {
                Toast.makeText(this, "Error: usuario sin ubicación", Toast.LENGTH_SHORT).show();
                return;
            }

            // 🔥 CLAVE: NO USAR SPINNER
            int idUbicacion = idUbicacionUsuario;
            int idCombustible = spCombustible.getSelectedItemPosition() + 1;

            double cantidad = Double.parseDouble(etCantidad.getText().toString());
            String fecha = etFecha.getText().toString();

            if (fecha.isEmpty()) {
                Toast.makeText(this, "Seleccione una fecha", Toast.LENGTH_SHORT).show();
                return;
            }

            pedidoDAO.crearPedido(idUbicacion, idCombustible, cantidad, fecha);

            Toast.makeText(this, "Pedido programado correctamente", Toast.LENGTH_LONG).show();

            limpiarCampos();

        } catch (Exception e) {
            Toast.makeText(this, "Error: Verifique los datos", Toast.LENGTH_SHORT).show();
        }
    }

    private void limpiarCampos() {
        etCantidad.setText("");
        etFecha.setText("");
        spCombustible.setSelection(0);
    }
}