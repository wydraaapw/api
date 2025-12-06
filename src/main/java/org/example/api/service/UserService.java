package org.example.api.service;

import lombok.RequiredArgsConstructor;
import org.example.api.exception.UserNotFoundException;
import org.example.api.model.User;
import org.example.api.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public User getUserByEmail(String email){
        return userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException("ERR_USER_NOT_FOUND"));
    }
}
