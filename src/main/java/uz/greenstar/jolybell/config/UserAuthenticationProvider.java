package uz.greenstar.jolybell.config;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.util.Assert;

public class UserAuthenticationProvider implements AuthenticationProvider,
        InitializingBean {

    private UserAuthenticationManagerImpl authenticationManager;

    public void afterPropertiesSet() throws Exception {
        Assert.notNull(this.authenticationManager,
                "remoteAuthenticationManager is mandatory");
    }

    public Authentication authenticate(Authentication authentication)
            throws AuthenticationException {
        String username = authentication.getPrincipal().toString();
        Object credentials = authentication.getCredentials();
        String password = credentials == null ? null : credentials.toString();

        return authenticationManager.attemptAuthentication(username, password);
    }

    public UserAuthenticationManagerImpl getAuthenticationManager() {
        return authenticationManager;
    }

    public void setAuthenticationManager(UserAuthenticationManagerImpl remoteAuthenticationManager) {
        this.authenticationManager = remoteAuthenticationManager;
    }

    public boolean supports(Class<?> authentication) {
        return (UsernamePasswordAuthenticationToken.class
                .isAssignableFrom(authentication));
    }
}