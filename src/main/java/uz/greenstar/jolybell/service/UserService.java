package uz.greenstar.jolybell.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import uz.greenstar.jolybell.api.filterForm.FilterRequest;
import uz.greenstar.jolybell.api.filterForm.FilterResponse;
import uz.greenstar.jolybell.dto.UserDTO;
import uz.greenstar.jolybell.entity.RoleEntity;
import uz.greenstar.jolybell.entity.UserEntity;
import uz.greenstar.jolybell.exception.ItemNotFoundException;
import uz.greenstar.jolybell.repository.RoleRepository;
import uz.greenstar.jolybell.repository.spec.UserListByAdminSpecification;
import uz.greenstar.jolybell.repository.UserRepository;

import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

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

    public FilterResponse getListForAdmin(FilterRequest request) {
        Pageable pageable = PageRequest.of(request.getPage(), request.getLength());
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
        UserEntity userEntity = new UserEntity();
        userEntity.setFirstname(dto.getFirstname());
        userEntity.setLastname(dto.getLastname());
        userEntity.setPatronymic(dto.getPatronymic());
        userEntity.setPassword(dto.getPassword());
        userEntity.setUsername(dto.getUsername());
        userEntity.setPhoneNumber(dto.getPhoneNumber());

        Optional<RoleEntity> roleOptional = roleRepository.findByName("ROLE_ADMIN");
        userEntity.setRoleList(Set.of(roleOptional.get()));
        userRepository.save(userEntity);
    }
}
