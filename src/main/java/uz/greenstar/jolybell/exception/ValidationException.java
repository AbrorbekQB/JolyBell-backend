package uz.greenstar.jolybell.exception;

import lombok.Getter;
import uz.greenstar.jolybell.api.validation.ValidatorError;

@Getter
public class ValidationException extends RuntimeException {
    private ValidatorError validatorError;

    public ValidationException(ValidatorError validatorError) {
        this.validatorError = validatorError;
    }
}
