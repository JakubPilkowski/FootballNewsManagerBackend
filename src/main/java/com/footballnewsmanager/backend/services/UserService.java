package com.footballnewsmanager.backend.services;

import com.footballnewsmanager.backend.auth.UserPrincipal;
import com.footballnewsmanager.backend.controllers.OnPresentInterface;
import com.footballnewsmanager.backend.exceptions.ResourceNotFoundException;
import com.footballnewsmanager.backend.models.User;
import com.footballnewsmanager.backend.repositories.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

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

}
