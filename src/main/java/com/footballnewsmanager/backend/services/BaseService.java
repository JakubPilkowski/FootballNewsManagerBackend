package com.footballnewsmanager.backend.services;

import com.footballnewsmanager.backend.controllers.OnPresentInterface;
import com.footballnewsmanager.backend.exceptions.ResourceNotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
public class BaseService {

    public  <T, R extends JpaRepository<T, Long>> T checkExistByIdAndOnSuccess(Long id, T type, R repository, String message, OnPresentInterface<T> onPresentInterface) {
        return repository.findById(id).map(result -> {
            onPresentInterface.onSuccess(result);
            return result;
        }).orElseThrow(() -> new ResourceNotFoundException(message));
    }
}
