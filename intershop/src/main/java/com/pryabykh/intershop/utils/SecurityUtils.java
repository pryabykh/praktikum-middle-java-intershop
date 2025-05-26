package com.pryabykh.intershop.utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

public class SecurityUtils {

    public static String fetchUserRole(Authentication authentication) {
        return authentication.getAuthorities().stream().findFirst().map(GrantedAuthority::getAuthority).orElse(null);
    }
}
