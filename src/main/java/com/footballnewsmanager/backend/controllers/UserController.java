package com.footballnewsmanager.backend.controllers;


import com.fasterxml.jackson.annotation.JsonView;
import com.footballnewsmanager.backend.api.request.user_settings.CredentialsChangeRequest;
import com.footballnewsmanager.backend.api.request.user_settings.FavouriteTeamRequest;
import com.footballnewsmanager.backend.api.request.user_settings.UserSiteRequest;
import com.footballnewsmanager.backend.api.request.user_settings.UserSettingsRequest;
import com.footballnewsmanager.backend.api.response.BaseResponse;
import com.footballnewsmanager.backend.auth.JwtTokenProvider;
import com.footballnewsmanager.backend.auth.UserPrincipal;
import com.footballnewsmanager.backend.exceptions.BadRequestException;
import com.footballnewsmanager.backend.exceptions.ResourceNotFoundException;
import com.footballnewsmanager.backend.models.*;
import com.footballnewsmanager.backend.repositories.*;
import com.footballnewsmanager.backend.views.Views;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserRepository userRepository;
    private final FavouriteTeamRepository favouriteTeamRepository;
    private final TeamRepository teamRepository;
    private final UserSiteRepository userSiteRepository;
    private final JwtTokenProvider tokenProvider;
    private final BlacklistTokenRepository blacklistTokenRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserRepository userRepository, FavouriteTeamRepository favouriteTeamRepository, TeamRepository teamRepository, UserSiteRepository userSiteRepository, JwtTokenProvider tokenProvider, BlacklistTokenRepository blacklistTokenRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.favouriteTeamRepository = favouriteTeamRepository;
        this.teamRepository = teamRepository;
        this.userSiteRepository = userSiteRepository;
        this.tokenProvider = tokenProvider;
        this.blacklistTokenRepository = blacklistTokenRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("")
    @PreAuthorize("hasAuthority('ADMIN')")
    @JsonView(Views.Internal.class)
    public List<User> users(@RequestHeader("Authorization") String jwtToken) {
        Long id = tokenProvider.getUserIdFromJWT(jwtToken.substring(7));
        return userRepository.findByIdNot(id).orElseThrow(() ->
                new ResourceNotFoundException("Nie ma użytkowników!")
        );
    }

    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    @DeleteMapping("{id}")
    public ResponseEntity<BaseResponse> deleteUser(@PathVariable("id") long id) {
        try {
            userRepository.deleteById(id);
            return ResponseEntity.ok(new BaseResponse(true, "Pomyślnie usunięto użytkownika"));
        } catch (EmptyResultDataAccessException exception) {
            throw new ResourceNotFoundException("Dla podanego id nie ma użytkownika", exception);
        }
    }

    @GetMapping("{username}")
    @PreAuthorize("hasAuthority('ADMIN')")
    @JsonView(Views.Internal.class)
    public User getUserProfile(@PathVariable("username") String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Nie ma takiego użytkownika!"));
    }


    @PutMapping("{username}/adminRole={role}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<BaseResponse> toggleAdminRole(@PathVariable("username") String username,
                                                        @PathVariable("role") boolean role) {
        AtomicReference<String> message = new AtomicReference<>("");
        checkUserExistByUsernameAndOnSuccess(username, user -> {
            Optional<Role> roleOptional = roleRepository.findByName(RoleName.ADMIN);
            if (roleOptional.isPresent()) {
                if (role) {
                    user.addToRoles(roleOptional.get());
                    message.set("Nadano prawa administracyjne!");
                } else {
                    user.removeFromRoles(roleOptional.get());
                    message.set("Usunięto prawa administracyjne!");
                }
                userRepository.save(user);
            } else {
                throw new BadRequestException("Nie ma takiej roli, coś poszło nie tak");
            }
        });
        return ResponseEntity.ok(new BaseResponse(true, message.get()));
    }


    @GetMapping("me")
    @JsonView(Views.Public.class)
    public User getMyProfile(@RequestHeader("Authorization") String jwtToken) {
        Long id = tokenProvider.getUserIdFromJWT(jwtToken.substring(7));
        return userRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Nie ma takiego użytkownika")
        );
    }

    @DeleteMapping("me")
    @Transactional
    public ResponseEntity<BaseResponse> deleteMyAccount(@RequestHeader("Authorization") String jwtToken) {
        Long id = tokenProvider.getUserIdFromJWT(jwtToken.substring(7));
        try {
            userRepository.deleteById(id);
            BlackListToken blackListToken = new BlackListToken();
            blackListToken.setToken(jwtToken.substring(7));
            blacklistTokenRepository.save(blackListToken);
            return ResponseEntity.ok(new BaseResponse(true, "Pomyślnie usunięto użytkownika"));
        } catch (EmptyResultDataAccessException exception) {
            throw new ResourceNotFoundException("Dla podanego id nie ma użytkownika", exception);
        }
    }

    @PutMapping("me")
    @Transactional
    @JsonView(Views.Public.class)
    public User changeUserSettings(@RequestHeader("Authorization") String jwtToken, @RequestBody UserSettingsRequest userSettingsRequest) {
        AtomicReference<User> newUser = new AtomicReference<>();
        checkUserExistByTokenAndOnSuccess(jwtToken, (user -> {
            checkIfExists(favouriteTeamRepository, user, new FavouriteTeam());
            checkIfExists(userSiteRepository, user, new UserSite());
            updateUserTeamsOrSites(userSettingsRequest.getFavouriteTeams(), favouriteTeamRepository, new FavouriteTeam(), user);
            updateUserTeamsOrSites(userSettingsRequest.getChosenSites(), userSiteRepository, new UserSite(), user);
            user.setNotification(userSettingsRequest.isNotifications());
            user.setDarkMode(userSettingsRequest.isDarkMode());
            user.setLanguage(userSettingsRequest.getLanguage());
            user.setProposedNews(userSettingsRequest.isProposedNews());
            userRepository.save(user);
            newUser.set(user);
        }));
        return newUser.get();
    }

    @PostMapping("me/email")
    public ResponseEntity<BaseResponse> changeEmail(@RequestHeader("Authorization") String jwtToken, @RequestBody CredentialsChangeRequest credentialsChangeRequest) {
        AtomicReference<BaseResponse> baseResponse = new AtomicReference<>();
        checkUserExistByTokenAndOnSuccess(jwtToken, user -> {
            if (credentialsChangeRequest.getOldCredential().equals(user.getEmail())) {
                if (!userRepository.existsByEmail(credentialsChangeRequest.getNewCredential())) {
                    user.setEmail(credentialsChangeRequest.getNewCredential());
                    userRepository.save(user);
                    baseResponse.set(new BaseResponse(true, "Poprawnie zmieniono adres mailowy!"));
                } else {
                    throw new BadRequestException("Na podany adres email jest już utworzone konto!");
                }
            } else {
                throw new BadRequestException("Podany adres email jest nieprawidłowy!");
            }
        });
        return ResponseEntity.ok(baseResponse.get());
    }

    @PostMapping("me/username")
    public ResponseEntity<BaseResponse> changeUsername(@RequestHeader("Authorization") String jwtToken, @RequestBody CredentialsChangeRequest credentialsChangeRequest) {
        AtomicReference<BaseResponse> baseResponse = new AtomicReference<>();
        checkUserExistByTokenAndOnSuccess(jwtToken, user -> {
            if (credentialsChangeRequest.getOldCredential().equals(user.getUsername())) {
                if (!userRepository.existsByUsername(credentialsChangeRequest.getNewCredential())) {
                    user.setUsername(credentialsChangeRequest.getNewCredential());
                    userRepository.save(user);
                    baseResponse.set(new BaseResponse(true, "Poprawnie zmieniono nazwę użytkownika!"));
                } else {
                    throw new BadRequestException("Podana nazwa użytkownika jest już zajęta!");
                }
            } else {
                throw new BadRequestException("Nieprawidłowa nazwa użytkownika!");
            }
        });
        return ResponseEntity.ok(baseResponse.get());
    }


    @PostMapping("me/password")
    public ResponseEntity<BaseResponse> changePassword(@RequestBody CredentialsChangeRequest credentialsChangeRequest) {

        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (passwordEncoder.matches(credentialsChangeRequest.getOldCredential(), userPrincipal.getPassword()))
        {
            Optional<User> user = userRepository.findById(userPrincipal.getId());
            if(user.isPresent()){
                String newPassword = passwordEncoder.encode(credentialsChangeRequest.getNewCredential());
                user.get().setPassword(newPassword);
                userRepository.save(user.get());
            }
            return ResponseEntity.ok(new BaseResponse(true, "Poprawnie zmieniono hasło!"));
        }
        else{
            throw new BadRequestException("Podano błędne hasło");
        }
    }



    @PutMapping("me/notification={notification}")
    public ResponseEntity<BaseResponse> toggleNotifications(@RequestHeader("Authorization") String jwtToken, @PathVariable("notification") boolean notification) {
        AtomicReference<BaseResponse> baseResponse = new AtomicReference<>();
        checkUserExistByTokenAndOnSuccess(jwtToken, (user) -> {
            user.setNotification(notification);
            userRepository.save(user);
            baseResponse.set(new BaseResponse(true, "Zmiana ustawienia powiadomień"));
        });
        return ResponseEntity.ok(baseResponse.get());
    }

    @PutMapping("me/proposedNews={proposedNews}")
    public ResponseEntity<BaseResponse> toggleProposedNews(@RequestHeader("Authorization") String jwtToken, @PathVariable("proposedNews") boolean proposedNews) {
        AtomicReference<BaseResponse> baseResponse = new AtomicReference<>();
        checkUserExistByTokenAndOnSuccess(jwtToken, (user) -> {
            user.setProposedNews(proposedNews);
            userRepository.save(user);
            baseResponse.set(new BaseResponse(true, "Zmiana ustawienia polecanych newsów"));
        });
        return ResponseEntity.ok(baseResponse.get());
    }

    @PutMapping("me/language={language}")
    public ResponseEntity<BaseResponse> changeLanguage(@RequestHeader("Authorization") String jwtToken, @PathVariable("language") Language language) {
        AtomicReference<BaseResponse> baseResponse = new AtomicReference<>();
        checkUserExistByTokenAndOnSuccess(jwtToken, (user) -> {
            user.setLanguage(language);
            userRepository.save(user);
            baseResponse.set(new BaseResponse(true, "Zmiana języka"));
        });
        return ResponseEntity.ok(baseResponse.get());
    }

    @PutMapping("me/darkMode={darkMode}")
    public ResponseEntity<BaseResponse> toggleDarkMode(@RequestHeader("Authorization") String jwtToken, @PathVariable("darkMode") boolean darkMode) {
        AtomicReference<BaseResponse> baseResponse = new AtomicReference<>();
        checkUserExistByTokenAndOnSuccess(jwtToken, (user) -> {
            user.setDarkMode(darkMode);
            userRepository.save(user);
            baseResponse.set(new BaseResponse(true, "Zmiana motywu"));
        });
        return ResponseEntity.ok(baseResponse.get());
    }

    @PutMapping("me/addTeam")
    @JsonView(Views.Public.class)
    public User addTeam(@RequestHeader("Authorization") String jwtToken, @RequestBody FavouriteTeamRequest teamRequest) {
        AtomicReference<User> newUser = new AtomicReference<>();
        checkUserExistByTokenAndOnSuccess(jwtToken, (user) -> {
            if (!favouriteTeamRepository.findByUserAndTeam(user, teamRequest.getTeam()).isPresent()) {
                FavouriteTeam favouriteTeam = new FavouriteTeam();
                favouriteTeam.setTeam(teamRequest.getTeam());
                favouriteTeam.setUser(user);
                favouriteTeamRepository.save(favouriteTeam);
                newUser.set(user);
            } else {
                throw new BadRequestException("Podana drużyna jest już dodana!");
            }
        });
        return newUser.get();
    }

    @DeleteMapping("me/removeTeam")
    @JsonView(Views.Public.class)
    @Transactional
    public User removeTeam(@RequestHeader("Authorization") String jwtToken, @RequestBody FavouriteTeamRequest favouriteTeamRequest) {
        AtomicReference<User> newUser = new AtomicReference<>();
        checkUserExistByTokenAndOnSuccess(jwtToken, (user) -> {
            try {
                favouriteTeamRepository.deleteByUserAndTeam(user, favouriteTeamRequest.getTeam());
                newUser.set(user);
            } catch (EmptyResultDataAccessException exception) {
                throw new ResourceNotFoundException("Podany użytkownik nie ma polubionej danej drużyny!", exception);
            }
        });
        return newUser.get();
    }

    @PutMapping("me/addSite")
    @JsonView(Views.Public.class)
    public User addSite(@RequestHeader("Authorization") String jwtToken, @RequestBody UserSiteRequest userSiteRequest) {
        AtomicReference<User> newUser = new AtomicReference<>();
        checkUserExistByTokenAndOnSuccess(jwtToken, (user) -> {
            if (!userSiteRepository.findByUserAndSite(user, userSiteRequest.getSite()).isPresent()) {
                UserSite userSite = new UserSite();
                userSite.setSite(userSiteRequest.getSite());
                userSite.setUser(user);
                userSiteRepository.save(userSite);
                newUser.set(user);
            } else {
                throw new BadRequestException("Podana strona jest już dodana!");
            }
        });
        return newUser.get();
    }


    @DeleteMapping("me/removeSite")
    @JsonView(Views.Public.class)
    @Transactional
    public User removeSite(@RequestHeader("Authorization") String jwtToken, @RequestBody UserSiteRequest userSiteRequest) {
        AtomicReference<User> newUser = new AtomicReference<>();
        checkUserExistByTokenAndOnSuccess(jwtToken, (user) -> {
            try {
                userSiteRepository.deleteByUserAndSite(user, userSiteRequest.getSite());
                newUser.set(user);
            } catch (EmptyResultDataAccessException exception) {
                throw new ResourceNotFoundException("Podany użytkownik nie ma podanej strony!", exception);
            }
        });
        return newUser.get();
    }


    private void checkUserExistByUsernameAndOnSuccess(String username, OnPresentInterface<User> onPresentInterface) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isPresent()) {
            onPresentInterface.onSuccess(userOptional.get());
        } else {
            throw new ResourceNotFoundException("Nie ma takiego użytkownika!");
        }
    }


    private void checkUserExistByTokenAndOnSuccess(String token, OnPresentInterface<User> onPresentInterface) {
        Long id = tokenProvider.getUserIdFromJWT(token.substring(7));
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isPresent()) {
            onPresentInterface.onSuccess(userOptional.get());
        } else {
            throw new ResourceNotFoundException("Nie ma takiego użytkownika!");
        }
    }


    private <R extends UserSettingsRepository<T>, T> void checkIfExists(R repository, User user, T repositoryType) {
        if (repository.findByUser(user).isPresent()) {
            repository.deleteByUser(user);
        }
    }

    private <R extends JpaRepository<O, Long>, T, O> void updateUserTeamsOrSites(List<T> objects, R repository, O objectType, User user) {
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
