package com.footballnewsmanager.backend.repositories;

import com.footballnewsmanager.backend.models.Marker;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MarkerRepository extends JpaRepository<Marker, Long> {

    Optional<Marker>findByName(String name);

    boolean existsByName(String name);
}
