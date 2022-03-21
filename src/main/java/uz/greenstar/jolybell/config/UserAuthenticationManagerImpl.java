package uz.greenstar.jolybell.config;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.rcp.RemoteAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.util.Assert;

public class UserAuthenticationManagerImpl implements InitializingBean {
    private AuthenticationManager authenticationManager;

    public void afterPropertiesSet() throws Exception {
        Assert.notNull(this.authenticationManager, "authenticationManager is required");
    }

    public Authentication attemptAuthentication(String username,
                                                String password) throws RemoteAuthenticationException {
        UsernamePasswordAuthenticationToken request = new UsernamePasswordAuthenticationToken(
                username, password);

        try {
            return authenticationManager.authenticate(request);
        }
        catch (Exception authEx) {
            throw new BadCredentialsException(authEx.getMessage());
        }
    }

    protected AuthenticationManager getAuthenticationManager() {
        return authenticationManager;
    }

    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }
}
