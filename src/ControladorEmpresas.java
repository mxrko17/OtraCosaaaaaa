import java.util.Date;
import java.util.Optional;
import java.util.ArrayList;
import excepciones.SistemaVentaPasajesException;

public class ControladorEmpresas {
    private static ControladorEmpresas instance;
    private ArrayList<Empresa> empresas;
    private ArrayList<Terminal> terminales;

    private ControladorEmpresas() {
        empresas = new ArrayList<>();
        terminales = new ArrayList<>();
    }

    public static ControladorEmpresas getInstance() {
        if (instance == null) {
            instance = new ControladorEmpresas();
        }
        return instance;
    }

    public void createEmpresa(Rut rut, String nombre, String url) {
        if (findEmpresa(rut).isPresent()) {
            throw new SistemaVentaPasajesException("Ya existe empresa con el rut indicado");
        }
        Empresa e = new Empresa(rut, nombre);
        e.setUrl(url);
        empresas.add(e);
    }

    public void createBus(String pat, String marca, String modelo, int nroAsientos, Rut rutEmp) {
        Optional<Empresa> emp = findEmpresa(rutEmp);
        if (!emp.isPresent()) {
            throw new SistemaVentaPasajesException("No existe empresa con el rut indicado");
        }
        if (findBus(pat).isPresent()) {
            throw new SistemaVentaPasajesException("Ya existe bus con la patente indicada");
        }

        Bus b = new Bus(pat, nroAsientos, emp.get());
        b.setMarca(marca);
        b.setModelo(modelo);
    }

    public void createTerminal(String nombre, Direccion direccion) {
        if (findTerminal(nombre).isPresent()) {
            throw new SistemaVentaPasajesException("Ya existe terminal con el nombre indicado");
        }
        if (findTerminalPorComuna(direccion.getComuna()).isPresent()) {
            throw new SistemaVentaPasajesException("Ya existe terminal en la comuna indicada");
        }

        terminales.add(new Terminal(nombre, direccion));
    }

    public void hireConductorForEmpresa(Rut rutEmp, idPersona id, Nombre nom, Direccion dir) {
        Optional<Empresa> emp = findEmpresa(rutEmp);
        if (!emp.isPresent()) {
            throw new SistemaVentaPasajesException("No existe empresa con el rut indicado");
        }
        if (!emp.get().addConductor(id, nom, dir)) {
            throw new SistemaVentaPasajesException("Ya está contratado conductor/auxiliar con el id dado en la empresa señalada");
        }
    }

    public void hireAuxiliarForEmpresa(Rut rutEmp, idPersona id, Nombre nom, Direccion dir) {
        Optional<Empresa> emp = findEmpresa(rutEmp);
        if (!emp.isPresent()) {
            throw new SistemaVentaPasajesException("No existe empresa con el rut indicado");
        }
        if (!emp.get().addAuxiliar(id, nom, dir)) {
            throw new SistemaVentaPasajesException("Ya está contratado auxiliar/conductor con el id dado en la empresa señalada");
        }
    }

    public String[][] listEmpresas() {
        String[][] list = new String[empresas.size()][6];
        for (int i = 0; i < empresas.size(); i++) {
            Empresa e = empresas.get(i);

            String rutFormateado = String.format("%,d", e.getRut().getNumero()).replace(',', '.') + "-" + e.getRut().getDv();

            list[i][0] = rutFormateado;
            list[i][1] = e.getNombre();
            list[i][2] = e.getUrl();

            list[i][3] = String.valueOf(e.getTripulantes().length);
            list[i][4] = String.valueOf(e.getBuses().length);
            list[i][5] = String.valueOf(e.getVentas().length);
        }
        return list;
    }

    public String[][] listLlegadasSalidasTerminal(String nombre, Date fecha) {
        Optional<Terminal> t = findTerminal(nombre);
        if (!t.isPresent()) {
            throw new excepciones.SistemaVentaPasajesException("No existe terminal con el nombre indicado");
        }

        ArrayList<String[]> resultado = new ArrayList<>();

        java.text.SimpleDateFormat fmtHoraSalida = new java.text.SimpleDateFormat("HH:mm");
        java.time.format.DateTimeFormatter fmtHoraLlegada = java.time.format.DateTimeFormatter.ofPattern("HH:mm");

        for (Viaje v : t.get().getSalidas()) {
            if (v.getFecha().equals(fecha)) {

                String horaFormat = fmtHoraSalida.format(v.getHora());

                resultado.add(new String[]{
                        "Salida",
                        horaFormat,
                        v.getBus().getPatente(),
                        v.getBus().getEmpresa().getNombre(),
                        String.valueOf(v.getBus().getNroAsientos() - v.getNroAsientosDisponibles())
                });
            }
        }

        for (Viaje v : t.get().getLlegadas()) {
            if (v.getFecha().equals(fecha)) {

                String horaFormat = v.getFechaHoraTermino().toLocalTime().format(fmtHoraLlegada);

                resultado.add(new String[]{
                        "Llegada",
                        horaFormat,
                        v.getBus().getPatente(),
                        v.getBus().getEmpresa().getNombre(),
                        String.valueOf(v.getBus().getNroAsientos() - v.getNroAsientosDisponibles())
                });
            }
        }
        return resultado.toArray(new String[0][0]);
    }

    public String[][] listVentasEmpresa(Rut rut) {
        Optional<Empresa> emp = findEmpresa(rut);
        if (!emp.isPresent()) {
            throw new excepciones.SistemaVentaPasajesException("No existe empresa con el rut indicado");
        }

        Venta[] ventas = emp.get().getVentas();
        String[][] result = new String[ventas.length][4];


        java.text.SimpleDateFormat fmtFecha = new java.text.SimpleDateFormat("dd/MM/yyyy");

        for (int i = 0; i < ventas.length; i++) {

            result[i][0] = fmtFecha.format(ventas[i].getFecha());


            result[i][1] = ventas[i].getTipo().toString().toLowerCase();


            result[i][2] = String.format("%,d", ventas[i].getMontoPagado()).replace(',', '.');

            result[i][3] = ventas[i].getTipoPago() != null ? ventas[i].getTipoPago() : "Pendiente";
        }
        return result;
    }

    protected Optional<Empresa> findEmpresa(Rut rut) {
        for (int i = 0; i < empresas.size(); i++) {
            if (empresas.get(i).getRut().equals(rut)) {
                return Optional.of(empresas.get(i));
            }
        }
        return Optional.empty();
    }

    protected Optional<Terminal> findTerminal(String nombre) {
        for (int i = 0; i < terminales.size(); i++) {
            if (terminales.get(i).getNombre().equalsIgnoreCase(nombre)) {
                return Optional.of(terminales.get(i));
            }
        }
        return Optional.empty();
    }

    protected Optional<Terminal> findTerminalPorComuna(String comuna) {
        for (int i = 0; i < terminales.size(); i++) {
            if (terminales.get(i).getDireccion().getComuna().equalsIgnoreCase(comuna)) {
                return Optional.of(terminales.get(i));
            }
        }
        return Optional.empty();
    }

    protected Optional<Bus> findBus(String patente) {
        for (int i = 0; i < empresas.size(); i++) {
            Empresa e = empresas.get(i);
            Bus[] busesEmpresa = e.getBuses();
            for (int j = 0; j < busesEmpresa.length; j++) {
                if (busesEmpresa[j].getPatente().equalsIgnoreCase(patente)) {
                    return Optional.of(busesEmpresa[j]);
                }
            }
        }
        return Optional.empty();
    }

    protected Optional<Conductor> findConductor(idPersona id, Rut rutEmpresa) {
        Optional<Empresa> emp = findEmpresa(rutEmpresa);
        if (emp.isPresent()) {
            Tripulante[] tripulantes = emp.get().getTripulantes();
            for (int i = 0; i < tripulantes.length; i++) {
                if (tripulantes[i] instanceof Conductor && tripulantes[i].getIdPersona().equals(id)) {
                    return Optional.of((Conductor) tripulantes[i]);
                }
            }
        }
        return Optional.empty();
    }

    protected Optional<Auxiliar> findAuxiliar(idPersona id, Rut rutEmpresa) {
        Optional<Empresa> emp = findEmpresa(rutEmpresa);
        if (emp.isPresent()) {
            Tripulante[] tripulantes = emp.get().getTripulantes();
            for (int i = 0; i < tripulantes.length; i++) {
                if (tripulantes[i] instanceof Auxiliar && tripulantes[i].getIdPersona().equals(id)) {
                    return Optional.of((Auxiliar) tripulantes[i]);
                }
            }
        }
        return Optional.empty();
    }
}