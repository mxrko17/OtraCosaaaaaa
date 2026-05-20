public class Direccion {
    private String calle;
    private int numero;
    private String comuna;

    public Direccion(String calle, int numero, String comuna) {
        this.calle = calle;
        this.numero = numero;
        this.comuna = comuna;
    }

    public String getCalle() {
        return calle;
    }

    public int getNumero() {
        return numero;
    }

    public String getComuna() {
        return comuna;
    }

    public String toString() {
        return "Comuna: " + comuna + " calle: " + calle + " numero: " + numero;
    }

    @Override
    public boolean equals(Object otro) {
        return super.equals(otro);
    }
}

