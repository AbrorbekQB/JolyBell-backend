package uz.greenstar.jolybell.api.validation;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ConstraintError {
    private String field;
    private String message;
    private String value;
}
