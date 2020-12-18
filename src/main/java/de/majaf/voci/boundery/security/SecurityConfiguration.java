package de.majaf.voci.boundery.security;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailsService userService;

    @Autowired
    private SecurityUtilities securityUtilities;

    private static final String[] ALLOW_ACCESS_WITHOUT_AUTHENTICATION = {"/", "/login", "/register", "/css/**", "/img/**", "/fonts/**"};

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers(ALLOW_ACCESS_WITHOUT_AUTHENTICATION)
                .permitAll().anyRequest().authenticated();
        http
                .formLogin().loginPage("/login").permitAll().defaultSuccessUrl("/main").failureUrl("/failedLogin")
                .and()
                .logout().logoutRequestMatcher(new AntPathRequestMatcher("/logout")).logoutSuccessUrl("/login")
                .deleteCookies("remember-me").permitAll()
                .and().rememberMe();
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService).passwordEncoder(passwordEncoder());
    }

    private BCryptPasswordEncoder passwordEncoder() {
        return securityUtilities.passwordEncoder();
    }
}