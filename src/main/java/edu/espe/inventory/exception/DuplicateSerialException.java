package edu.espe.inventory.exception;

public class DuplicateSerialException extends RuntimeException {
    
    public DuplicateSerialException(String message) {
        super("Dispositivo con serial " + message + " ya existe");
    }
}
