package edu.espe.inventory.exception;

public class NegativeStockException extends RuntimeException {
    
    public NegativeStockException(String message) {
        super("El stock no puede ser negativo");
    }
}
