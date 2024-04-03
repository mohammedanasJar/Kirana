package com.example.Kirana.security;

import com.example.Kirana.controllers.Report;
import com.example.Kirana.models.UserDetails;
import com.example.Kirana.repos.UserRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private static final Logger logger = LoggerFactory.getLogger(Report.class);

    @Autowired
    UserRepo ur;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http
                .csrf(AbstractHttpConfigurer::disable)
                .anonymous().and()
                .authorizeHttpRequests((authorize) -> authorize
                        .requestMatchers("/exchange_rates/**").hasAnyAuthority("ADMIN","USER")
                        .requestMatchers("/transaction/generate/**").hasAnyAuthority("ADMIN","USER")
                        .requestMatchers("/transaction/**").hasAnyAuthority("ADMIN")
                        .requestMatchers("/record/**").hasAnyAuthority("ADMIN", "USER")
                            .anyRequest().authenticated()
                )
                .httpBasic();
//        http
//                .csrf(AbstractHttpConfigurer::disable)
//                .anonymous().and()
//                .authorizeHttpRequests((authorize) -> authorize
//                        .requestMatchers("/exchange_rates/**")
//                            .permitAll()
//                        .requestMatchers("/transaction/**").hasAnyAuthority("ADMIN")
//                        .requestMatchers("/transaction/generate/**").hasAnyAuthority("ADMIN","USER")
//                        .requestMatchers("/record/**").hasAnyAuthority("ADMIN", "USER")
//                            .anyRequest().authenticated()
//                )
//                .httpBasic();

//        http.
//                authorizeHttpRequests(request -> request
//                        .requestMatchers(new AntPathRequestMatcher("/private/**"))
//                        .authenticated())
//                .httpBasic(Customizer.withDefaults())
//                .authorizeHttpRequests(request -> request
//                        .requestMatchers(new AntPathRequestMatcher("/exchange_rates/**"))
//                        .permitAll())
//                .authorizeHttpRequests(request -> request
//                        .requestMatchers(new AntPathRequestMatcher("/exchange_rates/**"))
//                        .anonymous())
//                .httpBasic();
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService() {

        User.UserBuilder users = User.builder().passwordEncoder(password -> passwordEncoder().encode(password));
        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
        logger.info("Generating InMemory UserBase from MongoDB");
        for (UserDetails ud : ur.findAll()) {
            manager.createUser(users.username(ud.getUsername()).password(ud.getPassword()).authorities(ud.getRole()).build());
        }
        return manager;
    }
}
