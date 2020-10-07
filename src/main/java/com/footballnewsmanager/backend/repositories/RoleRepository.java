package com.footballnewsmanager.backend.repositories;

import com.footballnewsmanager.backend.models.Role;
import com.footballnewsmanager.backend.models.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleName roleName);
}
