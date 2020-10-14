package com.footballnewsmanager.backend.services;

import com.footballnewsmanager.backend.api.response.BaseResponse;
import com.footballnewsmanager.backend.auth.UserPrincipal;
import com.footballnewsmanager.backend.controllers.OnPresentInterface;
import com.footballnewsmanager.backend.exceptions.BadRequestException;
import com.footballnewsmanager.backend.exceptions.ResourceNotFoundException;
import com.footballnewsmanager.backend.models.*;
import com.footballnewsmanager.backend.repositories.UserRepository;
import com.footballnewsmanager.backend.repositories.UserSettingsRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService extends BaseService{


    public User checkUserExistByUsernameAndOnSuccess(String username, UserRepository userRepository, OnPresentInterface<User> onPresentInterface) {
        return userRepository.findByUsername(username).map(user -> {
            onPresentInterface.onSuccess(user);
            return user;
        }).orElseThrow(()-> new ResourceNotFoundException("Nie ma takiego użytkownika!"));
    }


    public User checkUserExistByTokenAndOnSuccess(UserRepository userRepository, OnPresentInterface<User> onPresentInterface) {
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userRepository.findById(userPrincipal.getId()).map(tmpUser -> {
            onPresentInterface.onSuccess(tmpUser);
            return tmpUser;
        }).orElseThrow(()-> new ResourceNotFoundException("Nie ma takiego użytkownika!"));
    }


    public  <R extends UserSettingsRepository<T>, T> void deleteRepoValuesIfUserExists(R repository, User user, T repositoryType) {
        if (repository.existsByUser(user)) {
            repository.deleteByUser(user);
        }
    }

    public  <R extends JpaRepository<O, Long>, T, O> void updateUserTeamsOrSites(List<T> objects, R repository, O objectType, User user) {
        for (T object : objects) {
            if (objectType instanceof FavouriteTeam) {
                ((FavouriteTeam) objectType).setTeam((Team) object);
                ((FavouriteTeam) objectType).setUser(user);
            }
            if (objectType instanceof UserSite) {
                ((UserSite) objectType).setSite((Site) object);
                ((UserSite) objectType).setUser(user);
            }
            repository.save(objectType);
        }
    }

}
