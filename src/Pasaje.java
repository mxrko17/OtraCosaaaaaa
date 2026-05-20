import java.util.UUID; // Importamos la clase mágica

class Pasaje {
    private long numero;
    private int asiento;
    private Viaje viaje;
    private Pasajero pasajero;
    private Venta venta;

    public Pasaje(int asiento, Viaje viaje, Pasajero pasajero, Venta venta) {
        this.asiento = asiento;
        this.viaje = viaje;
        this.pasajero = pasajero;
        this.venta = venta;
        this.numero = Math.abs(UUID.randomUUID().getMostSignificantBits());

        if (viaje != null) {
            viaje.addPasaje(this);
        }
    }

    public long getNumero() { return numero; }
    public int getAsiento() { return asiento; }
    public Viaje getViaje() { return viaje; }
    public Pasajero getPasajero() { return pasajero; }
    public Venta getVenta() { return venta; }
}
