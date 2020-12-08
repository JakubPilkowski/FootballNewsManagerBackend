package com.footballnewsmanager.backend.controllers;


import com.fasterxml.jackson.annotation.JsonView;
import com.footballnewsmanager.backend.api.request.auth.ValidationMessage;
import com.footballnewsmanager.backend.api.request.user_settings.*;
import com.footballnewsmanager.backend.api.response.BaseResponse;
import com.footballnewsmanager.backend.auth.JwtTokenProvider;
import com.footballnewsmanager.backend.auth.UserPrincipal;
import com.footballnewsmanager.backend.exceptions.BadRequestException;
import com.footballnewsmanager.backend.exceptions.ResourceNotFoundException;
import com.footballnewsmanager.backend.models.*;
import com.footballnewsmanager.backend.repositories.*;
import com.footballnewsmanager.backend.services.UserService;
import com.footballnewsmanager.backend.validators.EnumNamePattern;
import com.footballnewsmanager.backend.views.Views;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@RestController
@RequestMapping("/users")
@Validated
public class UserController {

    @Value("${app.adminName}")
    private String username;

    private final UserRepository userRepository;
    private final FavouriteTeamRepository favouriteTeamRepository;
    private final UserSiteRepository userSiteRepository;
    private final BlacklistTokenRepository blacklistTokenRepository;
    private final RoleRepository roleRepository;
    private final TeamRepository teamRepository;
    private final SiteRepository siteRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;

    public UserController(UserRepository userRepository, FavouriteTeamRepository favouriteTeamRepository, TeamRepository teamRepository, UserSiteRepository userSiteRepository, JwtTokenProvider tokenProvider, BlacklistTokenRepository blacklistTokenRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder, UserService userService, SiteRepository siteRepository) {
        this.userRepository = userRepository;
        this.favouriteTeamRepository = favouriteTeamRepository;
        this.userSiteRepository = userSiteRepository;
        this.blacklistTokenRepository = blacklistTokenRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
        this.teamRepository = teamRepository;
        this.siteRepository = siteRepository;
    }

    @GetMapping("")
    @PreAuthorize("hasAuthority('ADMIN')")
    @JsonView(Views.Internal.class)
    public List<User> users() {
        Long id = ((UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
        return userRepository.findByIdNotAndUsernameNot(id, username).orElseThrow(() ->
                new ResourceNotFoundException("Nie ma użytkowników!")
        );
    }

    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    @DeleteMapping("{id}")
    public ResponseEntity<BaseResponse> deleteUser(@PathVariable("id")
                                                   @Min(value = 0, message = ValidationMessage.ID_LESS_THAN_ZERO)
                                                           long id) {
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
    public User getUserProfile(@PathVariable("username")
                               @NotBlank(message = ValidationMessage.USERNAME_NOT_BLANK)
                               @Size(min = 4, max = 20, message = ValidationMessage.USERNAME_SIZE)
                                       String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Nie ma takiego użytkownika!"));
    }


    @PutMapping("{username}/adminRole={role}")
    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    public ResponseEntity<BaseResponse> toggleAdminRole(@PathVariable("username")
                                                        @NotBlank(message = ValidationMessage.USERNAME_NOT_BLANK)
                                                        @Size(min = 4, max = 20, message = ValidationMessage.USERNAME_SIZE)
                                                                String username,
                                                        @PathVariable("role") @NotNull(message = ValidationMessage.ROLE_NOT_BLANK) Boolean role) {
        AtomicReference<String> message = new AtomicReference<>("");
        User user = userService.checkUserExistByUsernameAndOnSuccess(username, userRepository, userFromDB -> {
            Role adminRole = roleRepository.findByName(RoleName.ADMIN).orElseThrow(() -> new BadRequestException("Nie ma takiej roli użytkownika"));
            if (role) {
                userFromDB.addToRoles(adminRole);
                message.set("Nadano prawa administracyjne!");
            } else {
                userFromDB.removeFromRoles(adminRole);
                message.set("Usunięto prawa administracyjne!");
            }
            return userFromDB;
        });
        userRepository.save(user);
        return ResponseEntity.ok(new BaseResponse(true, message.get()));
    }


    @GetMapping("me")
    @JsonView(Views.Public.class)
    public User getMyProfile() {
        Long id = ((UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
        return userRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Nie ma takiego użytkownika")
        );
    }

    @DeleteMapping("me")
    @Transactional
    public ResponseEntity<BaseResponse> deleteMyAccount(@RequestHeader("Authorization") String jwtToken) {
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        try {
            userRepository.deleteById(userPrincipal.getId());
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
    public User changeUserSettings(@Valid @RequestBody UserSettingsRequest userSettingsRequest) {
        User user = userService.checkUserExistByTokenAndOnSuccess(userRepository, userFromDB -> {
            userService.deleteRepoValuesIfUserExists(favouriteTeamRepository, userFromDB, new FavouriteTeam());
            userService.deleteRepoValuesIfUserExists(userSiteRepository, userFromDB, new UserSite());
            userService.updateUserTeamsOrSites(userSettingsRequest.getFavouriteTeams(), favouriteTeamRepository, new FavouriteTeam(), userFromDB);
            userService.updateUserTeamsOrSites(userSettingsRequest.getChosenSites(), userSiteRepository, new UserSite(), userFromDB);
            userFromDB.setNotification(userSettingsRequest.isNotifications());
            userFromDB.setDarkMode(userSettingsRequest.isDarkMode());
            userFromDB.setLanguage(userSettingsRequest.getLanguage());
            userFromDB.setProposedNews(userSettingsRequest.isProposedNews());
            return userFromDB;
        });
        userRepository.save(user);
        return user;
    }

    @PostMapping("me/email")
    public ResponseEntity<BaseResponse> changeEmail(@Valid @RequestBody EmailChangeRequest emailChangeRequest) {
        User user = userService.checkUserExistByTokenAndOnSuccess(userRepository, userFromDB -> {
            if (emailChangeRequest.getOldCredential().equals(userFromDB.getEmail())) {
                if (!userRepository.existsByEmail(emailChangeRequest.getNewCredential())) {
                    userFromDB.setEmail(emailChangeRequest.getNewCredential());
                    return userFromDB;
                } else throw new BadRequestException("Na podany adres email jest już utworzone konto!");
            } else throw new BadRequestException("Podany adres email jest nieprawidłowy!");
        });
        userRepository.save(user);
        return ResponseEntity.ok(new BaseResponse(true, "Poprawnie zmieniono adres mailowy!"));
    }

    @PostMapping("me/username")
    public ResponseEntity<BaseResponse> changeUsername(@Valid @RequestBody UsernameChangeRequest usernameChangeRequest) {
        User user = userService.checkUserExistByTokenAndOnSuccess(userRepository, userFromDB -> {
            if (usernameChangeRequest.getOldCredential().equals(userFromDB.getUsername())) {
                if (!userRepository.existsByUsername(usernameChangeRequest.getNewCredential())) {
                    userFromDB.setUsername(usernameChangeRequest.getNewCredential());
                    return userFromDB;
                } else throw new BadRequestException("Podana nazwa użytkownika jest już zajęta!");
            } else throw new BadRequestException("Nieprawidłowa nazwa użytkownika!");
        });
        userRepository.save(user);
        return ResponseEntity.ok(new BaseResponse(true, "Poprawnie zmieniono nazwę użytkownika!"));
    }


    @PostMapping("me/password")
    public ResponseEntity<BaseResponse> changePassword(@Valid @RequestBody PasswordChangeRequest passwordChangeRequest) {
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (passwordEncoder.matches(passwordChangeRequest.getOldCredential(), userPrincipal.getPassword())) {
            User user = userRepository.findById(userPrincipal.getId()).map(userFromDb -> {
                String newPassword = passwordEncoder.encode(passwordChangeRequest.getNewCredential());
                userFromDb.setPassword(newPassword);
                return userFromDb;
            }).orElseThrow(() -> new ResourceNotFoundException("Nie ma takiego użytkownika!"));
            userRepository.save(user);
            return ResponseEntity.ok(new BaseResponse(true, "Poprawnie zmieniono hasło!"));
        } else {
            throw new BadRequestException("Podano błędne hasło");
        }
    }


    @PutMapping("me/notification={notification}")
    public ResponseEntity<BaseResponse> toggleNotifications(@PathVariable("notification")
                                                            @NotNull(message = ValidationMessage.NOTIFICATION_NOT_BLANK)
                                                                    Boolean notification) {
        userService.checkUserExistByTokenAndOnSuccess(userRepository, (user) -> {
            user.setNotification(notification);
            userRepository.save(user);
            return user;
        });
        return ResponseEntity.ok(new BaseResponse(true, "Zmiana ustawienia powiadomień"));
    }

    @PutMapping("me/proposedNews={proposedNews}")
    public ResponseEntity<BaseResponse> toggleProposedNews(@PathVariable("proposedNews")
                                                           @NotNull(message = ValidationMessage.PROPOSED_NEWS_NOT_BLANK)
                                                                   Boolean proposedNews) {
        userService.checkUserExistByTokenAndOnSuccess(userRepository, (user) -> {
            user.setProposedNews(proposedNews);
            userRepository.save(user);
            return user;
        });
        return ResponseEntity.ok(new BaseResponse(true, "Zmiana ustawienia polecanych newsów"));
    }

    @PutMapping("me/language={language}")
    public ResponseEntity<BaseResponse> changeLanguage(@PathVariable("language")
                                                       @NotNull(message = ValidationMessage.LANGUAGE_NOT_BLANK)
                                                       @EnumNamePattern(regexp = "POLSKI|ANGIELSKI|WŁOSKI|FRANCUSKI|NIEMIECKI|HISZPAŃSKI",
                                                               message = ValidationMessage.LANGUAGE_INVALID) Language language) {
        userService.checkUserExistByTokenAndOnSuccess(userRepository, (user) -> {
            user.setLanguage(language);
            userRepository.save(user);
            return user;
        });
        return ResponseEntity.ok(new BaseResponse(true, "Zmiana języka"));
    }

    @PutMapping("me/darkMode={darkMode}")
    public ResponseEntity<BaseResponse> toggleDarkMode(@PathVariable("darkMode")
                                                       @NotNull(message = ValidationMessage.DARK_MODE_NOT_BLANK)
                                                               Boolean darkMode) {
        userService.checkUserExistByTokenAndOnSuccess(userRepository, (user) -> {
            user.setDarkMode(darkMode);
            userRepository.save(user);
            return user;
        });
        return ResponseEntity.ok(new BaseResponse(true, "Zmiana motywu"));
    }

    @PutMapping("me/addTeams")
    @JsonView(Views.Public.class)
    public User addTeams(@RequestBody TeamsRequest teamsRequest){
        return userService.checkUserExistByTokenAndOnSuccess(userRepository, user -> {
            for(Team teamFromRequest: teamsRequest.getTeams()){
                Team team = teamRepository.findById(teamFromRequest.getId()).orElseThrow(()-> new ResourceNotFoundException("Nie ma takiej drużyny"));
                if(!favouriteTeamRepository.existsByUserAndTeam(user,team)){
                    FavouriteTeam favouriteTeam = new FavouriteTeam();
                    favouriteTeam.setTeam(team);
                    favouriteTeam.setUser(user);
                    team.setChosenAmount(team.getChosenAmount() + 1);
                    team.measurePopularity();
                    teamRepository.save(team);
                    favouriteTeamRepository.save(favouriteTeam);
                }
            }
            return user;
        });
    }


    @PutMapping("me/addTeam/{id}")
    @JsonView(Views.Public.class)
    public User addTeam(@PathVariable("id") @NotNull @Min(value = 0) Long id) {
        return userService.checkUserExistByTokenAndOnSuccess(userRepository, (user) -> {
            Team team = teamRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Nie ma takiej drużyny"));
            if (!favouriteTeamRepository.findByUserAndTeam(user, team).isPresent()) {
                FavouriteTeam favouriteTeam = new FavouriteTeam();
                favouriteTeam.setTeam(team);
                favouriteTeam.setUser(user);
                team.setChosenAmount(team.getChosenAmount() + 1);
                team.measurePopularity();
                teamRepository.save(team);
                favouriteTeamRepository.save(favouriteTeam);
                return user;
            } else {
                throw new BadRequestException("Podana drużyna jest już dodana!");
            }
        });
    }


    @DeleteMapping("me/removeTeams")
    @JsonView(Views.Public.class)
    @Transactional
    public User removeTeams(@RequestBody TeamsRequest teamsRequest){
        return userService.checkUserExistByTokenAndOnSuccess(userRepository, user -> {
            for(Team teamFromRequest: teamsRequest.getTeams()){
                Team team = teamRepository.findById(teamFromRequest.getId()).orElseThrow(()-> new ResourceNotFoundException("Nie ma takiej drużyny"));
                if(favouriteTeamRepository.existsByUserAndTeam(user,team)){
                    try {
                        favouriteTeamRepository.deleteByUserAndTeam(user, team);
                        team.setChosenAmount(team.getChosenAmount() - 1);
                        team.measurePopularity();
                        teamRepository.save(team);
                    } catch (EmptyResultDataAccessException exception) {
                        throw new ResourceNotFoundException("Podany użytkownik nie ma polubionej danej drużyny!", exception);
                    }
                }
            }
            return user;
        });
    }

    @DeleteMapping("me/removeTeam/{id}")
    @JsonView(Views.Public.class)
    @Transactional
    public User removeTeam(@PathVariable("id") @NotNull @Min(value = 0) Long id) {
        Team team = teamRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Nie ma takiej drużyny"));
        return userService.checkUserExistByTokenAndOnSuccess(userRepository, (user) -> {
            try {
                favouriteTeamRepository.deleteByUserAndTeam(user, team);
                team.setChosenAmount(team.getChosenAmount() - 1);
                team.measurePopularity();
                teamRepository.save(team);
                return user;
            } catch (EmptyResultDataAccessException exception) {
                throw new ResourceNotFoundException("Podany użytkownik nie ma polubionej danej drużyny!", exception);
            }
        });
    }

    @PutMapping("me/addSite/{id}")
    @JsonView(Views.Public.class)
    public User addSite(@PathVariable("id") @NotNull @Min(value = 0) Long id) {
        return userService.checkUserExistByTokenAndOnSuccess(userRepository, (user) -> {
            Site site = siteRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Nie ma takiej strony!"));
            if (!userSiteRepository.findByUserAndSite(user, site).isPresent()) {
                UserSite userSite = new UserSite();
                userSite.setSite(site);
                userSite.setUser(user);
                site.setChosenAmount(site.getChosenAmount()+1);
                site.measurePopularity();
                siteRepository.save(site);
                userSiteRepository.save(userSite);
                return user;
            } else {
                throw new BadRequestException("Podana strona jest już dodana!");
            }
        });
    }


    @DeleteMapping("me/removeSite/{id}")
    @JsonView(Views.Public.class)
    @Transactional
    public User removeSite(@PathVariable("id") @NotNull @Min(value = 0) Long id) {
        return userService.checkUserExistByTokenAndOnSuccess(userRepository, (user) -> {
            try {
                Site site = siteRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Nie ma takiej strony!"));
                userSiteRepository.deleteByUserAndSite(user, site);
                site.setChosenAmount(site.getChosenAmount()-1);
                site.measurePopularity();
                siteRepository.save(site);
                return user;
            } catch (EmptyResultDataAccessException exception) {
                throw new ResourceNotFoundException("Podany użytkownik nie ma podanej strony!", exception);
            }
        });
    }
}
