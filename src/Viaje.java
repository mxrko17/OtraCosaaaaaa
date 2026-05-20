import java.sql.Time;
import java.util.Date;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;

// Se ajusta la creación de viajes para agregar auxiliares y conductores, así como el origen y destino
public class Viaje {
    private Date fecha;
    private Time hora;
    private int precio;
    private int duracion;
    private Bus bus;
    private Auxiliar auxiliar;
    private Conductor conductor;
    private Terminal terminalSalida;
    private Terminal terminalLlegada;
    private ArrayList<Pasaje> pasajes;
    private ArrayList<Conductor> conductoresAdicionales; // Para soporte del segundo conductor

    // Constructor que asocia todos los parámetros obligatorios
    public Viaje(Date fecha, Time hora, int precio, int dur, Bus bus, Auxiliar aux, Conductor cond, Terminal sale, Terminal llega) {
        this.fecha = fecha;
        this.hora = hora;
        this.precio = precio;
        this.duracion = dur;
        this.bus = bus;
        this.auxiliar = aux;
        this.conductor = cond;
        this.terminalSalida = sale;
        this.terminalLlegada = llega;
        this.pasajes = new ArrayList<>();
        this.conductoresAdicionales = new ArrayList<>();

        if (bus != null) bus.addViaje(this);
        if (sale != null) sale.addSalida(this);
        if (llega != null) llega.addLlegada(this);
        if (cond != null) cond.addViaje(this);
        if (aux != null) aux.addViaje(this);
    }

    public Date getFecha() { return fecha; }
    public Time getHora() { return hora; }
    public int getPrecio() { return precio; }
    public void setPrecio(int precio) { this.precio = precio; }
    public void setDuracion(int duracion) { this.duracion = duracion; }

    // Retorna la fecha y hora de llegada del viaje sumando la duración
    public LocalDateTime getFechaHoraTermino() {
        LocalDateTime inicio = LocalDateTime.of(fecha.toInstant().atZone(ZoneId.systemDefault()).toLocalDate(), hora.toLocalTime());
        return inicio.plusMinutes(duracion);
    }

    public void addPasaje(Pasaje pasaje) {
        if (!pasajes.contains(pasaje)) pasajes.add(pasaje);
    }

    // Retorna verdadero si cuenta con la cantidad de asientos solicitada
    public boolean existeDisponibilidad(int nroAsientos) {
        return (bus.getNroAsientos() - pasajes.size()) >= nroAsientos;
    }

    // Retorna arreglo indicando número si está libre, o "*" si está ocupado
    public String[] getAsientos() {
        String[] resultado = new String[bus.getNroAsientos()];
        for (int i = 0; i < bus.getNroAsientos(); i++) resultado[i] = String.valueOf(i + 1);
        for (Pasaje p : pasajes) resultado[p.getAsiento() - 1] = "*";
        return resultado;
    }

    public Bus getBus() { return bus; }

    // Retorna arreglo bidimensional con id, nombre, nombre del contacto y teléfono
    public String[][] getListaPasajeros() {
        String[][] lista = new String[pasajes.size()][4];
        for (int i = 0; i < pasajes.size(); i++) {
            Pasajero p = pasajes.get(i).getPasajero();
            lista[i][0] = p.getIdPersona().toString();
            lista[i][1] = p.getNombreCompleto().toString();
            lista[i][2] = p.getNomContacto().toString();
            lista[i][3] = p.getFonoContacto();
        }
        return lista;
    }

    public int getNroAsientosDisponibles() {
        return bus.getNroAsientos() - pasajes.size();
    }

    // Retorna arreglo con las ventas sin duplicados
    public Venta[] getVentas() {
        ArrayList<Venta> ventasViaje = new ArrayList<>();
        for (Pasaje p : pasajes) {
            if (!ventasViaje.contains(p.getVenta())) {
                ventasViaje.add(p.getVenta());
            }
        }
        return ventasViaje.toArray(new Venta[0]);
    }

    public void addConductor(Conductor conductorAdicional) {
        if (conductorAdicional != null && !conductoresAdicionales.contains(conductorAdicional)) {
            this.conductoresAdicionales.add(conductorAdicional);
            conductorAdicional.addViaje(this);
        }
    }

    public Tripulante[] getTripulantes() {
        ArrayList<Tripulante> trips = new ArrayList<>();
        trips.add(auxiliar);
        trips.add(conductor);
        trips.addAll(conductoresAdicionales);
        return trips.toArray(new Tripulante[0]);
    }

    public Terminal getTerminalLlegada() { return terminalLlegada; }
    public Terminal getTerminalSalida() { return terminalSalida; }
}