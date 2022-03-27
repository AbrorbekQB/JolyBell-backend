package uz.greenstar.jolybell.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
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
import uz.greenstar.jolybell.utils.JwtUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final JwtUtils jwtUtils;

    public LoginResponse login(LoginRequest request) {
        Optional<UserEntity> optionalUser = userRepository.findByUsername(request.getUsername());
        if (optionalUser.isEmpty())
            throw new BadCredentialsException("Can not find user by this username");
        return getLoginResponse(optionalUser.get());
    }

    public LoginResponse registration(RegistrationRequest registration) {
        UserEntity userEntity = new UserEntity();
        BeanUtils.copyProperties(registration, userEntity);
        userEntity.setCreateTime(LocalDateTime.now());
        userEntity.setLastUpdateTime(LocalDateTime.now());
        Optional<RoleEntity> optionalRole = roleRepository.findByRole("USER");
        if (optionalRole.isEmpty())
            throw new UserCreationException("Can not find \"USER\" role");
        userEntity.getRoles().add(optionalRole.get());
        userRepository.save(userEntity);
        return getLoginResponse(userEntity);
    }

    private LoginResponse getLoginResponse(UserEntity user) {
        List<String> roleList = user.getRoles()
                .stream()
                .map(RoleEntity::getRole)
                .collect(Collectors.toList());
        JwtDTO jwtDTO = new JwtDTO(user.getId(), user.getUsername(), roleList);

        LoginResponse response = new LoginResponse();
        response.setRole(roleList);
        response.setUserId(user.getId());
        response.setUsername(user.getUsername());
        response.setToken(jwtUtils.getJwt(jwtDTO));
        return response;
    }
}
