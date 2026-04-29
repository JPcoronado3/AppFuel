package co.edu.unipiloto.scrumbacklog.activity.logIn;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import java.util.*;

import co.edu.unipiloto.scrumbacklog.R;
import co.edu.unipiloto.scrumbacklog.database.DAOFactory;
import co.edu.unipiloto.scrumbacklog.database.DatabaseHelper;
import co.edu.unipiloto.scrumbacklog.database.dao.UsuarioDAO;
import co.edu.unipiloto.scrumbacklog.model.Usuario;

public class RegisterActivity extends AppCompatActivity {

    EditText etNombre, etUsuario, etDireccion, etCorreo, etPassword, etPasswordConfirm, etFecha;
    RadioGroup rgGenero;
    Spinner spinnerEstacion;
    Button btnRegistrar, btnVolver;

    DatabaseHelper dbHelper;
    SQLiteDatabase db;

    UsuarioDAO usuarioDAO;
    DAOFactory factory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        dbHelper = new DatabaseHelper(this);
        db = dbHelper.getWritableDatabase();

        factory = new DAOFactory(this);
        usuarioDAO = factory.getUsuarioDAO();

        // Referencias
        etNombre = findViewById(R.id.etNombre);
        etUsuario = findViewById(R.id.etUsuario);
        etDireccion = findViewById(R.id.etDireccion);
        etCorreo = findViewById(R.id.etCorreo);
        etPassword = findViewById(R.id.etPassword);
        etPasswordConfirm = findViewById(R.id.etPasswordConfirm);
        etFecha = findViewById(R.id.etFecha);

        rgGenero = findViewById(R.id.rgGenero);
        spinnerEstacion = findViewById(R.id.spinnerEstacion);

        btnRegistrar = findViewById(R.id.btnRegistrar);
        btnVolver = findViewById(R.id.btnVolver);

        cargarEstaciones();

        btnRegistrar.setOnClickListener(v -> registrarUsuario());
        btnVolver.setOnClickListener(v -> {
            Intent intent = new Intent(this, LoginScreenActivity.class);
            startActivity(intent);
        });
    }

    // =========================
    // CARGAR ESTACIONES
    // =========================
    private void cargarEstaciones() {

        ArrayList<String> estaciones = new ArrayList<>();

        Cursor cursor = db.rawQuery("SELECT nombre FROM ubicacion", null);

        while (cursor.moveToNext()) {
            estaciones.add(cursor.getString(0));
        }
        cursor.close();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, estaciones);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEstacion.setAdapter(adapter);
    }

    // =========================
    // REGISTRAR USUARIO
    // =========================
    private void registrarUsuario() {

        String nombre = etNombre.getText().toString().trim();
        String usuarioTxt = etUsuario.getText().toString().trim();
        String direccion = etDireccion.getText().toString().trim();
        String correo = etCorreo.getText().toString().trim().toLowerCase();
        String password = etPassword.getText().toString().trim();
        String confirm = etPasswordConfirm.getText().toString().trim();
        String fecha = etFecha.getText().toString().trim();

        if (nombre.isEmpty() || usuarioTxt.isEmpty() || correo.isEmpty()
                || password.isEmpty() || confirm.isEmpty()) {

            Toast.makeText(this, "Complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirm)) {
            Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
            return;
        }

        if (spinnerEstacion.getSelectedItem() == null) {
            Toast.makeText(this, "Seleccione estación", Toast.LENGTH_SHORT).show();
            return;
        }

        int selectedId = rgGenero.getCheckedRadioButtonId();
        String genero = "";

        if (selectedId != -1) {
            RadioButton rb = findViewById(selectedId);
            genero = rb.getText().toString();
        }

        String nombreEstacion = spinnerEstacion.getSelectedItem().toString();

        Cursor cursor = db.rawQuery(
                "SELECT id_ubicacion FROM ubicacion WHERE nombre=?",
                new String[]{nombreEstacion}
        );

        int idUbicacion = -1;

        if (cursor.moveToFirst()) {
            idUbicacion = cursor.getInt(0);
        }
        cursor.close();

        if (idUbicacion == -1) {
            Toast.makeText(this, "Error con la estación", Toast.LENGTH_SHORT).show();
            return;
        }

        String rol = "operador";

        Usuario nuevo = new Usuario();
        nuevo.setNombre(nombre);
        nuevo.setUsuario(usuarioTxt);
        nuevo.setCorreo(correo);
        nuevo.setDireccion(direccion);
        nuevo.setPassword(password);
        nuevo.setRol(rol);
        nuevo.setIdUbicacion(idUbicacion);
        nuevo.setFechaNacimiento(fecha);
        nuevo.setGenero(genero);
        nuevo.setVerificado(1); // opcional

        long resultado = usuarioDAO.insertarUsuario(nuevo);

        if (resultado != -1) {
            Toast.makeText(this, "Usuario registrado correctamente", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Error al registrar", Toast.LENGTH_SHORT).show();
        }
    }
}