package com.footballnewsmanager.backend.controllers;


import com.footballnewsmanager.backend.api.request.auth.LoginRequest;
import com.footballnewsmanager.backend.api.request.auth.RegisterRequest;
import com.footballnewsmanager.backend.api.request.auth.ResetPasswordRequest;
import com.footballnewsmanager.backend.api.response.BaseResponse;
import com.footballnewsmanager.backend.api.response.auth.BadCredentialsResponse;
import com.footballnewsmanager.backend.api.response.auth.JwtTokenResponse;
import com.footballnewsmanager.backend.api.response.auth.UserError;
import com.footballnewsmanager.backend.auth.JwtTokenProvider;
import com.footballnewsmanager.backend.exceptions.ResourceNotFoundException;
import com.footballnewsmanager.backend.helpers.MailSender;
import com.footballnewsmanager.backend.models.*;
import com.footballnewsmanager.backend.repositories.BlacklistTokenRepository;
import com.footballnewsmanager.backend.repositories.PasswordResetTokenRepository;
import com.footballnewsmanager.backend.repositories.RoleRepository;
import com.footballnewsmanager.backend.repositories.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.*;

@RestController
@RequestMapping("/auth")
public class AuthController {


    private final JavaMailSender javaMailSender;
    private final AuthenticationManager authenticationManager;
    private final BlacklistTokenRepository blacklistTokenRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;
    private final PasswordResetTokenRepository passwordResetTokenRepository;


    public AuthController(JavaMailSender javaMailSender, AuthenticationManager authenticationManager, BlacklistTokenRepository blacklistTokenRepository,
                          UserRepository userRepository, RoleRepository roleRepository,
                          PasswordEncoder passwordEncoder, JwtTokenProvider tokenProvider, PasswordResetTokenRepository passwordResetTokenRepository) {
        this.javaMailSender = javaMailSender;
        this.authenticationManager = authenticationManager;
        this.blacklistTokenRepository = blacklistTokenRepository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
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
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
        if (userRepository.existsByUsernameOrEmail(registerRequest.getUsername(), registerRequest.getEmail())) {
            List<UserError> errors = new ArrayList<>();
            if (userRepository.existsByUsername(registerRequest.getUsername())) {
                UserError usernameError = new UserError("Podana nazwa użytkownika już istnieje", "username");
                errors.add(usernameError);
            }
            if (userRepository.existsByEmail(registerRequest.getEmail())) {
                UserError usernameError = new UserError("Na podany adres email jest już założone konto", "email");
                errors.add(usernameError);
            }
            return ResponseEntity.badRequest().body(new BadCredentialsResponse(false, "Błąd", errors));
        }

        User user = new User(registerRequest.getUsername(), registerRequest.getEmail(), registerRequest.getPassword());

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

    @PostMapping("resetPasswordSendTokenToMail/{email}")
    public ResponseEntity<BaseResponse> resetPasswordSendTokenToMail(@PathVariable("email") String email){
        Optional<User> userOptional = userRepository.findByEmail(email);
        if(userOptional.isPresent()){
            User user = userOptional.get();
            String token = UUID.randomUUID().toString();
            PasswordResetToken passwordResetToken = new PasswordResetToken();
            passwordResetToken.setToken(token);
            passwordResetToken.setUser(user);
            passwordResetTokenRepository.save(passwordResetToken);
            String text = "Wygenerowany token: \n" + token + "\n Token jest ważny przez 24 godziny. \n Wiadomość wygenerowana. Prosimy na nią nie odpowiadać";
            SimpleMailMessage mailMessage= MailSender.createMail("Reset hasła", text, email);
            javaMailSender.send(mailMessage);
            return ResponseEntity.ok(new BaseResponse(true, "wysłano mail z tokenem aktywacyjnym"));
        }else{
            throw new ResourceNotFoundException("Nie znaleziono konta na podany adres mailowy!");
        }
    }

    @PostMapping("resetPassword")
    public ResponseEntity<BaseResponse> resetPassword(@RequestBody ResetPasswordRequest resetPasswordRequest)
    {

        return ResponseEntity.ok(new BaseResponse(true, "Zaktualizowano hasło"));
    }
}
