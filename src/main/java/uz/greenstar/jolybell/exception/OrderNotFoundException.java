package uz.greenstar.jolybell.exception;

public class OrderNotFoundException extends RuntimeException {
    private String message;

    public OrderNotFoundException(String message) {
        this.message = message;
    }
}
