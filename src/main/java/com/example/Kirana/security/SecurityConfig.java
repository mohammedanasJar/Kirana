package com.example.Kirana.security;

import com.example.Kirana.controllers.ReportAPI;
import com.example.Kirana.enums.Role;
import com.example.Kirana.repos.UserRepo;
import com.example.Kirana.services.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private static final Logger logger = LoggerFactory.getLogger(ReportAPI.class);
    private final UserService userService;

    @Autowired
    private  final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    UserRepo ur;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests((authorize) -> authorize
                        .requestMatchers(HttpMethod.POST,"/user/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/transaction/fetchrate/**").hasAnyRole(Role.ADMIN.name(),Role.USER.name())
                        .requestMatchers(HttpMethod.GET, "/transaction/fetchrate/**").hasAnyAuthority(Role.ADMIN.name(),Role.USER.name())
                        .requestMatchers(HttpMethod.GET, "/transaction/generateData/**").hasAnyRole(Role.ADMIN.name(),Role.USER.name())
                        .requestMatchers(HttpMethod.POST, "/transaction/record/**").hasRole(Role.ADMIN.name())
                        .requestMatchers("/record/**").hasAnyRole("ADMIN", "USER")
                        .anyRequest().authenticated()
                )
                .sessionManagement(manager->manager.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(
                        jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider=new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userService.userDetailsService());
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
