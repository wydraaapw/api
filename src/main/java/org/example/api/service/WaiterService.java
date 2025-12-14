package org.example.api.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.api.dto.WaiterRequest;
import org.example.api.dto.WaiterResponse;
import org.example.api.exception.EmailAlreadyTakenException;
import org.example.api.exception.ResourceInUseException;
import org.example.api.exception.ResourceNotFoundException;
import org.example.api.model.Role;
import org.example.api.model.User;
import org.example.api.model.Waiter;
import org.example.api.repository.UserRepository;
import org.example.api.repository.WaiterRepository;
import org.example.api.repository.WorkShiftRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WaiterService {
    private final WaiterRepository waiterRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final WorkShiftRepository workShiftRepository;

    public List<WaiterResponse> findAll(){
        return waiterRepository.findAll().stream().map(this::mapToResponse).toList();
    }

    @Transactional
    public WaiterResponse create(WaiterRequest waiterRequest){

        if (userRepository.existsByEmail(waiterRequest.email())){
            throw new EmailAlreadyTakenException("Kelner z takim adresem e-mail już istnieje.");
        }

        User user = User.builder()
                .email(waiterRequest.email())
                .passwordHash(passwordEncoder.encode(waiterRequest.password()))
                .firstName(waiterRequest.firstName())
                .lastName(waiterRequest.lastName())
                .phoneNumber(waiterRequest.phoneNumber())
                .role(Role.ROLE_WAITER)
                .isActive(true)
                .build();

        Waiter waiter = Waiter.builder()
                .user(user)
                .hireDate(LocalDate.now())
                .speaksEnglish(waiterRequest.speaksEnglish())
                .build();

        userRepository.save(user);
        Waiter saved = waiterRepository.save(waiter);
        return mapToResponse(saved);
    }

    @Transactional
    public void delete(Long id) {

        Waiter waiter = waiterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Kelner nie istnieje"));

        if (workShiftRepository.existsWorkShiftByWaiterId(waiter.getId())){
            throw new ResourceInUseException("Nie można usunąć kelnera, który posiada przypisane zmiany.");
        }

        waiterRepository.delete(waiter);
        userRepository.delete(waiter.getUser());
    }

    private WaiterResponse mapToResponse(Waiter waiter) {
        User u = waiter.getUser();
        return new WaiterResponse(
                waiter.getId(),
                u.getEmail(),
                u.getFirstName(),
                u.getLastName(),
                u.getPhoneNumber(),
                waiter.isSpeaksEnglish(),
                waiter.getHireDate()
        );
    }
}
