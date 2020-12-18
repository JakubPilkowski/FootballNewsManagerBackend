package com.footballnewsmanager.backend.start_up;

import com.footballnewsmanager.backend.models.Role;
import com.footballnewsmanager.backend.models.User;
import com.footballnewsmanager.backend.repositories.RoleRepository;
import com.footballnewsmanager.backend.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Component
public class AdminAccInitialization implements CommandLineRunner {


    @Value("${app.adminName}")
    private String username;

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
    public void run(String... args) {
        if (!userRepository.existsByUsernameOrEmail(username, email)) {
            User user = new User(username, email, password);

            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
            LocalDateTime localDateTime = LocalDateTime.now();
            String format = dateTimeFormatter.format(localDateTime);
            user.setAddedDate(LocalDateTime.parse(format, dateTimeFormatter));
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            List<Role> roles = roleRepository.findAll();
            Set<Role> roleSet = new HashSet<>(roles);
            user.setRoles(roleSet);
            userRepository.save(user);
        }
    }
}
