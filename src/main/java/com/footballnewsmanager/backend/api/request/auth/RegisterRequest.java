package com.footballnewsmanager.backend.api.request.auth;

import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class RegisterRequest {

    @NotBlank(message = "Login wymagany")
    @Size(min = 4, max = 20, message = "Login musi się składać z od 3 do 20 znaków")
    private String username;

    @NotBlank(message = "Email wymagany")
    @Size(max = 40, message = "Email nie może składać się z więcej niż 40 znaków")
    @Email(message = "Niepoprawny mail")
    private String email;

    @NotBlank(message = "Hasło wymagane")
    @Size(min = 8, max = 30, message = "Hasło musi zawierać od 8 do 30 znaków")
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
