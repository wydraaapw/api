package org.example.api.controller;

import lombok.RequiredArgsConstructor;
import org.example.api.dto.UserResponse;
import org.example.api.model.User;
import org.example.api.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser(Authentication authentication){
        User user = userService.getUserByEmail(authentication.getName());

        UserResponse userResponse = new UserResponse(
          user.getId(),
          user.getEmail(),
          user.getFirstName(),
          user.getLastName(),
          user.getRole().name()
        );

        return ResponseEntity.ok(userResponse);
    }
}
