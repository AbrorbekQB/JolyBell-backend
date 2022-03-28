package uz.greenstar.jolybell.exception;

public class CategoryNotFoundException extends RuntimeException {
    private String message;

    public CategoryNotFoundException(String message) {
        this.message = message;
    }
}
