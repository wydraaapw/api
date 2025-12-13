package org.example.api.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.api.dto.WaiterRequest;
import org.example.api.dto.WaiterResponse;
import org.example.api.exception.ResourceNotFoundException;
import org.example.api.model.Role;
import org.example.api.model.User;
import org.example.api.model.Waiter;
import org.example.api.repository.UserRepository;
import org.example.api.repository.WaiterRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WaiterService {
    private final WaiterRepository waiterRepository;
    private final UserRepository userRepository;

    public List<WaiterResponse> findAll(){
        return waiterRepository.findAll().stream().map(this::mapToResponse).toList();
    }

    @Transactional
    public WaiterResponse create(WaiterRequest waiterRequest){
        User user = User.builder()
                .email(waiterRequest.email())
                .passwordHash(waiterRequest.password())
                .firstName(waiterRequest.firstName())
                .lastName(waiterRequest.lastName())
                .phoneNumber(waiterRequest.phoneNumber())
                .role(Role.ROLE_WAITER)
                .build();

        Waiter waiter = Waiter.builder()
                .user(user)
                .hireDate(LocalDate.now())
                .speaksEnglish(waiterRequest.speaksEnglish())
                .build();

       Waiter saved = waiterRepository.save(waiter);
       return mapToResponse(saved);
    }

    @Transactional
    public void delete(Long id) {

        Waiter waiter = waiterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Kelner nie istnieje"));

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
