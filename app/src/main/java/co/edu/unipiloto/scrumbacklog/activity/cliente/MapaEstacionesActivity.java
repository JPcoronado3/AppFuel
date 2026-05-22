package co.edu.unipiloto.scrumbacklog.activity.cliente;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import com.google.maps.android.PolyUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import co.edu.unipiloto.scrumbacklog.R;
import co.edu.unipiloto.scrumbacklog.database.DAOFactory;
import co.edu.unipiloto.scrumbacklog.database.dao.UbicacionDAO;
import co.edu.unipiloto.scrumbacklog.model.Estacion;

public class MapaEstacionesActivity extends AppCompatActivity
        implements OnMapReadyCallback {

    private GoogleMap mMap;
    private UbicacionDAO ubicacionDAO;

    private static final int LOCATION_PERMISSION_REQUEST = 1;

    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;

    private final ArrayList<LatLng> recorrido = new ArrayList<>();

    private Polyline polylineRecorrido;
    private Polyline rutaActual;

    private LatLng ubicacionActual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapa_estaciones);

        DAOFactory factory = new DAOFactory(this);
        ubicacionDAO = factory.getUbicacionDAO();

        fusedLocationClient =
                LocationServices.getFusedLocationProviderClient(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {

            getSupportActionBar().setTitle("Mapa Estaciones");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        SupportMapFragment mapFragment =
                (SupportMapFragment)
                        getSupportFragmentManager()
                                .findFragmentById(R.id.map);

        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {

        finish();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_consulta, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_info) {

            Toast.makeText(
                    this,
                    "Mapa de estaciones disponibles",
                    Toast.LENGTH_SHORT
            ).show();

            return true;
        }

        if (item.getItemId() == R.id.action_logout) {

            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void activarUbicacion() {

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                    this,
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION
                    },
                    LOCATION_PERMISSION_REQUEST
            );

            return;
        }

        mMap.setMyLocationEnabled(true);

        mMap.getUiSettings().setMyLocationButtonEnabled(true);
    }

    private void iniciarActualizacionesUbicacion() {

        LocationRequest locationRequest =
                new LocationRequest.Builder(
                        Priority.PRIORITY_HIGH_ACCURACY,
                        5000
                ).build();

        locationCallback = new LocationCallback() {

            @Override
            public void onLocationResult(
                    @NonNull LocationResult locationResult
            ) {

                super.onLocationResult(locationResult);

                for (Location location :
                        locationResult.getLocations()) {

                    LatLng nuevaPosicion =
                            new LatLng(
                                    location.getLatitude(),
                                    location.getLongitude()
                            );

                    ubicacionActual = nuevaPosicion;

                    recorrido.add(nuevaPosicion);

                    if (polylineRecorrido != null) {
                        polylineRecorrido.remove();
                    }

                    polylineRecorrido =
                            mMap.addPolyline(
                                    new PolylineOptions()
                                            .addAll(recorrido)
                                            .width(10)
                                            .color(android.graphics.Color.RED)
                            );
                }
            }
        };

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED) {

            return;
        }

        fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                getMainLooper()
        );
    }

    private void dibujarRuta(
            LatLng origen,
            LatLng destino
    ) {

        String url =
                "https://maps.googleapis.com/maps/api/directions/json?"
                        + "origin=" + origen.latitude + "," + origen.longitude
                        + "&destination=" + destino.latitude + "," + destino.longitude
                        + "&mode=driving"
                        + "&key=AIzaSyD27rJ4kQ4wNYCdTWsc6FhxLdJ25VEAUCk";

        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,

                response -> {

                    try {

                        String status =
                                response.getString("status");

                        if (!status.equals("OK")) {

                            Toast.makeText(
                                    this,
                                    "Error ruta: " + status,
                                    Toast.LENGTH_LONG
                            ).show();

                            return;
                        }

                        JSONArray routes =
                                response.getJSONArray("routes");

                        JSONObject route =
                                routes.getJSONObject(0);

                        JSONObject polyline =
                                route.getJSONObject(
                                        "overview_polyline"
                                );

                        String points =
                                polyline.getString("points");

                        List<LatLng> lista =
                                PolyUtil.decode(points);

                        // ELIMINAR RUTA ANTERIOR
                        if (rutaActual != null) {
                            rutaActual.remove();
                        }

                        // DIBUJAR NUEVA RUTA
                        rutaActual =
                                mMap.addPolyline(

                                        new PolylineOptions()
                                                .addAll(lista)
                                                .width(14)
                                                .color(android.graphics.Color.BLUE)
                                                .geodesic(true)
                                );

                    } catch (Exception e) {

                        e.printStackTrace();

                        Toast.makeText(
                                this,
                                "Error dibujando ruta",
                                Toast.LENGTH_LONG
                        ).show();
                    }
                },

                error -> {

                    error.printStackTrace();

                    Toast.makeText(
                            this,
                            "Error conexión",
                            Toast.LENGTH_LONG
                    ).show();
                }
        );

        queue.add(request);
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults
    ) {

        super.onRequestPermissionsResult(
                requestCode,
                permissions,
                grantResults
        );

        if (requestCode == LOCATION_PERMISSION_REQUEST) {

            if (grantResults.length > 0
                    && grantResults[0]
                    == PackageManager.PERMISSION_GRANTED) {

                activarUbicacion();
                iniciarActualizacionesUbicacion();
            }
        }
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();

        if (locationCallback != null) {

            fusedLocationClient.removeLocationUpdates(
                    locationCallback
            );
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        activarUbicacion();
        iniciarActualizacionesUbicacion();

        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(true);

        ArrayList<Estacion> estaciones =
                ubicacionDAO.obtenerEstaciones();

        for (Estacion estacion : estaciones) {

            LatLng posicion =
                    new LatLng(
                            estacion.getLatitud(),
                            estacion.getLongitud()
                    );

            Marker marker = mMap.addMarker(

                    new MarkerOptions()
                            .position(posicion)
                            .title(estacion.getNombre())
                            .snippet(
                                    "Dirección: "
                                            + estacion.getDireccion()
                                            + "\nHorario: "
                                            + estacion.getApertura()
                                            + " - "
                                            + estacion.getCierre()
                                            + "\nDisponible: "
                                            + (
                                            estacion.isDisponible()
                                                    ? "SI"
                                                    : "NO"
                                    )
                            )
            );

            if (marker != null) {
                marker.setTag(estacion);
            }
        }

        LatLng bogota =
                new LatLng(4.7110, -74.0721);

        mMap.moveCamera(
                CameraUpdateFactory.newLatLngZoom(
                        bogota,
                        11
                )
        );

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {

            private Marker ultimoMarker = null;

            @Override
            public void onInfoWindowClick(Marker marker) {

                if (ubicacionActual == null) {

                    Toast.makeText(
                            MapaEstacionesActivity.this,
                            "Ubicación actual no disponible",
                            Toast.LENGTH_SHORT
                    ).show();

                    return;
                }

                LatLng origen = ubicacionActual;
                LatLng destino = marker.getPosition();

                // PRIMER CLICK -> DIBUJAR RUTA
                if (ultimoMarker == null || !ultimoMarker.equals(marker)) {

                    ultimoMarker = marker;

                    dibujarRuta(origen, destino);

                    Toast.makeText(
                            MapaEstacionesActivity.this,
                            "Ruta dibujada",
                            Toast.LENGTH_SHORT
                    ).show();

                } else {

                    // SEGUNDO CLICK -> GOOGLE MAPS
                    Uri uri = Uri.parse(
                            "google.navigation:q="
                                    + destino.latitude
                                    + ","
                                    + destino.longitude
                    );

                    Intent intent =
                            new Intent(Intent.ACTION_VIEW, uri);

                    intent.setPackage("com.google.android.apps.maps");

                    if (intent.resolveActivity(getPackageManager()) != null) {

                        startActivity(intent);

                    } else {

                        Toast.makeText(
                                MapaEstacionesActivity.this,
                                "Google Maps no instalado",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                }
            }
        });
    }
}