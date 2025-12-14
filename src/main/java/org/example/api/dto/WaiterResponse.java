package org.example.api.dto;

import java.time.LocalDate;

public record WaiterResponse(
        Long id, String email, String firstName,
        String lastName, String phoneNumber,
        boolean speaksEnglish, LocalDate hireDate
) {}
