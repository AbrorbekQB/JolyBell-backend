package uz.greenstar.jolybell.api.validation;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ValidatorError {
    private String message;
    private List<ConstraintError> errors;
}
