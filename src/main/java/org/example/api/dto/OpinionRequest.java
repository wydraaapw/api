package org.example.api.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record OpinionRequest(
        @NotNull @Size(max = 500) String content,
        @NotNull LocalDateTime createdAt,
        @NotNull Long clientId,
        @Min(1) @Max(5) Integer rating
        )
{}
