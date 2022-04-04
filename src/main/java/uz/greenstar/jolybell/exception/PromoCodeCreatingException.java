package uz.greenstar.jolybell.exception;

public class PromoCodeCreatingException extends RuntimeException {
    private final String message;

    public PromoCodeCreatingException(String message) {
        this.message = message;
    }
}
