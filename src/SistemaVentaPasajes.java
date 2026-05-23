import java.sql.Time;
import java.util.Date;
import java.util.Optional;
import java.util.ArrayList;

public class SistemaVentaPasajes {
    private static SistemaVentaPasajes instance;

    private ArrayList<Cliente> clientes;
    private ArrayList<Pasajero> pasajeros;
    private ArrayList<Viaje> viajes;
    private ArrayList<Venta> ventas;

    private SistemaVentaPasajes() {
        clientes = new ArrayList<>();
        pasajeros = new ArrayList<>();
        viajes = new ArrayList<>();
        ventas = new ArrayList<>();
    }

    public static SistemaVentaPasajes getInstance() {
        if (instance == null) {
            instance = new SistemaVentaPasajes();
        }
        return instance;
    }

    public void createCliente(idPersona id, Nombre nom, String fono, String email) {
        // Verificamos si el cliente YA está presente
        if (findCliente(id).isPresent()) {
            throw new excepciones.SistemaVentaPasajesException("Ya existe cliente con el id indicado");
        }

        // Si no existe, procedemos a crearlo normalmente
        Cliente nuevo = new Cliente(id, nom, email);
        nuevo.setTelefono(fono);
        clientes.add(nuevo);
    }

    public void createPasajero(idPersona id, Nombre nom, String fono, Nombre nomContacto, String fonoContacto) {
        if (findPasajero(id).isPresent()) {
            throw new excepciones.SistemaVentaPasajesException("Ya existe pasajero con el id indicado");
        }
        pasajeros.add(new Pasajero(id, nom, fono, nomContacto, fonoContacto));
    }

    public void createViaje(Date fecha, Time hora, int precio, int duracion, String patBus, idPersona[] idTripulantes, String[] nomComunas) {
        ControladorEmpresas ctrl = ControladorEmpresas.getInstance();

        // 1. Validar si ya existe el viaje
        if (findViaje(fecha, hora, patBus).isPresent()) {
            throw new excepciones.SistemaVentaPasajesException("Ya existe viaje con fecha, hora y patente de bus indicados"); //
        }

        // 2. Validar bus
        Optional<Bus> busOpt = ctrl.findBus(patBus);
        if (!busOpt.isPresent()) {
            throw new excepciones.SistemaVentaPasajesException("No existe bus con la patente indicada"); //
        }
        Bus bus = busOpt.get();
        Rut rutEmpresa = bus.getEmpresa().getRut();

        // 3. Validar auxiliar
        Optional<Auxiliar> auxOpt = ctrl.findAuxiliar(idTripulantes[0], rutEmpresa);
        if (!auxOpt.isPresent()) {
            throw new excepciones.SistemaVentaPasajesException("No existe auxiliar con el id indicado en la empresa con el rut indicado"); //
        }

        // 4. Validar conductor
        Optional<Conductor> condOpt = ctrl.findConductor(idTripulantes[1], rutEmpresa);
        if (!condOpt.isPresent()) {
            throw new excepciones.SistemaVentaPasajesException("No existe conductor con el id indicado en la empresa con el rut indicado"); //
        }

        // 5. Validar terminal de salida
        Optional<Terminal> termSalidaOpt = ctrl.findTerminalPorComuna(nomComunas[0]);
        if (!termSalidaOpt.isPresent()) {
            throw new excepciones.SistemaVentaPasajesException("No existe terminal de salida en la comuna indicada"); //
        }

        // 6. Validar terminal de llegada
        Optional<Terminal> termLlegadaOpt = ctrl.findTerminalPorComuna(nomComunas[1]);
        if (!termLlegadaOpt.isPresent()) {
            throw new excepciones.SistemaVentaPasajesException("No existe terminal de llegada en la comuna indicada"); //
        }

        // Si esta correcto, crea el viaje
        Viaje nuevoViaje = new Viaje(fecha, hora, precio, duracion, bus, auxOpt.get(), condOpt.get(), termSalidaOpt.get(), termLlegadaOpt.get());

        // Agregar conductor 2 si el arreglo tiene un tercer elemento válido
        if (idTripulantes.length > 2 && idTripulantes[2] != null) {
            Optional<Conductor> cond2Opt = ctrl.findConductor(idTripulantes[2], rutEmpresa);
            cond2Opt.ifPresent(nuevoViaje::addConductor);
        }

        viajes.add(nuevoViaje);
    }

    public void iniciaVenta(String idDoc, TipoDocumento tipo, Date fechaViaje, String comSalida, String comLlegada, idPersona idCliente, int nroPasajes) {
        // 1. Validar si existe la venta
        if (findVenta(idDoc, tipo).isPresent()) {
            throw new excepciones.SistemaVentaPasajesException("Ya existe venta con el id y tipo de documento indicados");
        }

        // 2. Validar cliente
        Optional<Cliente> cliente = findCliente(idCliente);
        if (!cliente.isPresent()) {
            throw new excepciones.SistemaVentaPasajesException("No existe cliente con id indicado");
        }

        // 3. Validar la disponibilidad
        String[][] horarios = getHorariosDisponibles(fechaViaje, comSalida, comLlegada, nroPasajes);
        if (horarios.length == 0) {
            throw new excepciones.SistemaVentaPasajesException("No existen viajes disponibles en la fecha y con terminales en las comunas de salida y llegada indicados");
        }

        // 4. Creaamo la venta (Aqui usamos new Date() para cumplir con usar la fecha actual del sistema )
        ventas.add(new Venta(idDoc, tipo, new Date(), cliente.get()));
    }

    public String[][] getHorariosDisponibles(Date fechaViaje, String comunaSalida, String comunaLlegada, int nroPasajes) {
        java.util.ArrayList<String[]> disponibles = new java.util.ArrayList<>();
        java.text.SimpleDateFormat dfHora = new java.text.SimpleDateFormat("HH:mm");

        for (Viaje v : viajes) {
            boolean matchFecha = v.getFecha().equals(fechaViaje);
            boolean matchSalida = v.getTerminalSalida() != null &&
                    v.getTerminalSalida().getDireccion().getComuna().equalsIgnoreCase(comunaSalida);
            boolean matchLlegada = v.getTerminalLlegada() != null &&
                    v.getTerminalLlegada().getDireccion().getComuna().equalsIgnoreCase(comunaLlegada);

            if (matchFecha && matchSalida && matchLlegada && v.existeDisponibilidad(nroPasajes)) {
                String[] fila = new String[4];
                fila[0] = v.getBus().getPatente();
                fila[1] = dfHora.format(v.getHora());
                fila[2] = String.valueOf(v.getPrecio());
                fila[3] = String.valueOf(v.getNroAsientosDisponibles());
                disponibles.add(fila);
            }
        }
        return disponibles.toArray(new String[0][0]);
    }

    public String[] listAsientosDeViaje(Date fecha, Time hora, String patBus) {
        Optional<Viaje> v = findViaje(fecha, hora, patBus);
        return v.isPresent() ? v.get().getAsientos() : new String[0];
    }

    public Optional<String> getNombrePasajero(idPersona idPasajero) {
        Optional<Pasajero> p = findPasajero(idPasajero);
        return p.map(pasajero -> pasajero.getNombreCompleto().toString());
    }

    public Optional<Integer> getMontoVenta(String idDocumento, TipoDocumento tipo) {
        Optional<Venta> v = findVenta(idDocumento, tipo);
        return v.map(Venta::getMonto);
    }

    public void vendePasaje(String idDoc, TipoDocumento tipo, Date fechaViaje, Time hora, String patBus, int asiento, idPersona idPasajero) {
        Optional<Venta> venta = findVenta(idDoc, tipo);
        if (!venta.isPresent()) {
            throw new excepciones.SistemaVentaPasajesException("No existe venta con el id y tipo de documento indicados");
        }

        Optional<Pasajero> pasajero = findPasajero(idPasajero);
        if (!pasajero.isPresent()) {
            throw new excepciones.SistemaVentaPasajesException("No existe pasajero con el id indicado");
        }

        Optional<Viaje> viaje = findViaje(fechaViaje, hora, patBus);
        if (!viaje.isPresent()) {
            throw new excepciones.SistemaVentaPasajesException("No existe viaje con la fecha, hora y patente de bus indicados");
        }

        venta.get().createPasaje(asiento, viaje.get(), pasajero.get());
    }

    public void pagaVenta(String idDocumento, TipoDocumento tipo) {
        Optional<Venta> venta = findVenta(idDocumento, tipo);
        if (!venta.isPresent()) {
            throw new excepciones.SistemaVentaPasajesException("No existe venta con el id y tipo de documento indicados");
        }

        if (!venta.get().pagaMonto()) {
            throw new excepciones.SistemaVentaPasajesException("La venta ya fue pagada");
        }
    }

    public void pagaVenta(String idDocumento, TipoDocumento tipo, long nroTarjeta) {
        Optional<Venta> venta = findVenta(idDocumento, tipo);
        if (!venta.isPresent()) {
            throw new excepciones.SistemaVentaPasajesException("No existe venta con el id y tipo de documento indicados");
        }

        if (!venta.get().pagaMonto(nroTarjeta)) {
            throw new excepciones.SistemaVentaPasajesException("La venta ya fue pagada");
        }
    }

    public String[][] listVentas() {
        String[][] datos = new String[ventas.size()][4];
        java.text.SimpleDateFormat dfFecha = new java.text.SimpleDateFormat("dd/MM/yyyy");

        for (int i = 0; i < ventas.size(); i++) {
            datos[i][0] = ventas.get(i).getIdDocumento();

            datos[i][1] = ventas.get(i).getTipo().toString().toLowerCase();

            datos[i][2] = dfFecha.format(ventas.get(i).getFecha());

            datos[i][3] = String.format("%,d", ventas.get(i).getMontoPagado()).replace(',', '.');
        }
        return datos;
    }

    public String[][] listViajes() {
        String[][] datos = new String[viajes.size()][8];
        java.text.SimpleDateFormat dfFecha = new java.text.SimpleDateFormat("dd/MM/yyyy");
        java.text.SimpleDateFormat dfHora = new java.text.SimpleDateFormat("HH:mm");

        for (int i = 0; i < viajes.size(); i++) {
            Viaje v = viajes.get(i);

            datos[i][0] = (v.getFecha() != null) ? dfFecha.format(v.getFecha()) : "N/A";

            datos[i][1] = (v.getHora() != null) ? dfHora.format(v.getHora()) : "N/A";

            if (v.getFechaHoraTermino() != null) {
                java.time.LocalTime horaLlega = v.getFechaHoraTermino().toLocalTime();
                datos[i][2] = horaLlega.format(java.time.format.DateTimeFormatter.ofPattern("HH:mm"));
            } else {
                datos[i][2] = "N/A";
            }

            datos[i][3] = String.format("$%,d", v.getPrecio()).replace(',', '.');

            //Asientos Disponibles
            datos[i][4] = String.valueOf(v.getNroAsientosDisponibles());

            //Patente del Bus
            datos[i][5] = (v.getBus() != null) ? v.getBus().getPatente() : "N/A";

            //Comuna de Origen
            datos[i][6] = (v.getTerminalSalida() != null && v.getTerminalSalida().getDireccion() != null)
                    ? v.getTerminalSalida().getDireccion().getComuna().toUpperCase() : "N/A";

            //Comuna de Destino
            datos[i][7] = (v.getTerminalLlegada() != null && v.getTerminalLlegada().getDireccion() != null)
                    ? v.getTerminalLlegada().getDireccion().getComuna().toUpperCase() : "N/A";
        }
        return datos;
    }

    public String[][] listPasajerosViaje(Date fecha, Time hora, String patenteBus) {
        Optional<Viaje> v = findViaje(fecha, hora, patenteBus);
        if (!v.isPresent()) {
            throw new excepciones.SistemaVentaPasajesException("No existe viaje con la fecha, hora y patente de bus indicados");
        }
        return v.get().getListaPasajeros();
    }

    private Optional<Cliente> findCliente(idPersona id) {
        return clientes.stream().filter(c -> c.getIdPersona().equals(id)).findFirst();
    }

    private Optional<Venta> findVenta(String idDocumento, TipoDocumento tipoDocumento) {
        return ventas.stream().filter(v -> v.getIdDocumento().equals(idDocumento) && v.getTipo().equals(tipoDocumento)).findFirst();
    }

    private Optional<Viaje> findViaje(Date fecha, Time hora, String patenteBus) {
        return viajes.stream().filter(v -> v.getFecha().equals(fecha) && v.getHora().equals(hora) && v.getBus().getPatente().equals(patenteBus)).findFirst();
    }

    private Optional<Pasajero> findPasajero(idPersona idPersona) {
        return pasajeros.stream().filter(p -> p.getIdPersona().equals(idPersona)).findFirst();
    }
}