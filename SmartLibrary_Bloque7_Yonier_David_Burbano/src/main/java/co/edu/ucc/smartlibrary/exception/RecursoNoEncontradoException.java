package co.edu.ucc.smartlibrary.exception;

public class RecursoNoEncontradoException extends RuntimeException {
    public RecursoNoEncontradoException(String tipo, Long id) {
        super(tipo + " con identificador " + id + " no encontrado");
    }
}
