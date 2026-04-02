package com.finance.dashboard.dto;


import com.finance.dashboard.entity.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserRequestDTO {
    @NotBlank(message = "Name required")
    private String name;

    @Email
    @NotBlank(message = "Email required")
    private String email;

    @NotBlank(message = "Password required")
    private String password;

    @NotNull(message = "Role required")
    private Role role;
}
