package com.footballnewsmanager.backend.repositories;

import com.footballnewsmanager.backend.models.UserNewsDislike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserNewsDislikesRepository extends JpaRepository<UserNewsDislike, Long> {
}
