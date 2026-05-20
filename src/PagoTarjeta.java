public class PagoTarjeta extends Pago {
    private long nroTarjeta;

    public PagoTarjeta(int monto, long nroTarjeta) {
        super(monto);
        this.nroTarjeta = nroTarjeta;
    }

    public long getNroTarjeta() {
        return nroTarjeta;
    }
}