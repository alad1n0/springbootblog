package edu.itstep.final_project_v1.web.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true)
public class WebSecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> {
                        auth.requestMatchers(antMatcher("/css/**")).permitAll();
                        auth.requestMatchers(antMatcher("/js/**")).permitAll();
                        auth.requestMatchers(antMatcher("/images/**")).permitAll();
                        auth.requestMatchers(antMatcher("/fonts/**")).permitAll();
                        auth.requestMatchers(antMatcher("/webjars/**")).permitAll();
                        auth.requestMatchers(antMatcher("/")).permitAll();
                        auth.requestMatchers(antMatcher("/register/**")).permitAll();
                        auth.requestMatchers(antMatcher("/posts/**")).permitAll();
                        auth.requestMatchers(antMatcher("/aboute")).permitAll();
                        auth.requestMatchers(antMatcher("/contact")).permitAll();
                        auth.requestMatchers(antMatcher("/search/**")).permitAll();
                        auth.requestMatchers(antMatcher("/admin/**")).hasAuthority("ROLE_ADMIN");
                        auth.anyRequest().authenticated();
                })
                .formLogin(formLogin -> formLogin
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .usernameParameter("email")
                        .passwordParameter("password")
                        .defaultSuccessUrl("/", true)
                        .failureUrl("/login?error")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                        .permitAll()
                );

        return http.build();
    }
}