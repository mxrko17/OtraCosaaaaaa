import java.util.ArrayList;

// Se ajusta la creación de buses para asociarlos a la empresa a la que pertenecen
public class Bus {
    private String patente;
    private String marca;
    private String modelo;
    private int nroAsientos;
    private Empresa empresa;
    private ArrayList<Viaje> viajes;

    public Bus(String patente, int nroAsientos, Empresa emp) {
        // Restricción: patente debe ser alfanumérica y permitir guiones
        if (!patente.matches("^[a-zA-Z0-9-]+$")) {
            throw new IllegalArgumentException("La patente debe ser alfanumérica (puede incluir guiones).");
        }
        this.patente = patente;
        this.nroAsientos = nroAsientos;
        this.empresa = emp;
        this.viajes = new ArrayList<>();

        if (emp != null) {
            emp.addBus(this);
        }
    }

    public String getPatente() { return patente; }
    public String getMarca() { return marca; }
    public void setMarca(String marca) { this.marca = marca; }
    public String getModelo() { return modelo; }
    public void setModelo(String modelo) { this.modelo = modelo; }
    public int getNroAsientos() { return nroAsientos; }
    public Empresa getEmpresa() { return empresa; }

    public void addViaje(Viaje viaje) {
        if (!viajes.contains(viaje)) viajes.add(viaje);
    }

    public Viaje[] getViajes() {
        return viajes.toArray(new Viaje[0]);
    }
}