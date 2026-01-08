package org.example.api.dto;

import java.time.LocalDateTime;

public record NotificationResponse(
        Long id,
        String title,
        String content,
        LocalDateTime sentAt,
        boolean isRead
) {}
