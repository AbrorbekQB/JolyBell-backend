package uz.greenstar.jolybell.rest.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.greenstar.jolybell.api.validation.ValidatorError;
import uz.greenstar.jolybell.dto.PromocodeDTO;
import uz.greenstar.jolybell.exception.ValidationException;
import uz.greenstar.jolybell.service.PromocodeService;
import uz.greenstar.jolybell.utils.validattion.PromoCodeValidation;

@RestController
@RequestMapping("/admin/promocode")
@RequiredArgsConstructor
@CrossOrigin("http://localhost:4200")
public class AdminPromocodeRestController {
    private final PromocodeService promocodeService;
    private final PromoCodeValidation validation;

    @PostMapping("/create")
    public void create(@RequestBody PromocodeDTO dto) {
        ValidatorError validatorError = validation.validateCreatePromocode(dto);
        if (!validatorError.getErrors().isEmpty())
            throw new ValidationException(validatorError);
        promocodeService.create(dto);
    }

    @ExceptionHandler({ValidationException.class})
    public ResponseEntity<?> handleException(ValidationException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getValidatorError());
    }
}