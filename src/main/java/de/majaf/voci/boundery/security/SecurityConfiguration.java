package de.majaf.voci.boundery.security;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@Scope("singleton")
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailsService userService;

    @Autowired
    private SecurityUtilities securityUtilities;

    @Autowired
    private CustomLogoutHandler customLogoutHandler;

    private static final String[] ALLOW_ACCESS_WITHOUT_AUTHENTICATION = {
            "/", "/login", "/register",
            "/css/**", "/img/**", "/js/**",
            "/invitation/**",
            "/webjars/**", "/chat/**",
            "/test/**", "/api/**", // TODO remove test
            "/call/leave/**", "/call/ended",
            "/download/**", "/favicon.ico"};

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers(ALLOW_ACCESS_WITHOUT_AUTHENTICATION)
                .permitAll()
                .antMatchers("/**").hasRole("USER").anyRequest().authenticated();
        http
                .formLogin().loginPage("/login").permitAll().defaultSuccessUrl("/main").failureUrl("/failedLogin")
                .and()
                .logout().logoutRequestMatcher(new AntPathRequestMatcher("/logout")).logoutSuccessUrl("/login")
                .addLogoutHandler(customLogoutHandler)
                .deleteCookies("remember-me-voci").permitAll()
                .and().rememberMe();
        http.
                csrf().ignoringAntMatchers("/api/**");
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService).passwordEncoder(passwordEncoder());
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    private BCryptPasswordEncoder passwordEncoder() {
        return securityUtilities.passwordEncoder();
    }
}