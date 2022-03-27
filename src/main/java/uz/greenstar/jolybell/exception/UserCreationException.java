package uz.greenstar.jolybell.exception;

public class UserCreationException extends RuntimeException {
    private String message;

    public UserCreationException(String message) {
        this.message = message;
    }
}
