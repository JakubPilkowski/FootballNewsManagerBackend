package com.footballnewsmanager.backend.start_up;

import com.footballnewsmanager.backend.helpers.LeaguesHelper;
import com.footballnewsmanager.backend.models.Role;
import com.footballnewsmanager.backend.models.RoleName;
import com.footballnewsmanager.backend.models.User;
import com.footballnewsmanager.backend.repositories.RoleRepository;
import com.footballnewsmanager.backend.repositories.TeamRepository;
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

    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    public AdminAccInitialization(PasswordEncoder passwordEncoder, RoleRepository roleRepository, UserRepository userRepository) {
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) {
        if (!userRepository.existsByUsernameOrEmail(username, email)) {
            List<User> users = new ArrayList<>();
            users.add(createSuperAdmin());
            users.add(createTestUser());
            userRepository.saveAll(users);
        }
    }

    public User createSuperAdmin(){
        User user = new User(username, email, password);
        LocalDateTime localDateTime = LocalDateTime.now();
        user.setAddedDate(localDateTime);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        List<Role> roles = roleRepository.findAll();
        Set<Role> roleSet = new HashSet<>(roles);
        user.setRoles(roleSet);
        return user;
    }
    public User createTestUser(){
        User user = new User("tester", "tester@gmail.com", "retsetfnm");
        LocalDateTime localDateTime = LocalDateTime.now();
        user.setAddedDate(localDateTime);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        Optional<Role> role = roleRepository.findByName(RoleName.USER);
        role.ifPresent(value -> user.setRoles(Collections.singleton(value)));
        return user;
    }

}
