class Cliente extends Persona {
    private String email;
    private Venta[] ventas;

    public Cliente(idPersona id, Nombre nom, String email) {
        super(id, nom); //persona
        this.email = email;
        this.ventas = new Venta[0];
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void addVenta(Venta venta) {
        Venta[] nuevo = new Venta[ventas.length + 1];
        for (int i = 0; i < ventas.length; i++) {
            nuevo[i] = ventas[i];
        }
        nuevo[ventas.length] = venta;
        ventas = nuevo;
    }

    public Venta[] getVentas() {
        return ventas;
    }
}