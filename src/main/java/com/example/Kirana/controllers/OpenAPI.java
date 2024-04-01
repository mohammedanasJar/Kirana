package com.example.Kirana.controllers;

import com.example.Kirana.models.AuthenticationRequest;
import com.example.Kirana.models.AuthenticationResponse;
import com.example.Kirana.models.User;
import com.example.Kirana.repos.UserRepo;
import com.example.Kirana.services.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class OpenAPI {
    private final AuthenticationService authenticationService;

    @Autowired
    UserRepo ur;
    private final PasswordEncoder passwordEncoder;
    @PostMapping
    public ResponseEntity createUser(@RequestBody User user){
        User u=new User();
        u.setUsername(user.getUsername());
        u.setRole(user.getRole());
        u.setLimit(user.getLimit());
        u.setPassword(passwordEncoder.encode(user.getPassword()));
        ur.save(u);
        return ResponseEntity.ok("Saved User");
    }
    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> signin(@RequestBody AuthenticationRequest authenticationRequest){
        return ResponseEntity.ok(authenticationService.signin(authenticationRequest));
    }

}
