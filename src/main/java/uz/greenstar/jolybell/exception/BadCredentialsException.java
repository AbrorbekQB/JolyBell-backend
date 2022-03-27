package uz.greenstar.jolybell.exception;

public class BadCredentialsException extends RuntimeException {
    private String message;

    public BadCredentialsException(String message) {
        this.message = message;
    }
}
