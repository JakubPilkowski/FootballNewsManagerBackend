package com.footballnewsmanager.backend.repositories;

import com.footballnewsmanager.backend.models.UserNewsLike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserNewsLikeRepository extends JpaRepository<UserNewsLike, Long> {
}
