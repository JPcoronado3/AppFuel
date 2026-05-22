package co.edu.unipiloto.scrumbacklog.activity.cliente;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import co.edu.unipiloto.scrumbacklog.R;
import co.edu.unipiloto.scrumbacklog.service.SubsidioService;

public class SubsidioActivity extends AppCompatActivity {

    private EditText etCodigo;
    private Button btnSolicitar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subsidio);

        etCodigo = findViewById(R.id.etCodigoSubsidio);
        btnSolicitar = findViewById(R.id.btnSolicitarSubsidio);

        btnSolicitar.setOnClickListener(v -> {

            String codigo =
                    etCodigo.getText().toString().trim();

            // VALIDAR 8 DIGITOS
            if (codigo.length() != 8) {

                Toast.makeText(
                        this,
                        "El código debe tener 8 dígitos",
                        Toast.LENGTH_LONG
                ).show();

                return;
            }

            Intent intent =
                    new Intent(
                            this,
                            SubsidioService.class
                    );

            intent.putExtra("codigo", codigo);

            startService(intent);

            Toast.makeText(
                    this,
                    "Procesando subsidio...",
                    Toast.LENGTH_SHORT
            ).show();
        });
    }
}