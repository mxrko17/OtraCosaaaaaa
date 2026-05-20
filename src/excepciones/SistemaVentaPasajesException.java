package excepciones;

// En este avance solo las clases de la capa controlador se comunicarán mediante excepciones con la vista [cite: 261]
public class SistemaVentaPasajesException extends RuntimeException { // [cite: 263]
    public SistemaVentaPasajesException(String msg) {
        super(msg); // [cite: 265]
    }
}