package uz.greenstar.jolybell.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import uz.greenstar.jolybell.api.auth.LoginRequest;
import uz.greenstar.jolybell.api.auth.LoginResponse;
import uz.greenstar.jolybell.api.auth.RegistrationRequest;
import uz.greenstar.jolybell.api.jwt.JwtDTO;
import uz.greenstar.jolybell.entity.ActivationCodeEntity;
import uz.greenstar.jolybell.entity.RoleEntity;
import uz.greenstar.jolybell.entity.UserEntity;
import uz.greenstar.jolybell.exception.BadCredentialsException;
import uz.greenstar.jolybell.exception.UserCreationException;
import uz.greenstar.jolybell.repository.ActivationCodeRepository;
import uz.greenstar.jolybell.repository.RoleRepository;
import uz.greenstar.jolybell.repository.UserRepository;
//import uz.greenstar.jolybell.repository.UserRoleRepository;
//import uz.greenstar.jolybell.utils.JwtUtils;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    //    private final UserRoleRepository userRoleRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final ActivationCodeRepository activationCodeRepository;

    public ResponseEntity<LoginResponse> login(LoginRequest request) {
        try {
//            Authentication authenticate = authenticationManager
//                    .authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
//            UserDetails user = (UserDetails) authenticate.getPrincipal();
            Optional<UserEntity> optionalUser = userRepository.findByUsername(request.getUsername());
            if (optionalUser.isEmpty())
                throw new BadCredentialsException("Can not find user by this username");


            return getLoginResponse(optionalUser.get());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

    }

    public ResponseEntity<LoginResponse> registration(RegistrationRequest registration) {
        Optional<UserEntity> userEntityOptional = userRepository.findByUsername(registration.getUsername());
        if (userEntityOptional.isPresent())
            throw new UserCreationException("User already exist");
        UserEntity userEntity = new UserEntity();
        BeanUtils.copyProperties(registration, userEntity);
        userEntity.setPassword(passwordEncoder.encode(userEntity.getPassword()));
        Optional<RoleEntity> optionalRole = roleRepository.findByName("ROLE_USER");
        if (optionalRole.isEmpty())
            throw new UserCreationException("Can not find \"USER\" role");
        userEntity.getRoleList().add(optionalRole.get());

        ActivationCodeEntity activationCodeEntity = new ActivationCodeEntity();
        activationCodeEntity.setId(UUID.randomUUID().toString());
        activationCodeEntity.setUserId(userEntity.getId());
        activationCodeRepository.save(activationCodeEntity);


        Thread thread = new Thread(() -> emailService.sendEmail("Verify account", activationCodeEntity.getId(), registration.getUsername()));

        thread.start();

        userRepository.save(userEntity);
        return getLoginResponse(userEntity);
    }

    private ResponseEntity<LoginResponse> getLoginResponse(UserEntity user) {
        List<String> roleList = user.getRoleList()
                .stream()
                .map(RoleEntity::getName)
                .collect(Collectors.toList());
        JwtDTO jwtDTO = new JwtDTO(user.getId(), user.getUsername(), roleList);

        LoginResponse response = new LoginResponse();
        response.setRole(roleList);
        response.setUserId(user.getId());
        response.setUsername(user.getUsername());
//        response.setToken(jwtUtils.getJwt(jwtDTO));
        return ResponseEntity.ok().header(HttpHeaders.AUTHORIZATION, response.getToken())
                .body(response);
    }

    public String verify(String id) {
        Optional<ActivationCodeEntity> activationCodeEntityOptional = activationCodeRepository.findById(id);
        if (activationCodeEntityOptional.isEmpty() || activationCodeEntityOptional.get().getCreateTime().isBefore(LocalDateTime.now().minusMinutes(5)))
            return "Verify error!";


        Optional<UserEntity> userEntityOptional = userRepository.findById(activationCodeEntityOptional.get().getUserId());
        if (userEntityOptional.isEmpty())
            return "Verify error!";

        userEntityOptional.get().setActive(Boolean.TRUE);
        userRepository.save(userEntityOptional.get());
        return "Verify success!";
    }
}
