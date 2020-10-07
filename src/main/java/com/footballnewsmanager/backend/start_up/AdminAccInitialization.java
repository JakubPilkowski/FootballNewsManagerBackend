package com.footballnewsmanager.backend.start_up;

import com.footballnewsmanager.backend.models.Role;
import com.footballnewsmanager.backend.models.RoleName;
import com.footballnewsmanager.backend.models.User;
import com.footballnewsmanager.backend.repositories.RoleRepository;
import com.footballnewsmanager.backend.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Optional;

@Component
public class AdminAccInitialization implements CommandLineRunner {


    @Value("${app.adminName}")
    private String login;

    @Value("${app.adminEmail}")
    private String email;

    @Value("${app.adminPassword}")
    private String password;

    private PasswordEncoder passwordEncoder;
    private RoleRepository roleRepository;
    private UserRepository userRepository;

    public AdminAccInitialization(PasswordEncoder passwordEncoder, RoleRepository roleRepository, UserRepository userRepository) {
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (!userRepository.existsByUsernameOrEmail(login, email)) {
            User user = new User(login, email, password);
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            Optional<Role> userRole = roleRepository.findByName(RoleName.ADMIN);

            if (userRole.isPresent()) {
                user.setRoles(Collections.singleton(userRole.get()));
            } else {
                Role role = new Role(RoleName.ADMIN);
                roleRepository.save(role);
                user.setRoles(Collections.singleton(role));
            }
            userRepository.save(user);
        }
    }
}
