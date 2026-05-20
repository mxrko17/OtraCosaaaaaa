import java.util.Date;
import java.util.Optional;
import java.util.ArrayList;
import excepciones.SistemaVentaPasajesException;

// Singleton que controla las empresas, terminales y buses [cite: 21, 176]
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
            throw new SistemaVentaPasajesException("Ya existe empresa con el rut indicado"); // [cite: 256]
        }
        Empresa e = new Empresa(rut, nombre);
        e.setUrl(url);
        empresas.add(e);
    }

    public void createBus(String pat, String marca, String modelo, int nroAsientos, Rut rutEmp) {
        Optional<Empresa> emp = findEmpresa(rutEmp);
        if (!emp.isPresent()) throw new SistemaVentaPasajesException("No existe empresa con el rut indicado"); // [cite: 256]
        if (findBus(pat).isPresent()) throw new SistemaVentaPasajesException("Ya existe bus con la patente indicada"); // [cite: 256]

        Bus b = new Bus(pat, nroAsientos, emp.get());
        b.setMarca(marca);
        b.setModelo(modelo);
    }

    public void createTerminal(String nombre, Direccion direccion) {
        if (findTerminal(nombre).isPresent()) throw new SistemaVentaPasajesException("Ya existe terminal con el nombre indicado"); // [cite: 256]
        if (findTerminalPorComuna(direccion.getComuna()).isPresent()) throw new SistemaVentaPasajesException("Ya existe terminal en la comuna indicada"); // [cite: 256]

        terminales.add(new Terminal(nombre, direccion));
    }

    public void hireConductorForEmpresa(Rut rutEmp, idPersona id, Nombre nom, Direccion dir) {
        Optional<Empresa> emp = findEmpresa(rutEmp);
        if (!emp.isPresent()) throw new SistemaVentaPasajesException("No existe empresa con el rut indicado"); // [cite: 256]
        if (!emp.get().addConductor(id, nom, dir)) {
            throw new SistemaVentaPasajesException("Ya está contratado conductor/auxiliar con el id dado en la empresa señalada"); // [cite: 256]
        }
    }

    public void hireAuxiliarForEmpresa(Rut rutEmp, idPersona id, Nombre nom, Direccion dir) {
        Optional<Empresa> emp = findEmpresa(rutEmp);
        if (!emp.isPresent()) throw new SistemaVentaPasajesException("No existe empresa con el rut indicado"); // [cite: 256]
        if (!emp.get().addAuxiliar(id, nom, dir)) {
            throw new SistemaVentaPasajesException("Ya está contratado auxiliar/conductor con el id dado en la empresa señalada"); // [cite: 256]
        }
    }

    public String[][] listEmpresas() {
        String[][] list = new String[empresas.size()][6]; // Ahora son 6 columnas
        for (int i = 0; i < empresas.size(); i++) {
            Empresa e = empresas.get(i);

            // Formateamos el RUT para que ponga los puntos separadores de miles y el guion
            String rutFormateado = String.format("%,d", e.getRut().getNumero()).replace(',', '.') + "-" + e.getRut().getDv();

            list[i][0] = rutFormateado;
            list[i][1] = e.getNombre();
            list[i][2] = e.getUrl();
            // Separamos los contadores en columnas independientes
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

        ArrayList<String[]> result = new ArrayList<>();

        // Formateadores para quitar los segundos
        java.text.SimpleDateFormat fmtHoraSalida = new java.text.SimpleDateFormat("HH:mm");
        java.time.format.DateTimeFormatter fmtHoraLlegada = java.time.format.DateTimeFormatter.ofPattern("HH:mm");

        for (Viaje v : t.get().getSalidas()) {
            if (v.getFecha().equals(fecha)) {
                // Aplicamos el formato a la hora de salida
                String horaFormat = fmtHoraSalida.format(v.getHora());

                result.add(new String[]{
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
                // Aplicamos el formato a la hora de llegada
                String horaFormat = v.getFechaHoraTermino().toLocalTime().format(fmtHoraLlegada);

                result.add(new String[]{
                        "Llegada",
                        horaFormat,
                        v.getBus().getPatente(),
                        v.getBus().getEmpresa().getNombre(),
                        String.valueOf(v.getBus().getNroAsientos() - v.getNroAsientosDisponibles())
                });
            }
        }
        return result.toArray(new String[0][0]);
    }

    public String[][] listVentasEmpresa(Rut rut) {
        Optional<Empresa> emp = findEmpresa(rut);
        if (!emp.isPresent()) {
            throw new excepciones.SistemaVentaPasajesException("No existe empresa con el rut indicado");
        }

        Venta[] ventas = emp.get().getVentas();
        String[][] result = new String[ventas.length][4];

        // 1. Creamos el formateador para la fecha
        java.text.SimpleDateFormat fmtFecha = new java.text.SimpleDateFormat("dd/MM/yyyy");

        for (int i = 0; i < ventas.length; i++) {
            // 2. Aplicamos el formato a la fecha
            result[i][0] = fmtFecha.format(ventas[i].getFecha());

            // 3. Pasamos el tipo a minúsculas
            result[i][1] = ventas[i].getTipo().toString().toLowerCase();

            // 4. Formateamos el monto con el separador de miles
            result[i][2] = String.format("%,d", ventas[i].getMontoPagado()).replace(',', '.');

            result[i][3] = ventas[i].getTipoPago() != null ? ventas[i].getTipoPago() : "Pendiente";
        }
        return result;
    }

    protected Optional<Empresa> findEmpresa(Rut rut) {
        return empresas.stream().filter(e -> e.getRut().equals(rut)).findFirst(); // [cite: 724]
    }

    protected Optional<Terminal> findTerminal(String nombre) {
        return terminales.stream().filter(t -> t.getNombre().equalsIgnoreCase(nombre)).findFirst(); // [cite: 724]
    }

    protected Optional<Terminal> findTerminalPorComuna(String comuna) {
        return terminales.stream().filter(t -> t.getDireccion().getComuna().equalsIgnoreCase(comuna)).findFirst(); // [cite: 724]
    }

    protected Optional<Bus> findBus(String patente) {
        for (Empresa e : empresas) {
            for (Bus b : e.getBuses()) {
                if (b.getPatente().equalsIgnoreCase(patente)) return Optional.of(b); // [cite: 724]
            }
        }
        return Optional.empty();
    }

    protected Optional<Conductor> findConductor(idPersona id, Rut rutEmpresa) {
        Optional<Empresa> emp = findEmpresa(rutEmpresa);
        if (emp.isPresent()) {
            for (Tripulante t : emp.get().getTripulantes()) {
                if (t instanceof Conductor && t.getIdPersona().equals(id)) return Optional.of((Conductor) t); // [cite: 724]
            }
        }
        return Optional.empty();
    }

    protected Optional<Auxiliar> findAuxiliar(idPersona id, Rut rutEmpresa) {
        Optional<Empresa> emp = findEmpresa(rutEmpresa);
        if (emp.isPresent()) {
            for (Tripulante t : emp.get().getTripulantes()) {
                if (t instanceof Auxiliar && t.getIdPersona().equals(id)) return Optional.of((Auxiliar) t); // [cite: 724]
            }
        }
        return Optional.empty();
    }
}