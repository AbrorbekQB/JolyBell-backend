package uz.greenstar.jolybell.config;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import uz.greenstar.jolybell.exception.*;

@ControllerAdvice
public class ExceptionHandlerConfig {

    @ExceptionHandler({
            CategoryNotFoundException.class,
            ItemNotFoundException.class,
            OrderNotFoundException.class,
            ProductNotFoundException.class,
            PromoCodeCreatingException.class,
            UserCreationException.class})
    public ResponseEntity<?> handler(Exception ex) {
        return ResponseEntity.badRequest().body(ex);
    }
}
