package com.footballnewsmanager.backend.services;

import com.footballnewsmanager.backend.models.PasswordResetToken;
import com.footballnewsmanager.backend.repositories.PasswordResetTokenRepository;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Optional;


@Service
public class ResetPasswordService {

    public String validateToken(String token, PasswordResetTokenRepository passwordResetTokenRepository){
        Optional<PasswordResetToken> tokenOptional = passwordResetTokenRepository.findByToken(token);
        return tokenOptional.map(passwordResetToken -> isTokenExpired(passwordResetToken) ? "Przedawniony Token" : "Ok").orElse("Niepoprawny Token");
    }

    public boolean isTokenExpired(PasswordResetToken passwordResetToken){
        Calendar calendar = Calendar.getInstance();
        return passwordResetToken.getExpiryDate().before(calendar.getTime());
    }

}
