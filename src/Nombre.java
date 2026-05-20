public class Nombre {

    private Tratamiento tratamiento;
    private String nombre,ApellidoPaterno,ApellidoMaterno;

    public Tratamiento getTratamiento() {
        return tratamiento;
    }

    public void setTratamiento(Tratamiento tratamiento) {

        this.tratamiento = tratamiento;
    }

    public String getNombres() {
        return nombre;
    }

    public void setNombres(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidoPaterno() {
        return ApellidoPaterno;
    }

    public void setApellidoPaterno(String apellidoPaterno) {
        ApellidoPaterno = apellidoPaterno;
    }

    public String getApellidoMaterno() {
        return ApellidoMaterno;
    }

    public void setApellidoMaterno(String apellidoMaterno) {
        ApellidoMaterno = apellidoMaterno;
    }

    @Override
    public String toString() {
        return tratamiento + " " + nombre + " " + ApellidoPaterno + " " + ApellidoMaterno;
    }

    @Override
    public boolean equals(Object otro) {
        if (this == otro) return true;
        if (otro == null || getClass() != otro.getClass()) return false;

        Nombre n = (Nombre) otro;

        return tratamiento == n.tratamiento && nombre.equals(n.nombre) && ApellidoPaterno.equals(n.ApellidoPaterno) && ApellidoMaterno.equals(n.ApellidoMaterno);
    }
}


