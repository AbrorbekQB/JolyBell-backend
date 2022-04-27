package uz.greenstar.jolybell.utils.validattion;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import uz.greenstar.jolybell.api.validation.ConstraintError;
import uz.greenstar.jolybell.api.validation.ValidatorError;
import uz.greenstar.jolybell.dto.user.UserDTO;
import uz.greenstar.jolybell.entity.DistrictEntity;
import uz.greenstar.jolybell.entity.ProvinceEntity;
import uz.greenstar.jolybell.repository.DistrictRepository;
import uz.greenstar.jolybell.repository.ProvinceRepository;
import uz.greenstar.jolybell.utils.HelperUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserValidation {
    private final ProvinceRepository provinceRepository;
    private final DistrictRepository districtRepository;

    public ValidatorError validateUserUpdate(UserDTO userDTO) {
        List<ConstraintError> constrainErrors = new ArrayList<>();

        if (!StringUtils.hasText(userDTO.getFirstname()))
            constrainErrors.add(new ConstraintError("firstname", "Firstname is empty!", userDTO.getFirstname()));

        if (!StringUtils.hasText(userDTO.getLastname()))
            constrainErrors.add(new ConstraintError("surname", "Surname is empty!", userDTO.getLastname()));

        if (!StringUtils.hasText(userDTO.getPatronymic()))
            constrainErrors.add(new ConstraintError("patronymic", "Patronymic is empty!", userDTO.getPatronymic()));

        if (!StringUtils.hasText(userDTO.getPhoneNumber()))
            constrainErrors.add(new ConstraintError("phoneNumber", "PhoneNumber is empty!", userDTO.getPhoneNumber()));

        if (!HelperUtil.validatePhoneNumber(userDTO.getPhoneNumber()))
            constrainErrors.add(new ConstraintError("phoneNumber", "PhoneNumber is not validated", userDTO.getPhoneNumber()));

        if (constrainErrors.isEmpty()) {
            return new ValidatorError("Success!", constrainErrors);
        }

        return new ValidatorError("User updating error!", constrainErrors);
    }

    public ValidatorError validateUserUpdateAddress(UserDTO userDTO) {
        List<ConstraintError> constrainErrors = new ArrayList<>();

        if (!StringUtils.hasText(userDTO.getDistrict()))
            constrainErrors.add(new ConstraintError("district", "District is empty!", userDTO.getDistrict()));

        if (!StringUtils.hasText(userDTO.getProvince()))
            constrainErrors.add(new ConstraintError("province", "Province is empty!", userDTO.getProvince()));

        if (!StringUtils.hasText(userDTO.getAddress()))
            constrainErrors.add(new ConstraintError("address", "Address is empty!", userDTO.getAddress()));

        Optional<ProvinceEntity> provinceEntityOptional = provinceRepository.findById(userDTO.getProvince());
        Optional<DistrictEntity> districtEntityOptional = districtRepository.findById(userDTO.getDistrict());

        if (provinceEntityOptional.isEmpty())
            constrainErrors.add(new ConstraintError("province", "Province not found!", userDTO.getProvince()));

        if (districtEntityOptional.isEmpty())
            constrainErrors.add(new ConstraintError("district", "District not found!", userDTO.getDistrict()));

        if (constrainErrors.isEmpty()) {
            return new ValidatorError("Success!", constrainErrors);
        }

        return new ValidatorError("User updating error!", constrainErrors);
    }
}
