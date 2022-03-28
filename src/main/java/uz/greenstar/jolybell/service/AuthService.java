package uz.greenstar.jolybell.service;

import lombok.RequiredArgsConstructor;
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
import uz.greenstar.jolybell.entity.RoleEntity;
import uz.greenstar.jolybell.entity.UserEntity;
import uz.greenstar.jolybell.exception.BadCredentialsException;
import uz.greenstar.jolybell.exception.UserCreationException;
import uz.greenstar.jolybell.repository.RoleRepository;
import uz.greenstar.jolybell.repository.UserRepository;
//import uz.greenstar.jolybell.repository.UserRoleRepository;
//import uz.greenstar.jolybell.utils.JwtUtils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
//    private final UserRoleRepository userRoleRepository;
    private final BCryptPasswordEncoder passwordEncoder;

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
        UserEntity userEntity = new UserEntity();
        BeanUtils.copyProperties(registration, userEntity);
        userEntity.setPassword(passwordEncoder.encode(userEntity.getPassword()));
        Optional<RoleEntity> optionalRole = roleRepository.findByName("ROLE_USER");
        if (optionalRole.isEmpty())
            throw new UserCreationException("Can not find \"USER\" role");
        userEntity.getRoleList().add(optionalRole.get());

        userRepository.save(userEntity);
        return getLoginResponse(userEntity);
    }

    private ResponseEntity<LoginResponse> getLoginResponse(UserEntity user) {
        List<String> roleList = user.getRoleList()
                .stream()
                .map(userRoleEntity -> userRoleEntity.getName())
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
}
