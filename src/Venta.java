import java.util.Date;
import java.util.ArrayList;

public class Venta {
    private String idDocumento;
    private TipoDocumento tipo;
    private Date fecha;
    private Cliente cliente;
    private ArrayList<Pasaje> pasajes;
    private Pago pago;

    // Inicialmente la venta no tiene pago asociado
    public Venta(String idDoc, TipoDocumento tipo, Date fec, Cliente cli) {
        this.idDocumento = idDoc;
        this.tipo = tipo;
        this.fecha = fec;
        this.cliente = cli;
        this.pasajes = new ArrayList<>();

        if (cli != null) {
            cli.addVenta(this);
        }
    }

    public String getIdDocumento() { return idDocumento; }
    public TipoDocumento getTipo() { return tipo; }
    public Date getFecha() { return fecha; }
    public Cliente getCliente() { return cliente; }
    public Pasaje[] getPasajes() { return pasajes.toArray(new Pasaje[0]); }

    public int getMonto() {
        int total = 0;
        for (Pasaje p : pasajes) {
            total += p.getViaje().getPrecio();
        }
        return total;
    }

    public void createPasaje(int asiento, Viaje viaje, Pasajero pasajero) {
        for (Pasaje p : pasajes) {
            if (p.getAsiento() == asiento && p.getViaje().equals(viaje)) {
                return;
            }
        }
        Pasaje nuevoPasaje = new Pasaje(asiento, viaje, pasajero, this);
        this.pasajes.add(nuevoPasaje);
    }

    public int getMontoPagado() {
        return (pago != null) ? pago.getMonto() : 0;
    }

    public boolean pagaMonto() {
        if (this.pago != null) {
            return false;
        }
        this.pago = new PagoEfectivo(this.getMonto());
        return true;
    }

    public boolean pagaMonto(long nroTarjeta) {
        if (this.pago != null) {
            return false;
        }
        this.pago = new PagoTarjeta(this.getMonto(), nroTarjeta);
        return true;
    }

    public String getTipoPago() {
        if (pago instanceof PagoEfectivo) {
            return "Pago Efectivo";
        }
        if (pago instanceof PagoTarjeta) {
            return "Pago Tarjeta";
        }
        return null;
    }

    @Override
    public boolean equals(Object otro) {
        if (this == otro) {
            return true;
        }
        if (otro == null || getClass() != otro.getClass()) {
            return false;
        }
        Venta v = (Venta) otro;
        return idDocumento.equals(v.idDocumento) && tipo == v.tipo;
    }
}