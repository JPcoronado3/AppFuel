package co.edu.unipiloto.scrumbacklog.activity.cliente;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

import co.edu.unipiloto.scrumbacklog.R;
import co.edu.unipiloto.scrumbacklog.database.DAOFactory;
import co.edu.unipiloto.scrumbacklog.database.dao.CombustibleDAO;
import co.edu.unipiloto.scrumbacklog.database.dao.InventarioDAO;
import co.edu.unipiloto.scrumbacklog.database.dao.MovimientoDAO;
import co.edu.unipiloto.scrumbacklog.database.dao.PrecioDAO;
import co.edu.unipiloto.scrumbacklog.database.dao.UbicacionDAO;
import co.edu.unipiloto.scrumbacklog.database.dao.UsuarioDAO;

public class ConsultaActivity extends AppCompatActivity {

    Spinner spTipoCombustible, spCiudad, spZona;
    Button btnCalcular, btnCalcularGalones, btnVolver;
    TextView txtResultado, txtResultadoGalones;
    EditText etGalones;

    DAOFactory factory;
    CombustibleDAO combustibleDAO;
    InventarioDAO inventarioDAO;
    MovimientoDAO movimientoDAO;
    PrecioDAO precioDAO;
    UbicacionDAO ubicacionDAO;
    UsuarioDAO usuarioDAO;

    String rol;
    int idUbicacion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consulta);

        SharedPreferences prefs = getSharedPreferences("sesion", MODE_PRIVATE);
        rol = prefs.getString("rol", "");
        idUbicacion = prefs.getInt("id_ubicacion", -1);

        factory = new DAOFactory(this);
        inventarioDAO = factory.getInventarioDAO();
        combustibleDAO = factory.getCombustibleDAO();
        movimientoDAO = factory.getMovimientoDAO();
        precioDAO = factory.getPrecioDAO();
        ubicacionDAO = factory.getUbicacionDAO();
        usuarioDAO = factory.getUsuarioDAO();

        spTipoCombustible = findViewById(R.id.spTipoCombustible);
        spCiudad = findViewById(R.id.spCiudad);
        spZona = findViewById(R.id.spZona);
        btnCalcular = findViewById(R.id.btnCalcular);
        btnVolver = findViewById(R.id.btnVolver);
        btnCalcularGalones = findViewById(R.id.calcularGalones);
        etGalones = findViewById(R.id.etGalones);
        txtResultadoGalones = findViewById(R.id.txtResultadoGalones);
        txtResultado = findViewById(R.id.txtResultado);

        ArrayAdapter<String> adapterComb = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                combustibleDAO.obtenerCombustibles()
        );
        spTipoCombustible.setAdapter(adapterComb);

        if (rol.equalsIgnoreCase("OPERADOR")) {

            UsuarioDAO usuarioDAO = factory.getUsuarioDAO();
            String[] ubicacion = usuarioDAO.obtenerUbicacionUsuario(idUbicacion);

            if (ubicacion != null) {

                String ciudadOperador = ubicacion[0];
                String zonaOperador = ubicacion[1];

                ArrayList<String> listaCiudad = new ArrayList<>();
                listaCiudad.add(ciudadOperador);

                ArrayAdapter<String> adapterCiudad = new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_item,
                        listaCiudad
                );
                spCiudad.setAdapter(adapterCiudad);

                ArrayList<String> listaZona = new ArrayList<>();
                listaZona.add(zonaOperador);

                ArrayAdapter<String> adapterZona = new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_item,
                        listaZona
                );
                spZona.setAdapter(adapterZona);

                spCiudad.setEnabled(false);
                spZona.setEnabled(false);
            }

        } else {
            ArrayAdapter<String> adapterCiudad = new ArrayAdapter<>(
                    this,
                    android.R.layout.simple_spinner_item,
                    ubicacionDAO.obtenerCiudades()
            );
            spCiudad.setAdapter(adapterCiudad);

            spCiudad.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String ciudad = spCiudad.getSelectedItem().toString();

                    ArrayAdapter<String> adapterZona = new ArrayAdapter<>(
                            ConsultaActivity.this,
                            android.R.layout.simple_spinner_item,
                            ubicacionDAO.obtenerZonas(ciudad)
                    );

                    spZona.setAdapter(adapterZona);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {}
            });
        }

        btnCalcular.setOnClickListener(view -> {

            String tipo = spTipoCombustible.getSelectedItem().toString();
            double precio;

            if (rol.equalsIgnoreCase("ADMIN")) {

                String ciudad = spCiudad.getSelectedItem().toString();
                String zona = spZona.getSelectedItem().toString();

                precio = precioDAO.obtenerPrecioZona(tipo, ciudad, zona);

            } else {
                precio = precioDAO.obtenerPrecioPorUbicacion(tipo, idUbicacion);
            }

            txtResultado.setText("Precio: $" + precio);
        });

        btnCalcularGalones.setOnClickListener(view -> {

            String galonesTexto = etGalones.getText().toString().trim();

            if (galonesTexto.isEmpty()) {
                Toast.makeText(this, "Ingrese galones", Toast.LENGTH_SHORT).show();
                return;
            }

            double galones = Double.parseDouble(galonesTexto);
            String tipo = spTipoCombustible.getSelectedItem().toString();

            double precio;

            if (rol.equalsIgnoreCase("ADMIN")) {

                String ciudad = spCiudad.getSelectedItem().toString();
                String zona = spZona.getSelectedItem().toString();

                precio = precioDAO.obtenerPrecioZona(tipo, ciudad, zona);

            } else {
                precio = precioDAO.obtenerPrecioPorUbicacion(tipo, idUbicacion);
            }

            double total = galones * precio;

            txtResultadoGalones.setText("Total: $" + total);
        });

        btnVolver.setOnClickListener(view -> finish());
    }
}