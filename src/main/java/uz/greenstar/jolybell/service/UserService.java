package uz.greenstar.jolybell.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uz.greenstar.jolybell.dto.UserDTO;
import uz.greenstar.jolybell.entity.UserEntity;
import uz.greenstar.jolybell.repository.UserRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
private final UserRepository userRepository;

    public String login(UserDTO userDTO) {
        Optional<UserEntity> optionalUser = userRepository.findByUsername(userDTO.getUsername());
//        if (optionalUser.isEmpty())
//            throw new ItemNotFoundException("User not found!");
        System.out.println(userDTO);
        return "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhYnJvcmJla2dyZWVuNDk3QGdtYWlsLmNvbSIsInJvbGVzIjoiVVNFUiIsInVzZXJBY2NvdW50SWQiOiJiZWU5YzE5Ny0wMGU1LTRkZTYtOTliNC00ODIxMzZiNTQ3ZTQiLCJpc3MiOiJlLXRpY2tldC5yYWlsd2F5LnV6IiwiZXhwIjoxNjQ3OTc0MTAwLCJ1c2VySWQiOiI4ODU2YzYyNi0yYjUyLTQyZjctOTc1ZC00MjFhMTU2YmQwNGMifQ.s2NNRl8yp6F0YfsKZaoOSraqqMwH5x8og9Olhicivwg";
    }
}
