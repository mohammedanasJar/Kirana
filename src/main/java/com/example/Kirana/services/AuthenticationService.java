package com.example.Kirana.services;

import com.example.Kirana.models.AuthenticationRequest;
import com.example.Kirana.models.AuthenticationResponse;
import com.example.Kirana.models.User;
import com.example.Kirana.repos.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepo userRepo;
    private final AuthenticationManager authenticationManager;
private final JWTService jwtService;
    public AuthenticationResponse signin(AuthenticationRequest authenticationRequest){
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(),authenticationRequest.getPassword()));
        var user=userRepo.findByUsername(authenticationRequest.getUsername()).orElseThrow(()->new IllegalArgumentException("Invalid Username"));
        var token=jwtService.generateToken(user);
        var refreshToken=jwtService.generateRefreshToken(new HashMap<>(),user);

        AuthenticationResponse authenticationResponse=new AuthenticationResponse();

        authenticationResponse.setToken(token);
        authenticationResponse.setRefreshToken(refreshToken);
        return authenticationResponse;

    }

    public AuthenticationResponse prolongSignin(AuthenticationResponse refreshTokenRequest){
        String username=jwtService.extractUsername(refreshTokenRequest.getToken());
        User user=userRepo.findByUsername(username).orElseThrow();
        if(jwtService.isTokenValid(refreshTokenRequest.getToken(),user)){
            var token=jwtService.generateToken(user);
            AuthenticationResponse authenticationResponse=new AuthenticationResponse();
            authenticationResponse.setToken(token);
            authenticationResponse.setRefreshToken(refreshTokenRequest.getToken());
            return authenticationResponse;
        }
        return null;
    }
}
