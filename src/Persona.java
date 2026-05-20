class Persona {
    private idPersona idPersona;
    private Nombre nombreCompleto;
    private String telefono;

    public Persona(idPersona idPersona, Nombre nombreCompleto) {
        this.idPersona = idPersona;
        this.nombreCompleto = nombreCompleto;
    }

    public idPersona getIdPersona() {
        return idPersona;
    }

    public Nombre getNombreCompleto() {
        return nombreCompleto;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setNombreCompleto(Nombre nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String toString(){
        return nombreCompleto + " " + idPersona + " " + telefono;
    }
    public boolean equals(Object otro) {
        if (this == otro) {
            return true;
        }
        if (otro == null || getClass() != otro.getClass()) {
            return false;
        }
        Persona persona = (Persona) otro;

        return idPersona.equals(persona.idPersona);
    }
}