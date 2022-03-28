package uz.greenstar.jolybell.exception;

public class ItemNotFoundException extends RuntimeException {
    private String message;

    public ItemNotFoundException(String message) {
        this.message = message;
    }
}
