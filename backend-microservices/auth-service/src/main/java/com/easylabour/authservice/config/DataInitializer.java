package com.easylabour.authservice.config;

import com.easylabour.authservice.domain.model.User;
import com.easylabour.authservice.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.easylabour.common.enums.UserRole;
import com.easylabour.common.enums.UserStatus;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.findByMobileNumber("9000000001").isEmpty()) {
            System.out.println("Seeding test user: 9000000001");
            User user = User.builder()
                    .mobileNumber("9000000001")
                    .passwordHash(passwordEncoder.encode("Indu@134"))
                    .email("indu@example.com")
                    .role(UserRole.CUSTOMER)
                    .status(UserStatus.ACTIVE)
                    .isVerified(true)
                    .build();
            userRepository.save(user);
        }
    }
}
