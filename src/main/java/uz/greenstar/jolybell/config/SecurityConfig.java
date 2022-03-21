package uz.greenstar.jolybell.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    UserAuthenticationService authenticationManager;

    public static String[] NOT_FILTER = new String[]{"/", "/user/login"};

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authenticationProvider());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeRequests().antMatchers(NOT_FILTER).permitAll()
                .and()
                .authorizeRequests().anyRequest().authenticated()
                .and()
                .formLogin().loginPage("/login.do")
                .usernameParameter("username").passwordParameter("password").permitAll()
                .loginProcessingUrl("/login")
                .successForwardUrl("/hi")
                .failureForwardUrl("/login.do?error=true")
                .and()
                .logout().logoutUrl("/logout.do").logoutSuccessUrl("/login.do?logout=true")
                .and()
                .exceptionHandling().accessDeniedPage("/login.do?denied=true")/*.and().exceptionHandling().authenticationEntryPoint(new BasicAuthenticationEntryPoint())*/;
    }

    @Bean
    public UserAuthenticationProvider authenticationProvider() {
        UserAuthenticationProvider userAuthenticationProvider = new UserAuthenticationProvider();
        userAuthenticationProvider.setAuthenticationManager(authenticationManager1());
        return userAuthenticationProvider;
    }

    @Bean
    public UserAuthenticationManagerImpl authenticationManager1() {
        UserAuthenticationManagerImpl userAuthenticationManager = new UserAuthenticationManagerImpl();
        userAuthenticationManager.setAuthenticationManager(authenticationManager);
        return userAuthenticationManager;
    }
}
