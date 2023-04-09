package com.backend.pdfs.security;

import com.backend.pdfs.entities.User;
import com.backend.pdfs.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Collection;

public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException{
        return userService.getUserByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found " + username));
    }

}
