import java.util.ArrayList;

public class Empresa {
    private Rut rut;
    private String nombre;
    private String url;
    private ArrayList<Bus> buses;
    private ArrayList<Tripulante> tripulantes;

    public Empresa(Rut rut, String nombre) {
        this.rut = rut;
        this.nombre = nombre;
        this.buses = new ArrayList<>();
        this.tripulantes = new ArrayList<>();
    }

    public Rut getRut() { return rut; }
    public String getNombre() { return nombre; }
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public void addBus(Bus bus) {
        if (!buses.contains(bus)) buses.add(bus);
    }

    public Bus[] getBuses() {
        return buses.toArray(new Bus[0]);
    }

    public boolean addConductor(idPersona id, Nombre nom, Direccion dir) {
        for (Tripulante t : tripulantes) {
            if (t.getIdPersona().equals(id)) return false;
        }
        Conductor c = new Conductor(id, nom, dir);
        tripulantes.add(c);
        return true;
    }

    public boolean addAuxiliar(idPersona id, Nombre nom, Direccion dir) {
        for (Tripulante t : tripulantes) {
            if (t.getIdPersona().equals(id)) return false;
        }
        Auxiliar a = new Auxiliar(id, nom, dir);
        tripulantes.add(a);
        return true;
    }

    public Tripulante[] getTripulantes() {
        return tripulantes.toArray(new Tripulante[0]);
    }

    public Venta[] getVentas() {
        ArrayList<Venta> ventasEmpresa = new ArrayList<>();
        for (Bus b : buses) {
            for (Viaje v : b.getViajes()) {
                for (Venta venta : v.getVentas()) {
                    if (!ventasEmpresa.contains(venta)) {
                        ventasEmpresa.add(venta);
                    }
                }
            }
        }
        return ventasEmpresa.toArray(new Venta[0]);
    }
}