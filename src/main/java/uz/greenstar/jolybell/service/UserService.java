package uz.greenstar.jolybell.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uz.greenstar.jolybell.dto.UserDTO;
import uz.greenstar.jolybell.entity.UserEntity;
import uz.greenstar.jolybell.exceptions.ItemNotFoundException;
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
        return "1231234124";
    }
}
