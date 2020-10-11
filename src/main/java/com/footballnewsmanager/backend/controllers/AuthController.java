package com.footballnewsmanager.backend.controllers;


import com.footballnewsmanager.backend.api.request.auth.LoginRequest;
import com.footballnewsmanager.backend.api.request.auth.RegisterRequest;
import com.footballnewsmanager.backend.api.request.auth.ResetPasswordRequest;
import com.footballnewsmanager.backend.api.request.auth.ValidationMessage;
import com.footballnewsmanager.backend.api.response.BaseResponse;
import com.footballnewsmanager.backend.api.response.auth.ArgumentNotValidResponse;
import com.footballnewsmanager.backend.api.response.auth.JwtTokenResponse;
import com.footballnewsmanager.backend.auth.JwtTokenProvider;
import com.footballnewsmanager.backend.exceptions.ForbiddenRequestException;
import com.footballnewsmanager.backend.exceptions.ValidationExceptionHandlers;
import com.footballnewsmanager.backend.exceptions.BadRequestException;
import com.footballnewsmanager.backend.exceptions.ResourceNotFoundException;
import com.footballnewsmanager.backend.helpers.MailSender;
import com.footballnewsmanager.backend.models.*;
import com.footballnewsmanager.backend.repositories.BlacklistTokenRepository;
import com.footballnewsmanager.backend.repositories.PasswordResetTokenRepository;
import com.footballnewsmanager.backend.repositories.RoleRepository;
import com.footballnewsmanager.backend.repositories.UserRepository;
import com.footballnewsmanager.backend.services.ResetPasswordService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.mail.internet.MimeMessage;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/auth")
@Validated
public class AuthController extends ValidationExceptionHandlers {


    private final JavaMailSender javaMailSender;
    private final AuthenticationManager authenticationManager;
    private final BlacklistTokenRepository blacklistTokenRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;
    private final ResetPasswordService resetPasswordService;
    private final PasswordResetTokenRepository passwordResetTokenRepository;


    public AuthController(JavaMailSender javaMailSender, AuthenticationManager authenticationManager, BlacklistTokenRepository blacklistTokenRepository,
                          UserRepository userRepository, RoleRepository roleRepository,
                          PasswordEncoder passwordEncoder, JwtTokenProvider tokenProvider, ResetPasswordService resetPasswordService, PasswordResetTokenRepository passwordResetTokenRepository) {
        this.javaMailSender = javaMailSender;
        this.authenticationManager = authenticationManager;
        this.blacklistTokenRepository = blacklistTokenRepository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
        this.resetPasswordService = resetPasswordService;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) throws Exception{
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
            Map<String, String> errors = new HashMap<>();
            if (userRepository.existsByUsername(registerRequest.getUsername())) {
                errors.put("username" , "Podana nazwa użytkownika już istnieje");
            }
            if (userRepository.existsByEmail(registerRequest.getEmail())) {
                errors.put("email" , "Na podany adres email jest już założone konto");
            }
            return ResponseEntity.badRequest().body(new ArgumentNotValidResponse(LocalDateTime.now(),
                    HttpStatus.BAD_REQUEST.value(), "Bad Credentials Error", errors));
        }

        User user = new User(registerRequest.getUsername(), registerRequest.getEmail(), registerRequest.getPassword());

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        Role role = roleRepository.findByName(RoleName.USER).orElseGet(()->{
            Role createdRole = new Role(RoleName.USER);
            roleRepository.save(createdRole);
            return createdRole;
        });
        user.setRoles(Collections.singleton(role));
        User result = userRepository.save(user);

        //zobaczyć co to jest
        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/users/{username}")
                .buildAndExpand(result.getUsername()).toUri();

        return ResponseEntity.created(location).body(new BaseResponse(true, "Pomyślnie zarejestrowano"));

    }

    //authority user
    //w configu zmienić dostęp
    @PutMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") @Size(min= 7, max = 512) String jwtToken) {
            BlackListToken blackListToken = new BlackListToken();
            blackListToken.setToken(jwtToken.substring(7));
            blacklistTokenRepository.save(blackListToken);
            return ResponseEntity.ok(new BaseResponse(true, "Poprawnie wylogowano"));
    }

    //authority user
    @GetMapping("/isLoggedIn")
    public ResponseEntity<?> isLoggedIn(@RequestHeader("Authorization") @Size(min = 7, max = 512) String jwttoken) {
        if (tokenProvider.validateToken(jwttoken.substring(7)))
            return ResponseEntity.ok(new BaseResponse(true, "Jesteś zalogowany"));
        else
            throw new ForbiddenRequestException("Nie jesteś zalogowany, nie masz praw dostępu do Aplikacji");
    }

    @PostMapping("sendResetPassToken/{email}")
    public ResponseEntity<BaseResponse> resetPasswordSendTokenToMail(@PathVariable("email") @NotBlank(message = ValidationMessage.EMAIL_NOT_BLANK) @Size(max = 40, message = ValidationMessage.EMAIL_SIZE) @Email(message = ValidationMessage.EMAIL_VALID) String email){
        Optional<User> userOptional = userRepository.findByEmail(email);
        if(userOptional.isPresent()){
            User user = userOptional.get();
            if(!passwordResetTokenRepository.existsByUserAndExpiryDateGreaterThan(userOptional.get(), Calendar.getInstance().getTime())){
                String token = UUID.randomUUID().toString();
                PasswordResetToken passwordResetToken = new PasswordResetToken();
                passwordResetToken.setToken(token);
                passwordResetToken.setUser(user);
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.DATE, 1);
                passwordResetToken.setExpiryDate(calendar.getTime());
                passwordResetTokenRepository.save(passwordResetToken);
                MimeMessage mailMessage= MailSender.createResetPassMail("Reset hasła",token, email, javaMailSender);
                javaMailSender.send(mailMessage);
                return ResponseEntity.ok(new BaseResponse(true, "wysłano mail z tokenem aktywacyjnym"));
            }
            else{
                throw new BadRequestException("token został już wygenerowany");
            }
        }else{
            throw new ResourceNotFoundException("Nie znaleziono konta na podany adres mailowy!");
        }
    }

    @PostMapping("resetPassword")
    @Transactional
    public ResponseEntity<BaseResponse> resetPassword(@Valid @RequestBody ResetPasswordRequest resetPasswordRequest)
    {
        String result = resetPasswordService.validateToken(resetPasswordRequest.getToken(), passwordResetTokenRepository);
        if(result.equals("Ok")){
            Optional<PasswordResetToken> passwordResetToken = passwordResetTokenRepository.findByToken(resetPasswordRequest.getToken());
            if(passwordResetToken.isPresent()){
                User user = passwordResetToken.get().getUser();
                String pass = resetPasswordRequest.getPassword();
                user.setPassword(passwordEncoder.encode(pass));
                userRepository.save(user);
                passwordResetTokenRepository.delete(passwordResetToken.get());
            }
        }
        else{
            throw new BadRequestException(result);
        }
        return ResponseEntity.ok(new BaseResponse(true, "Zaktualizowano hasło"));
    }



}
