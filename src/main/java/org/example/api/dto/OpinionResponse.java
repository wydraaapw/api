package org.example.api.dto;

import java.time.LocalDateTime;

public record OpinionResponse(
        Long id,
        String content,
        LocalDateTime createdAt,
        String userFirstName,
        String userLastName,
        Integer rating
)
{}
