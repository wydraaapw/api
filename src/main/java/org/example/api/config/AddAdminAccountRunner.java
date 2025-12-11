package org.example.api.config;

import lombok.RequiredArgsConstructor;
import org.example.api.model.Role;
import org.example.api.model.User;
import org.example.api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AddAdminAccountRunner implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${admin.account.email}")
    private String adminEmail;
    @Value("${admin.account.password}")
    private String adminPassword;


    @Override
    public void run(String... args) throws Exception {
        if (!userRepository.existsByRole(Role.ROLE_ADMIN)){
            User admin = User.builder()
                    .email(adminEmail)
                    .passwordHash(passwordEncoder.encode(adminPassword))
                    .firstName("admin")
                    .lastName("admin")
                    .phoneNumber("000000000")
                    .role(Role.ROLE_ADMIN)
                    .isActive(true)
                    .build();

            userRepository.save(admin);
        }
    }
}
