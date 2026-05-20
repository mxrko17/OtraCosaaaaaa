public class Conductor extends Tripulante {

    public Conductor(idPersona id, Nombre nom, Direccion dir) {
        super(id, nom, dir);
    }

    @Override
    public void addViaje(Viaje viaje) {
        super.addViaje(viaje);
    }

    @Override
    public int getNroViajes() {
        return super.getNroViajes();
    }
}