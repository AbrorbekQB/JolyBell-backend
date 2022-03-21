package uz.greenstar.jolybell.exceptions;

public class ItemNotFoundException extends RuntimeException {
    private String message;

    public ItemNotFoundException(String message) {
        this.message = message;
    }
}
