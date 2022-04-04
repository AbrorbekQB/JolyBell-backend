package uz.greenstar.jolybell.utils.validattion;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import uz.greenstar.jolybell.api.validation.ConstraintError;
import uz.greenstar.jolybell.api.validation.ValidatorError;
import uz.greenstar.jolybell.dto.PromocodeDTO;
import uz.greenstar.jolybell.entity.PromoCodeEntity;
import uz.greenstar.jolybell.repository.PromocodeRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class PromoCodeValidation {
    private final PromocodeRepository promocodeRepository;


    public ValidatorError validateCreatePromocode(PromocodeDTO dto) {
        List<ConstraintError> constraintErrors = new ArrayList<>();

        if (!StringUtils.hasText(dto.getCode()))
            constraintErrors.add(new ConstraintError("code", "", "Code is empty!"));

        Optional<PromoCodeEntity> promocodeOptional = promocodeRepository.findByCode(dto.getCode());
        if (promocodeOptional.isPresent())
            constraintErrors.add(new ConstraintError("code", "", "this code already exist!"));

        if (dto.getStartDate().isAfter(dto.getEndDate()))
            constraintErrors.add(new ConstraintError("date", "", "date"));

        if (constraintErrors.isEmpty()) {
            return new ValidatorError("Success!", constraintErrors);
        }
        return new ValidatorError("Promocode creation error!", constraintErrors);
    }
}
