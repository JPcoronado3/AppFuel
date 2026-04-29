package co.edu.unipiloto.scrumbacklog.activity.logIn;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import co.edu.unipiloto.scrumbacklog.R;
import co.edu.unipiloto.scrumbacklog.activity.MainActivity;
import co.edu.unipiloto.scrumbacklog.database.DAOFactory;
import co.edu.unipiloto.scrumbacklog.database.dao.UsuarioDAO;
import co.edu.unipiloto.scrumbacklog.model.Usuario;

public class LoginActivity extends AppCompatActivity {

    private EditText etCorreoLogin, etPasswordLogin;
    private Button btnLogin, btnVolver;
    private UsuarioDAO usuarioDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etCorreoLogin = findViewById(R.id.etCorreoLogin);
        etPasswordLogin = findViewById(R.id.etPasswordLogin);
        btnLogin = findViewById(R.id.btnLogin);
        btnVolver = findViewById(R.id.btnVolver);

        DAOFactory factory = new DAOFactory(this);
        usuarioDAO = factory.getUsuarioDAO();

        btnLogin.setOnClickListener(v -> login());
        btnVolver.setOnClickListener(v -> {
            Intent intent = new Intent(this, LoginScreenActivity.class);
            startActivity(intent);
        });

    }

    private void login() {

        String correo = etCorreoLogin.getText().toString().trim().toLowerCase();
        String password = etPasswordLogin.getText().toString().trim();

        if (correo.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        if (usuarioDAO.validarLogin(correo, password)) {

            Usuario usuario = usuarioDAO.obtenerUsuario(correo);

            if (usuario != null) {

                SharedPreferences prefs = getSharedPreferences("sesion", MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();

                editor.putInt("id_usuario", usuario.getId());
                editor.putString("rol", usuario.getRol());
                editor.putInt("id_ubicacion", usuario.getIdUbicacion());

                editor.apply();

                Toast.makeText(this, "Login exitoso", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();

            } else {
                Toast.makeText(this, "Error al obtener usuario", Toast.LENGTH_SHORT).show();
            }

        } else {
            Toast.makeText(this, "Credenciales incorrectas", Toast.LENGTH_SHORT).show();
        }
    }
}