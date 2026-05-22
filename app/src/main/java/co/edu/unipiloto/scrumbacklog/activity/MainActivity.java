package co.edu.unipiloto.scrumbacklog.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

import co.edu.unipiloto.scrumbacklog.R;
import co.edu.unipiloto.scrumbacklog.activity.cliente.ConsultaActivity;
import co.edu.unipiloto.scrumbacklog.activity.cliente.HorariosActivity;
import co.edu.unipiloto.scrumbacklog.activity.cliente.MapaEstacionesActivity;
import co.edu.unipiloto.scrumbacklog.activity.cliente.SubsidioActivity;
import co.edu.unipiloto.scrumbacklog.activity.distribuidor.ControlInventarioActivity;
import co.edu.unipiloto.scrumbacklog.activity.distribuidor.HistoricoDistribuidorActivity;
import co.edu.unipiloto.scrumbacklog.activity.distribuidor.PedidosAEntregarActivity;
import co.edu.unipiloto.scrumbacklog.activity.distribuidor.PedidosPendientesActivity;
import co.edu.unipiloto.scrumbacklog.activity.operador.HistorialOperadorActivity;
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
            btnPedidosPendientes, btnPedidosCancelados, btnPedidosAEntregar,
            btnRecepcionCombustible,btnMapa,btnCerrarSesion, btnHistoricoDistribuidor,
            btnHistorialOperador, btnSubsidios;

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
        btnMapa = findViewById(R.id.btnMapa);
        btnHistoricoDistribuidor = findViewById(R.id.btnHistoricoDistribiudor);
        btnHistorialOperador = findViewById(R.id.btnHistoricoOperador);
        btnSubsidios = findViewById(R.id.btnSubsidios);
        btnCerrarSesion = findViewById(R.id.btnCerrarSesion);

        configurarAccesoPorRol();

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

        btnMapa.setOnClickListener(v ->
                startActivity(new Intent(this, MapaEstacionesActivity.class)));

        btnHistoricoDistribuidor.setOnClickListener(v ->
                startActivity(new Intent(this, HistoricoDistribuidorActivity.class)));

        btnHistorialOperador.setOnClickListener(v ->
                startActivity(new Intent(this, HistorialOperadorActivity.class)));

        btnSubsidios.setOnClickListener(v ->
                startActivity(new Intent(this,SubsidioActivity.class)));

        btnCerrarSesion.setOnClickListener(view -> {
            SharedPreferences prefs = getSharedPreferences("sesion", MODE_PRIVATE);
            prefs.edit().clear().apply();

            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }

    private void configurarAccesoPorRol() {

        SharedPreferences prefs = getSharedPreferences("sesion", MODE_PRIVATE);
        String rol = prefs.getString("rol", "");

        btnConsulta.setVisibility(View.GONE);
        btnConsulta.setEnabled(false);
        btnInventario.setVisibility(View.GONE);
        btnInventario.setEnabled(false);
        btnSalidas.setVisibility(View.GONE);
        btnSalidas.setEnabled(false);
        btnNotificador.setVisibility(View.GONE);
        btnNotificador.setEnabled(false);
        btnRegulador.setVisibility(View.GONE);
        btnRegulador.setEnabled(false);
        btnControl.setVisibility(View.GONE);
        btnControl.setEnabled(false);
        btnProgramarPedido.setVisibility(View.GONE);
        btnProgramarPedido.setEnabled(false);
        btnPedidosPendientes.setVisibility(View.GONE);
        btnPedidosPendientes.setEnabled(false);
        btnHorarios.setVisibility(View.GONE);
        btnHorarios.setEnabled(false);
        btnPedidosCancelados.setVisibility(View.GONE);
        btnPedidosCancelados.setEnabled(false);
        btnPedidosAEntregar.setVisibility(View.GONE);
        btnPedidosAEntregar.setEnabled(false);
        btnRecepcionCombustible.setVisibility(View.GONE);
        btnRecepcionCombustible.setEnabled(false);
        btnMapa.setVisibility(View.GONE);
        btnMapa.setEnabled(false);
        btnHistoricoDistribuidor.setVisibility(View.GONE);
        btnHistoricoDistribuidor.setEnabled(false);
        btnHistorialOperador.setVisibility(View.GONE);
        btnHistorialOperador.setEnabled(false);


        if (rol == null) return;

        switch (rol) {

            case "admin":
                btnConsulta.setVisibility(View.VISIBLE);
                btnConsulta.setEnabled(true);
                btnInventario.setVisibility(View.VISIBLE);
                btnInventario.setEnabled(true);
                btnNotificador.setVisibility(View.VISIBLE);
                btnNotificador.setEnabled(true);
                btnControl.setVisibility(View.VISIBLE);
                btnControl.setEnabled(true);
                break;

            case "operador": // estación de servicio
                btnInventario.setVisibility(View.VISIBLE);
                btnInventario.setEnabled(true);
                btnSalidas.setVisibility(View.VISIBLE);
                btnSalidas.setEnabled(true);
                btnNotificador.setVisibility(View.VISIBLE);
                btnNotificador.setEnabled(true);
                btnRegulador.setVisibility(View.VISIBLE);
                btnRegulador.setEnabled(true);
                btnControl.setVisibility(View.VISIBLE);
                btnControl.setEnabled(true);
                btnProgramarPedido.setVisibility(View.VISIBLE);
                btnProgramarPedido.setEnabled(true);
                btnPedidosCancelados.setVisibility(View.VISIBLE);
                btnPedidosCancelados.setEnabled(true);
                btnRecepcionCombustible.setVisibility(View.VISIBLE);
                btnRecepcionCombustible.setEnabled(true);
                btnHistorialOperador.setVisibility(View.VISIBLE);
                btnHistorialOperador.setEnabled(true);
                break;

            case "cliente":
                btnConsulta.setVisibility(View.VISIBLE);
                btnConsulta.setEnabled(true);
                btnHorarios.setVisibility(View.VISIBLE);
                btnHorarios.setEnabled(true);
                btnMapa.setVisibility(View.VISIBLE);
                btnMapa.setEnabled(true);
                break;

            case "distribuidor":
                btnControl.setVisibility(View.VISIBLE);
                btnControl.setEnabled(true);
                btnSalidas.setVisibility(View.VISIBLE);
                btnSalidas.setEnabled(true);
                btnPedidosPendientes.setVisibility(View.VISIBLE);
                btnPedidosPendientes.setEnabled(true);
                btnPedidosAEntregar.setVisibility(View.VISIBLE);
                btnPedidosAEntregar.setEnabled(true);
                btnHistoricoDistribuidor.setVisibility(View.VISIBLE);
                btnHistoricoDistribuidor.setEnabled(true);
                btnMapa.setVisibility(View.VISIBLE);
                btnMapa.setEnabled(true);
                break;
        }
    }
}