package co.edu.unipiloto.scrumbacklog.activity.cliente;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import co.edu.unipiloto.scrumbacklog.R;

public class SubsidioAprobadoActivity
        extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_subsidio_aprobado
        );

        TextView tvCodigo =
                findViewById(R.id.tvCodigoSubsidio);

        String codigo =
                getIntent().getStringExtra("codigo");

        tvCodigo.setText(
                "Código aprobado: " + codigo
        );
    }
}