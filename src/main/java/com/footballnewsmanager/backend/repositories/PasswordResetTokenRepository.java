package com.footballnewsmanager.backend.repositories;

import com.footballnewsmanager.backend.models.PasswordResetToken;
import com.footballnewsmanager.backend.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {


    Optional<PasswordResetToken> findByToken(String token);

    boolean existsByUser(User user);

    void deleteByUser(User user);
}
