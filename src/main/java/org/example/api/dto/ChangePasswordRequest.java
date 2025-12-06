package org.example.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangePasswordRequest(
    @NotBlank(message = "ERR_OLD_PASSWORD_REQUIRED")
    String oldPassword,

    @NotBlank(message = "ERR_NEW_PASSWORD_REQUIRED")
    @Size(min = 6, message = "ERR_PASSWORD_TOO_SHORT")
    String newPassword
) {}