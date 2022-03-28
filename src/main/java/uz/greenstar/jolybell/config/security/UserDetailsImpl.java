package uz.greenstar.jolybell.config.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import uz.greenstar.jolybell.entity.UserEntity;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class UserDetailsImpl implements UserDetails {
    private String id;
    private String username;
    private String password;
    private boolean enabled;
    private List<String> roles;

    private List<GrantedAuthority> authorityList;

    public UserDetailsImpl(UserEntity user) {
        this.id = user.getId();
        username = user.getUsername();
        password = user.getPassword();
        enabled = user.getActive();
//        roles = user.getRoleList().stream().map(userRoleEntity -> userRoleEntity.getRole().getName()).collect(Collectors.toList());
        roles = List.of("ROLE_USER");
        this.authorityList = roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorityList;
    }

    @Override
    public String getPassword() {
        System.out.println("CustomUserDetails: getPassword()");
        return password;
    }

    @Override
    public String getUsername() {
        System.out.println("CustomUserDetails: getUsername()");
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }


    @Override
    public String toString() {
        return "CustomUserDetails{" +
                "id=" + id +
                ", userName='" + username + '\'' +
                ", password='" + password + '\'' +
                ", enabled=" + enabled +
                ", role='" + roles + '\'' +
                ", authorityList=" + authorityList +
                '}';
    }

    public String getId() {
        return id;
    }
}
