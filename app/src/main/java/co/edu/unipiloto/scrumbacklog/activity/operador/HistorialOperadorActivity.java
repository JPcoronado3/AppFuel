package co.edu.unipiloto.scrumbacklog.activity.operador;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import co.edu.unipiloto.scrumbacklog.R;
import co.edu.unipiloto.scrumbacklog.database.DAOFactory;
import co.edu.unipiloto.scrumbacklog.database.dao.MovimientoDAO;

public class HistorialOperadorActivity extends AppCompatActivity {

    ListView listView;
    MovimientoDAO movimientoDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial_operador);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        listView = findViewById(R.id.listViewMovimientos);

        SharedPreferences prefs =
                getSharedPreferences("sesion", MODE_PRIVATE);

        int idUbicacion =
                prefs.getInt("id_ubicacion", -1);

        DAOFactory factory = new DAOFactory(this);

        movimientoDAO = factory.getMovimientoDAO();

        Cursor cursor =
                movimientoDAO.obtenerMovimientosCursorPorUbicacion(idUbicacion);

        MovimientoAdapter adapter =
                new MovimientoAdapter(this, cursor);

        listView.setAdapter(adapter);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}