package uz.greenstar.jolybell.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import uz.greenstar.jolybell.dto.UserDTO;
import uz.greenstar.jolybell.entity.UserEntity;
import uz.greenstar.jolybell.exception.ItemNotFoundException;
import uz.greenstar.jolybell.repository.UserRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;

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

//    private final AuthenticationManager authenticationManager;

    public String login(UserDTO userDTO) {
        Optional<UserEntity> optionalUser = userRepository.findByUsername(userDTO.getUsername());
//        if (optionalUser.isEmpty())
//            throw new ItemNotFoundException("User not found!");
        System.out.println(userDTO);
        return "1231234124";
    }

    public UserDTO get() {
        Authentication authenticate = SecurityContextHolder.getContext().getAuthentication();
        Optional<UserEntity> optionalUser = userRepository.findByUsername(authenticate.getName());
        if (optionalUser.isEmpty())
            throw new ItemNotFoundException("Can not find this user");
        UserDTO userDTO = new UserDTO();
        userDTO.setFirstname(optionalUser.get().getFirstname());
        userDTO.setLastname(optionalUser.get().getLastname());
        userDTO.setUsername(optionalUser.get().getUsername());
        userDTO.setPhoneNumber(optionalUser.get().getPhoneNumber());
        userDTO.setPatronymic(optionalUser.get().getPatronymic());
        return userDTO;
    }
}
