package com.lcwd.auth.auth_app.security;

import com.lcwd.auth.auth_app.Repository.UserRepository;
import com.lcwd.auth.auth_app.entity.Users;
import com.lcwd.auth.auth_app.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Users users = userRepository.findByEmail(username).orElseThrow(() -> new ResourceNotFoundException("Invalid Email or password.."));
        return users;
    }
}
