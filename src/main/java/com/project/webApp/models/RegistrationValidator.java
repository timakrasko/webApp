package com.project.webApp.models;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.*;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegistrationValidator {
    @NotEmpty(message = "User name should not be empty")
    @Size(min = 2, max= 30, message = "Size of user name should be in range between 2 and 30")
    private String userName;
    @NotEmpty(message = "Email should not be empty")
    @Email(message = "Email should be valid")
    private String email;
    @NotEmpty(message = "Password should not be empty")
    @Size(min = 4, max= 20, message = "Size of password should be in range between 4 and 20")
    private String password;
}