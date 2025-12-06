package org.example.api.dto;

public record AuthResponse(
        String token,
        UserResponse user
) {}