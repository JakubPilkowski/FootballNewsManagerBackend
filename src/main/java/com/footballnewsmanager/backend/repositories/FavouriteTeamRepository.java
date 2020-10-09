package com.footballnewsmanager.backend.repositories;

import com.footballnewsmanager.backend.models.FavouriteTeam;
import com.footballnewsmanager.backend.models.Team;
import com.footballnewsmanager.backend.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FavouriteTeamRepository extends UserSettingsRepository<FavouriteTeam> {
    Optional<FavouriteTeam> findByUserAndTeam(User user, Team team);

    void deleteByUserAndTeam(User user, Team team);

//    Optional<List<FavouriteTeam>> findByUser(User user);

//    void deleteByUser(User user);
}
