public class Tripulante extends Persona{
    private Direccion direccion;
    private int nroViajes;

    public Tripulante (idPersona id, Nombre nom, Direccion dir){
        super(id,nom);
        this.direccion=direccion;
        this.nroViajes=0;
    }

    public Direccion getDirrecion(){
        return direccion;
    }

    public void setDireccion(Direccion direccion) {
        this.direccion = direccion;
    }

    public void addViaje(Viaje viaje) {
        nroViajes++;
    }

    public int getNroViajes() {
        return nroViajes;
    }
}