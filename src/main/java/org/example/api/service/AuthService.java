package org.example.api.service;

import lombok.RequiredArgsConstructor;
import org.example.api.dto.*;
import org.example.api.exception.*;
import org.example.api.model.*;
import org.example.api.repository.ActivationTokenRepository;
import org.example.api.repository.ClientRepository;
import org.example.api.repository.PasswordResetTokenRepository;
import org.example.api.repository.UserRepository;
import org.example.api.security.UserAdapter;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final ClientRepository clientRepository;
    private final ActivationTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;
    private final PasswordResetTokenRepository passwordResetTokenRepository;

    private static final String USER_NOT_FOUND_MESSAGE = "ERR_USER_NOT_FOUND";
    @Transactional
    public void register(RegisterRequest request) {
        Optional<User> existingUser = userRepository.findByEmail(request.email());

        if (existingUser.isPresent()) {
            User user = existingUser.get();

            if (user.isActive()) {
                throw new EmailAlreadyTakenException("Email zajęty");
            } else {
                regenerateActivationToken(user);
                return;
            }
        }

        User user = User.builder()
                .email(request.email())
                .passwordHash(passwordEncoder.encode(request.password()))
                .firstName(request.firstName())
                .lastName(request.lastName())
                .phoneNumber(request.phoneNumber())
                .role(Role.ROLE_CLIENT)
                .isActive(false)
                .build();

        userRepository.save(user);

        Client client = Client.builder()
                .user(user)
                .build();

        clientRepository.save(client);

        regenerateActivationToken(user);
    }

    @Transactional
    public void activateAccount(String token) {
        ActivationToken activationToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new InvalidTokenException("Token aktywacyjny nie istnieje"));

        if (activationToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new TokenExpiredException("Token stracił ważność");
        }

        User user = activationToken.getUser();
        user.setActive(true);
        userRepository.save(user);

        tokenRepository.delete(activationToken);
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND_MESSAGE));

        if (!user.isActive()) {
            throw new InactiveAccountException("Konto nie zostało aktywowane");
        }

        Map<String, Object> extraClaims = Map.of("role", user.getRole().name(), "id", user.getId());
        String jwtToken = jwtService.generateToken(extraClaims, new UserAdapter(user));

        UserResponse userResponse = new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getRole().name()
        );

        return new AuthResponse(jwtToken, userResponse);
    }

    @Transactional
    public void changePassword(String email, ChangePasswordRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND_MESSAGE));

        if (!passwordEncoder.matches(request.oldPassword(), user.getPasswordHash())) {
            throw new ChangePasswordOldPasswordWrongException("Niepoprawne stare hasło");
        }

        user.setPasswordHash(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
    }

    @Transactional
    public void forgotPassword(String email) {
        userRepository.findByEmail(email).ifPresent(user -> {
            String token = UUID.randomUUID().toString();

            PasswordResetToken resetToken = passwordResetTokenRepository.findByUser(user)
                    .orElse(new PasswordResetToken());

            resetToken.setToken(token);
            resetToken.setUser(user);
            resetToken.setExpiresAt(LocalDateTime.now().plusMinutes(15));

            passwordResetTokenRepository.save(resetToken);

            emailService.sendPasswordResetEmail(user.getEmail(), token);
        });
    }
    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(request.token())
                .orElseThrow(() -> new InvalidTokenException("Nieprawidłowy PasswordResetToken"));

        if (resetToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new TokenExpiredException("Token stracił ważność");
        }

        User user = resetToken.getUser();
        user.setPasswordHash(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);

        passwordResetTokenRepository.delete(resetToken);
    }

    private void regenerateActivationToken(User user) {
        String token = UUID.randomUUID().toString();

        ActivationToken activationToken = tokenRepository.findByUser(user)
                .orElse(new ActivationToken());

        activationToken.setToken(token);
        activationToken.setUser(user);
        activationToken.setExpiresAt(LocalDateTime.now().plusHours(24));

        tokenRepository.save(activationToken);

        emailService.sendActivationEmail(user.getEmail(), user.getFirstName(), token);
    }
}