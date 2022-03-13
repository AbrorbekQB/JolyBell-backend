package uz.greenstar.jolybell.exceptions;

public class CategoryNotFoundException extends RuntimeException {
    private String message;

    public CategoryNotFoundException(String message) {
        this.message = message;
    }
}
