package com.project.webApp.models;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegistrationRequest {
    @NotEmpty(message = "User name should not be empty")
    @Size(min = 2, max= 30, message = "Size of user name should be in range between 2 and 30")
    private String userName;
    @NotEmpty(message = "Email should not be empty")
    @Email(message = "Email should be valid")
    private String email;
    @NotEmpty(message = "Password should not be empty")
    @Size(min = 4, max= 20, message = "Size of first name should be in range between 2 and 30")
    private String password;
}