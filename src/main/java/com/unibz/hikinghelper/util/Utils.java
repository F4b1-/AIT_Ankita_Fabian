package com.unibz.hikinghelper.util;

import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class Utils {


    public  static boolean hasRole(String role, Collection<GrantedAuthority> authorities) {
        boolean hasRole = false;
        for (GrantedAuthority authority : authorities) {
            hasRole = authority.getAuthority().equals(role);
            if (hasRole) {
                break;
            }
        }
        return hasRole;
    }

}
