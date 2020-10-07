package com.footballnewsmanager.backend.controllers;


import com.footballnewsmanager.backend.api.response.BaseResponse;
import com.footballnewsmanager.backend.repositories.UserRepository;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    //    @GetMapping Mapping("/{username}")
    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable("id") long id) {
        try {
            userRepository.deleteById(id);
            return ResponseEntity.ok(new BaseResponse(true, "Usunięto pomyślnie użytkownika"));
        } catch (EmptyResultDataAccessException exception) {
            return ResponseEntity.badRequest().body(new BaseResponse(false, "Dla podanego id nie ma użytkownika"));
        }
    }

}
