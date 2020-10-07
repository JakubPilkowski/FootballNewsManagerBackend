package com.footballnewsmanager.backend.repositories;

import com.footballnewsmanager.backend.models.BlackListToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BlacklistTokenRepository extends JpaRepository<BlackListToken, Long> {

    Optional<BlackListToken> findByTokenEquals(String jwt);
}
