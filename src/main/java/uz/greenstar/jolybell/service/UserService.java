package uz.greenstar.jolybell.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import uz.greenstar.jolybell.api.filterForm.FilterRequest;
import uz.greenstar.jolybell.api.filterForm.FilterResponse;
import uz.greenstar.jolybell.dto.user.ChangePasswordDTO;
import uz.greenstar.jolybell.dto.user.ForgotPasswordDTO;
import uz.greenstar.jolybell.dto.user.UserDTO;
import uz.greenstar.jolybell.entity.ActivationCodeEntity;
import uz.greenstar.jolybell.entity.RoleEntity;
import uz.greenstar.jolybell.entity.UserEntity;
import uz.greenstar.jolybell.exception.ItemNotFoundException;
import uz.greenstar.jolybell.repository.*;
import uz.greenstar.jolybell.repository.spec.UserListByAdminSpecification;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final ProvinceRepository provinceRepository;
    private final DistrictRepository districtRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final ActivationCodeRepository activationCodeRepository;
    private final EmailService emailService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<UserEntity> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isEmpty())
            throw new UsernameNotFoundException(username + " not found");

        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        optionalUser.get().getRoleList().forEach(roleEntity -> {
            authorities.add(new SimpleGrantedAuthority(roleEntity.getName()));
        });
        return new User(optionalUser.get().getUsername(), optionalUser.get().getPassword(), authorities);
    }

    public UserDTO get() {
        Authentication authenticate = SecurityContextHolder.getContext().getAuthentication();
        Optional<UserEntity> optionalUser = userRepository.findByUsername(authenticate.getName());
        if (optionalUser.isEmpty() || !optionalUser.get().getActive())
            throw new ItemNotFoundException("Can not find this user");
        UserDTO userDTO = new UserDTO();
        userDTO.setFirstname(optionalUser.get().getFirstname());
        userDTO.setLastname(optionalUser.get().getLastname());
        userDTO.setUsername(optionalUser.get().getUsername());
        userDTO.setPhoneNumber(optionalUser.get().getPhoneNumber());
        userDTO.setPatronymic(optionalUser.get().getPatronymic());
        if (Objects.nonNull(optionalUser.get().getProvince()))
            userDTO.setProvince(optionalUser.get().getProvince().getId());
        if (Objects.nonNull(optionalUser.get().getDistrict()))
            userDTO.setDistrict(optionalUser.get().getDistrict().getId());
        userDTO.setAddress(optionalUser.get().getAddress());
        return userDTO;
    }

    public FilterResponse getListForAdmin(FilterRequest request) {
        Pageable pageable = PageRequest.of(request.getPage() - 1, request.getLength());
        Page<UserEntity> userEntityPage = userRepository.findAll(UserListByAdminSpecification.getFilteredPayments(request), pageable);

        FilterResponse response = new FilterResponse();

        List<Object> userDTOList = userEntityPage.getContent().stream().map(user -> {
            UserDTO userDTO = new UserDTO();
            BeanUtils.copyProperties(user, userDTO);
            userDTO.setPassword(null);
            userDTO.setRoles(user.getRoleList().stream().map(RoleEntity::getName).collect(Collectors.toList()));
            userDTO.setCreateDateTime(user.getCreateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            return userDTO;
        }).collect(Collectors.toList());

        response.setData(userDTOList);
        response.setTotalCount(userEntityPage.getTotalElements());
        response.setPages(userEntityPage.getTotalPages());
        return response;
    }

    public void createAdmin(UserDTO dto) {
        Optional<UserEntity> userEntityOptional = userRepository.findByUsername(dto.getUsername());
        if (userEntityOptional.isPresent())
            throw new BadCredentialsException("This user already exists!");
        UserEntity userEntity = new UserEntity();
        userEntity.setFirstname(dto.getFirstname());
        userEntity.setLastname(dto.getLastname());
        userEntity.setPatronymic(dto.getPatronymic());
        userEntity.setPassword(passwordEncoder.encode(dto.getPassword()));
        userEntity.setUsername(dto.getUsername());
        userEntity.setPhoneNumber(dto.getPhoneNumber());

        Optional<RoleEntity> roleOptional = roleRepository.findByName("ROLE_ADMIN");
        Optional<RoleEntity> roleOptional1 = roleRepository.findByName("ROLE_USER");
        userEntity.setRoleList(Set.of(roleOptional.get(), roleOptional1.get()));
        userRepository.save(userEntity);
    }

    public void update(UserDTO userDTO) {
        Authentication authenticate = SecurityContextHolder.getContext().getAuthentication();
        Optional<UserEntity> userEntityOptional = userRepository.findByUsername(authenticate.getName());
        if (userEntityOptional.isEmpty())
            throw new ItemNotFoundException("No user was found to update");

        UserEntity userEntity = userEntityOptional.get();
        userEntity.setFirstname(userDTO.getFirstname());
        userEntity.setLastname(userDTO.getLastname());
        userEntity.setPatronymic(userDTO.getPatronymic());
        userEntity.setPhoneNumber(userDTO.getPhoneNumber());
        userEntity.setLastUpdateTime(LocalDateTime.now());
        userRepository.save(userEntity);
    }

    public void updateAddress(UserDTO userDTO) {
        Authentication authenticate = SecurityContextHolder.getContext().getAuthentication();
        Optional<UserEntity> userEntityOptional = userRepository.findByUsername(authenticate.getName());
        if (userEntityOptional.isEmpty())
            throw new ItemNotFoundException("No user was found to update");

        UserEntity userEntity = userEntityOptional.get();
        userEntity.setAddress(userDTO.getAddress());
        userEntity.setDistrict(districtRepository.getById(userDTO.getDistrict()));
        userEntity.setProvince(provinceRepository.getById(userDTO.getProvince()));
        userEntity.setLastUpdateTime(LocalDateTime.now());
        userRepository.save(userEntity);
    }

    public void changePassword(ChangePasswordDTO dto) {
        Authentication authenticate = SecurityContextHolder.getContext().getAuthentication();
        Optional<UserEntity> userEntityOptional = userRepository.findByUsername(authenticate.getName());
        if (userEntityOptional.isEmpty())
            throw new ItemNotFoundException("No user was found to update");

        userEntityOptional.get().setPassword(passwordEncoder.encode(dto.getPassword()));
        userRepository.save(userEntityOptional.get());
    }

    public void forgotPassword(UserDTO dto) {
        Optional<UserEntity> userEntityOptional = userRepository.findByUsername(dto.getUsername());
        if (userEntityOptional.isEmpty())
            throw new UsernameNotFoundException("User not found");
        UserEntity userEntity = userEntityOptional.get();

        ActivationCodeEntity activationCodeEntity = new ActivationCodeEntity();
        activationCodeEntity.setUserId(userEntity.getId());

        boolean uniqId = false;
        while (!uniqId) {
            activationCodeEntity.setId(generateCode());
            if (activationCodeRepository.findById(activationCodeEntity.getId()).isEmpty())
                uniqId = true;
        }
        activationCodeRepository.save(activationCodeEntity);

        Thread thread = new Thread(() -> emailService.sendEmail("Forgot Password", activationCodeEntity.getId(), dto.getUsername()));
        thread.start();

    }

    private String generateCode() {
        return String.valueOf((int) (Math.random() * 899999 + 100000));
    }

    public void verifyPassword(ForgotPasswordDTO dto) {
        Optional<ActivationCodeEntity> activationCodeEntityOptional = activationCodeRepository.findById(dto.getConfirmCode());
        if (activationCodeEntityOptional.isEmpty())
            throw new ItemNotFoundException("Activation not found!");
        Optional<UserEntity> userEntityOptional = userRepository.findById(activationCodeEntityOptional.get().getUserId());
        UserEntity userEntity = userEntityOptional.get();
        userEntity.setPassword(passwordEncoder.encode(dto.getPassword()));
        userEntity.setLastUpdateTime(LocalDateTime.now());
        userRepository.save(userEntity);
    }

    public void changeActive(String id) {
        Optional<UserEntity> userEntityOptional = userRepository.findById(id);
        if (userEntityOptional.isEmpty())
            throw new UsernameNotFoundException("User not found!");
        UserEntity userEntity = userEntityOptional.get();
        userEntity.setActive(!userEntity.getActive());
        userEntity.setLastUpdateTime(LocalDateTime.now());
        userRepository.save(userEntity);
    }
}
