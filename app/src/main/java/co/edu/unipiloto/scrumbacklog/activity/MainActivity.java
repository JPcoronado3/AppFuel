package co.edu.unipiloto.scrumbacklog.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

import co.edu.unipiloto.scrumbacklog.R;
import co.edu.unipiloto.scrumbacklog.activity.cliente.ConsultaActivity;
import co.edu.unipiloto.scrumbacklog.activity.cliente.HorariosActivity;
import co.edu.unipiloto.scrumbacklog.activity.distribuidor.ControlInventarioActivity;
import co.edu.unipiloto.scrumbacklog.activity.distribuidor.PedidosAEntregarActivity;
import co.edu.unipiloto.scrumbacklog.activity.distribuidor.PedidosPendientesActivity;
import co.edu.unipiloto.scrumbacklog.activity.operador.PedidosCanceladosActivity;
import co.edu.unipiloto.scrumbacklog.activity.operador.ProgramarPedidoActivity;
import co.edu.unipiloto.scrumbacklog.activity.logIn.LoginActivity;
import co.edu.unipiloto.scrumbacklog.activity.operador.InventarioActivity;
import co.edu.unipiloto.scrumbacklog.activity.operador.NotificadorActivity;
import co.edu.unipiloto.scrumbacklog.activity.operador.RecepcionCombustibleActivity;
import co.edu.unipiloto.scrumbacklog.activity.operador.ReguladorPreciosActivity;
import co.edu.unipiloto.scrumbacklog.activity.operador.SalidasActivity;

public class MainActivity extends AppCompatActivity {

    Button btnConsulta, btnInventario, btnSalidas, btnNotificador,
            btnRegulador, btnControl, btnProgramarPedido , btnHorarios,
            btnPedidosPendientes, btnPedidosCancelados, btnPedidosAEntregar, btnRecepcionCombustible, btnCerrarSesion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Referencias
        btnConsulta = findViewById(R.id.btnConsulta);
        btnInventario = findViewById(R.id.btnInventario);
        btnSalidas = findViewById(R.id.btnSalidas);
        btnNotificador = findViewById(R.id.btnNotificador);
        btnRegulador = findViewById(R.id.btnRegulador);
        btnControl = findViewById(R.id.btnControl);
        btnProgramarPedido = findViewById(R.id.btnProgramarPedido);
        btnPedidosPendientes = findViewById(R.id.btnPedidosPendientes);
        btnHorarios = findViewById(R.id.btnHorarios);
        btnPedidosCancelados = findViewById(R.id.btnPedidosCancelados);
        btnPedidosAEntregar = findViewById(R.id.btnPedidosAEntregar);
        btnRecepcionCombustible = findViewById(R.id.btnRecepcionCombustible);
        btnCerrarSesion = findViewById(R.id.btnCerrarSesion);

        // 🔥 CONFIGURAR PERMISOS POR ROL
        configurarAccesoPorRol();

        // Navegación
        btnConsulta.setOnClickListener(v ->
                startActivity(new Intent(this, ConsultaActivity.class)));

        btnInventario.setOnClickListener(v ->
                startActivity(new Intent(this, InventarioActivity.class)));

        btnSalidas.setOnClickListener(v ->
                startActivity(new Intent(this, SalidasActivity.class)));

        btnNotificador.setOnClickListener(v ->
                startActivity(new Intent(this, NotificadorActivity.class)));

        btnRegulador.setOnClickListener(v ->
                startActivity(new Intent(this, ReguladorPreciosActivity.class)));

        btnControl.setOnClickListener(v ->
                startActivity(new Intent(this, ControlInventarioActivity.class)));

        btnProgramarPedido.setOnClickListener(v ->
                startActivity(new Intent(this, ProgramarPedidoActivity.class)));

        btnPedidosPendientes.setOnClickListener(v ->
                startActivity(new Intent(this, PedidosPendientesActivity.class )));

        btnHorarios.setOnClickListener(v ->
                startActivity(new Intent(this, HorariosActivity.class)));

        btnPedidosCancelados.setOnClickListener(v ->
                startActivity(new Intent(this, PedidosCanceladosActivity.class)));

        btnPedidosAEntregar.setOnClickListener(v ->
                startActivity(new Intent(this, PedidosAEntregarActivity.class)));

        btnRecepcionCombustible.setOnClickListener(v ->
                startActivity(new Intent(this, RecepcionCombustibleActivity.class)));


        btnCerrarSesion.setOnClickListener(view -> {
            SharedPreferences prefs = getSharedPreferences("sesion", MODE_PRIVATE);
            prefs.edit().clear().apply();

            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }

    // =========================
    // 🔐 CONTROL DE ACCESO POR ROL
    // =========================
    private void configurarAccesoPorRol() {

        SharedPreferences prefs = getSharedPreferences("sesion", MODE_PRIVATE);
        String rol = prefs.getString("rol", "");

        // 🔒 Primero deshabilitamos TODO
        btnConsulta.setEnabled(false);
        btnInventario.setEnabled(false);
        btnSalidas.setEnabled(false);
        btnNotificador.setEnabled(false);
        btnRegulador.setEnabled(false);
        btnControl.setEnabled(false);
        btnProgramarPedido.setEnabled(false);
        btnPedidosPendientes.setEnabled(false);
        btnHorarios.setEnabled(false);
        btnPedidosCancelados.setEnabled(false);
        btnPedidosAEntregar.setEnabled(false);
        btnRecepcionCombustible.setEnabled(false);

        if (rol == null) return;

        switch (rol) {

            case "admin":
                btnConsulta.setEnabled(true);
                btnInventario.setEnabled(true);
                btnNotificador.setEnabled(true);
                btnControl.setEnabled(true);
                break;

            case "operador": // estación de servicio
                btnConsulta.setEnabled(true);
                btnInventario.setEnabled(true);
                btnSalidas.setEnabled(true);
                btnNotificador.setEnabled(true);
                btnRegulador.setEnabled(true);
                btnControl.setEnabled(true);
                btnProgramarPedido.setEnabled(true);
                btnPedidosCancelados.setEnabled(true);
                btnRecepcionCombustible.setEnabled(true);
                break;

            case "cliente":
                btnConsulta.setEnabled(true);
                btnHorarios.setEnabled(true);
                break;

            case "distribuidor":
                btnControl.setEnabled(true);
                btnSalidas.setEnabled(true);
                btnPedidosPendientes.setEnabled(true);
                btnPedidosAEntregar.setEnabled(true);
                break;
        }
    }
}