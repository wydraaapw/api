package org.example.api.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.api.dto.WaiterRequest;
import org.example.api.dto.WaiterResponse;
import org.example.api.exception.EmailAlreadyTakenException;
import org.example.api.exception.ResourceInUseException;
import org.example.api.exception.ResourceNotFoundException;
import org.example.api.model.ReservationStatus;
import org.example.api.model.Role;
import org.example.api.model.User;
import org.example.api.model.Waiter;
import org.example.api.repository.ReservationRepository;
import org.example.api.repository.UserRepository;
import org.example.api.repository.WaiterRepository;
import org.example.api.repository.WorkShiftRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class WaiterService {
    private final WaiterRepository waiterRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final WorkShiftRepository workShiftRepository;
    private final ReservationRepository reservationRepository;

    public List<WaiterResponse> findAll(){
        return waiterRepository.findAllActive().stream().map(this::mapToResponse).toList();
    }

    @Transactional
    public WaiterResponse create(WaiterRequest waiterRequest){

        if (userRepository.existsByEmail(waiterRequest.email())){
            throw new EmailAlreadyTakenException("Konto z takim adresem e-mail już istnieje.");
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

        if (workShiftRepository.existsFutureShifts(waiter.getId(), LocalDateTime.now())){
            throw new ResourceInUseException("Nie można usunąć kelnera, posiada on nadchodzące zmiany w pracy.");
        }

        if (reservationRepository.existsByWaiterIdAndStatusIn(waiter.getId(), Set.of(ReservationStatus.PENDING, ReservationStatus.CONFIRMED))){
            throw new ResourceInUseException("Nie można usunąć kelnera, który posiada przypisane nadchodzące lub oczekujące rezerwację.");
        }

        User user =  waiter.getUser();
        user.setActive(false);

        userRepository.save(user);
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
