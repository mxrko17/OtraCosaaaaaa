class Pasajero extends Persona {
    private Nombre nomContacto;
    private String fonoContacto;

    public Pasajero(idPersona idPersona, Nombre nombreCompleto, String telefono, Nombre nomContacto, String fonoContacto) {
        super(idPersona, nombreCompleto); //persona
        this.setTelefono(telefono); //persona
        this.nomContacto = nomContacto;
        this.fonoContacto = fonoContacto;
    }

    public Nombre getNomContacto() {
        return nomContacto;
    }

    public void setNomContacto(Nombre nom) {
        this.nomContacto = nom;
    }

    public String getFonoContacto() {
        return fonoContacto;
    }

    public void setFonoContacto(String fono) {
        this.fonoContacto = fono;
    }
}