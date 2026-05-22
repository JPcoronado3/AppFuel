package co.edu.unipiloto.scrumbacklog.activity.distribuidor;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import co.edu.unipiloto.scrumbacklog.R;
import co.edu.unipiloto.scrumbacklog.activity.logIn.LoginActivity;
import co.edu.unipiloto.scrumbacklog.database.DatabaseHelper;
import co.edu.unipiloto.scrumbacklog.database.dao.PedidoDAO;

public class HistoricoDistribuidorActivity extends AppCompatActivity {

    private ListView listView;
    private PedidoDAO pedidoDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historico_distribuidor);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle("Histórico");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        listView = findViewById(R.id.listViewHistorico);

        DatabaseHelper helper = new DatabaseHelper(this);
        SQLiteDatabase db = helper.getWritableDatabase();

        pedidoDAO = new PedidoDAO(db);

        cargarHistorico();
    }

    private void cargarHistorico(){

        Cursor cursor = pedidoDAO.obtenerHistoricoCompleto();

        HistoricoAdapter adapter =
                new HistoricoAdapter(this, cursor);

        listView.setAdapter(adapter);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_pedido_entregar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == R.id.action_info){

            Toast.makeText(this,
                    "Historial completo de movimientos.",
                    Toast.LENGTH_SHORT).show();

            return true;

        } else if(item.getItemId() == R.id.action_logout){

            cerrarSesion();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void cerrarSesion(){

        SharedPreferences prefs =
                getSharedPreferences("sesion", MODE_PRIVATE);

        prefs.edit().clear().apply();

        Intent intent =
                new Intent(this, LoginActivity.class);

        intent.setFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK);

        startActivity(intent);

        finish();
    }
}