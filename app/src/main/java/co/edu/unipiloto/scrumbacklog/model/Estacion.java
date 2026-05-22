package co.edu.unipiloto.scrumbacklog.model;

public class Estacion {

    private String nombre;
    private String ciudad;
    private String localidad;

    private double latitud;
    private double longitud;

    private String direccion;

    private String apertura;
    private String cierre;

    private boolean disponible;

    public Estacion(
            String nombre,
            String ciudad,
            String localidad,
            double latitud,
            double longitud,
            String direccion,
            String apertura,
            String cierre,
            boolean disponible
    ) {

        this.nombre = nombre;
        this.ciudad = ciudad;
        this.localidad = localidad;
        this.latitud = latitud;
        this.longitud = longitud;
        this.direccion = direccion;
        this.apertura = apertura;
        this.cierre = cierre;
        this.disponible = disponible;
    }

    public String getNombre() {
        return nombre;
    }

    public String getCiudad() {
        return ciudad;
    }

    public String getLocalidad() {
        return localidad;
    }

    public double getLatitud() {
        return latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public String getDireccion() {
        return direccion;
    }

    public String getApertura() {
        return apertura;
    }

    public String getCierre() {
        return cierre;
    }

    public boolean isDisponible() {
        return disponible;
    }
}