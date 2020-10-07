package com.footballnewsmanager.backend.controllers;


import com.footballnewsmanager.backend.api.request.login.LoginRequest;
import com.footballnewsmanager.backend.api.request.register.RegisterRequest;
import com.footballnewsmanager.backend.api.response.BaseResponse;
import com.footballnewsmanager.backend.api.response.auth.BadCredentialsResponse;
import com.footballnewsmanager.backend.api.response.auth.JwtTokenResponse;
import com.footballnewsmanager.backend.api.response.auth.UserError;
import com.footballnewsmanager.backend.auth.JwtTokenProvider;
import com.footballnewsmanager.backend.models.BlackListToken;
import com.footballnewsmanager.backend.models.Role;
import com.footballnewsmanager.backend.models.RoleName;
import com.footballnewsmanager.backend.models.User;
import com.footballnewsmanager.backend.repositories.BlacklistTokenRepository;
import com.footballnewsmanager.backend.repositories.RoleRepository;
import com.footballnewsmanager.backend.repositories.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {

    AuthenticationManager authenticationManager;

    BlacklistTokenRepository blacklistTokenRepository;

    UserRepository userRepository;

    RoleRepository roleRepository;

    PasswordEncoder passwordEncoder;

    JwtTokenProvider tokenProvider;

    public AuthController(AuthenticationManager authenticationManager, BlacklistTokenRepository blacklistTokenRepository,
                          UserRepository userRepository, RoleRepository roleRepository,
                          PasswordEncoder passwordEncoder, JwtTokenProvider tokenProvider) {
        this.authenticationManager = authenticationManager;
        this.blacklistTokenRepository = blacklistTokenRepository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest){
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsernameOrEmail(),
                        loginRequest.getPassword()
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(authentication);

        JwtTokenResponse jwtTokenResponse = new JwtTokenResponse(true, "Poprawnie zalogowano", jwt);
        return ResponseEntity.ok(jwtTokenResponse);
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest registerRequest){
        if(userRepository.existsByUsernameOrEmail(registerRequest.getUsername(), registerRequest.getEmail())){
            List<UserError> errors = new ArrayList<>();
            if(userRepository.existsByUsername(registerRequest.getUsername())){
                UserError usernameError = new UserError("Podana nazwa użytkownika już istnieje", "username");
                errors.add(usernameError);
            }
            if(userRepository.existsByEmail(registerRequest.getEmail())){
                UserError usernameError = new UserError("Na podany adres email jest już założone konto", "email");
                errors.add(usernameError);
            }
            return ResponseEntity.badRequest().body(new BadCredentialsResponse(false, "Błąd", errors));
        }

        User user = new User(registerRequest.getUsername(),registerRequest.getEmail(), registerRequest.getPassword());

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        Optional<Role> userRole = roleRepository.findByName(RoleName.USER);

        if (userRole.isPresent()) {
            user.setRoles(Collections.singleton(userRole.get()));
        } else {
            roleRepository.save(new Role(RoleName.USER));
        }

        User result = userRepository.save(user);

        //zobaczyć co to jest
        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/users/{username}")
                .buildAndExpand(result.getUsername()).toUri();

        return ResponseEntity.created(location).body(new BaseResponse(true, "Pomyślnie zarejestrowano"));

    }

//    @PreAuthorize("hasRole('USER')")
    @DeleteMapping("/deleteUser/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable("id") long id) {
        userRepository.deleteById(id);
        return ResponseEntity.ok(new BaseResponse(true, "Usunięto pomyślnie użytkownika"));
    }

    @PutMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String jwtToken) {
        BlackListToken blackListToken = new BlackListToken();
        blackListToken.setToken(jwtToken.substring(7));
        blacklistTokenRepository.save(blackListToken);
        return ResponseEntity.ok(new BaseResponse(true, "Poprawnie wylogowano"));
    }

    @GetMapping("/isLoggedIn")
    public ResponseEntity<?> isLoggedIn(@RequestHeader("Authorization") String jwttoken) {
        if (tokenProvider.validateToken(jwttoken.substring(7)))
            return ResponseEntity.ok(new BaseResponse(true, "Jesteś zalogowany"));
        else
            return ResponseEntity.ok(new BaseResponse(false, "Nie jesteś zalogowany"));
    }


}
