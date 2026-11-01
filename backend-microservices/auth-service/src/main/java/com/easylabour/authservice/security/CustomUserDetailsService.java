package com.easylabour.authservice.security;

import com.easylabour.authservice.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String mobileNumber) throws UsernameNotFoundException {
        return userRepository.findByMobileNumber(mobileNumber)
                .map(user -> org.springframework.security.core.userdetails.User.builder()
                        .username(user.getMobileNumber())
                        .password(user.getPasswordHash())
                        .authorities(Collections.singletonList(() -> "ROLE_" + user.getRole().name()))
                        .build())
                .orElseThrow(() -> new UsernameNotFoundException("User not found with mobile number: " + mobileNumber));
    }
}
