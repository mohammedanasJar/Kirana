package com.example.Kirana.utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class AuthorisationDetails {

    public String getUsernameFromAuthorizationHeader() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName();
        } else {
            return null;
        }
    }
}