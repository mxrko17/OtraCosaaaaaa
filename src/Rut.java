public class Rut implements idPersona {
    private int numero;
    private char dv;

    private Rut(int numero, char dv) {
        this.numero = numero;
        this.dv = dv;
    }

    public int getNumero() {
        return numero;
    }

    public char getDv() {
        return dv;
    }

    public static Rut of(String rutConDv) {
        String limpio = rutConDv.trim().toUpperCase();

        String[] partes = limpio.split("-");

        int num = Integer.parseInt(partes[0].trim());
        char dv = partes[1].trim().charAt(0);

        return new Rut(num, dv);
    }

    @Override
    public String toString() {
        return "Rut: " + numero + "-" +dv;
    }

    @Override
    public boolean equals(Object otro) {
        if (this==otro) {
            return true;
        }
        if(otro == null || getClass() != otro.getClass()) {
            return false;
        }
        Rut rut = (Rut) otro;
        return numero == rut.numero && dv == rut.dv;
    }
}