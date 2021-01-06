package com.footballnewsmanager.backend.controllers;


import com.fasterxml.jackson.annotation.JsonView;
import com.footballnewsmanager.backend.api.request.auth.ValidationMessage;
import com.footballnewsmanager.backend.api.request.user_settings.*;
import com.footballnewsmanager.backend.api.response.BaseResponse;
import com.footballnewsmanager.backend.api.response.profile.UserProfileResponse;
import com.footballnewsmanager.backend.auth.JwtTokenProvider;
import com.footballnewsmanager.backend.auth.UserPrincipal;
import com.footballnewsmanager.backend.exceptions.BadRequestException;
import com.footballnewsmanager.backend.exceptions.ResourceNotFoundException;
import com.footballnewsmanager.backend.models.*;
import com.footballnewsmanager.backend.repositories.*;
import com.footballnewsmanager.backend.services.NewsService;
import com.footballnewsmanager.backend.services.UserService;
import com.footballnewsmanager.backend.views.Views;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.EmptyResultDataAccessException;
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
    private final UserTeamRepository userTeamRepository;
    private final UserSiteRepository userSiteRepository;
    private final BlacklistTokenRepository blacklistTokenRepository;
    private final RoleRepository roleRepository;
    private final TeamRepository teamRepository;
    private final SiteRepository siteRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;
    private final NewsRepository newsRepository;
    private final UserNewsRepository userNewsRepository;

    public UserController(UserRepository userRepository, UserTeamRepository userTeamRepository, TeamRepository teamRepository, UserSiteRepository userSiteRepository, JwtTokenProvider tokenProvider, BlacklistTokenRepository blacklistTokenRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder, UserService userService, SiteRepository siteRepository, NewsRepository newsRepository, UserNewsRepository userNewsRepository) {
        this.userRepository = userRepository;
        this.userTeamRepository = userTeamRepository;
        this.userSiteRepository = userSiteRepository;
        this.blacklistTokenRepository = blacklistTokenRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
        this.teamRepository = teamRepository;
        this.siteRepository = siteRepository;
        this.newsRepository = newsRepository;
        this.userNewsRepository = userNewsRepository;
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
    public ResponseEntity<UserProfileResponse> getMyProfile() {
        Long id = ((UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
        User user = userRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Nie ma takiego użytkownika")
        );

        Long likes = userNewsRepository.countDistinctByUserAndLikedIsTrue(user);
        Long favouritesCount = userTeamRepository.countByUserAndFavouriteIsTrue(user);
        UserProfileResponse userProfileResponse = new UserProfileResponse(user, likes, favouritesCount);

        return ResponseEntity.ok(userProfileResponse);
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
            for (Team teamFromRequest : userSettingsRequest.getFavouriteTeams()) {
                Team team = teamRepository.findById(teamFromRequest.getId()).orElseThrow(() -> new ResourceNotFoundException("Nie ma takiej drużyny"));
                UserTeam userTeam = userTeamRepository.findByUserAndTeam(userFromDB, team)
                        .orElse(new UserTeam());
                userTeam.setFavourite(!userTeam.isFavourite());
                team.setChosenAmount(team.getChosenAmount() + 1);
                team.measurePopularity();
                teamRepository.save(team);
                userTeamRepository.save(userTeam);
                NewsService.addNewsToFavourites(userNewsRepository, userFromDB, team);
            }
            return userFromDB;
        });
        userRepository.save(user);
        return user;
    }

//    @PostMapping("me/email")
//    public ResponseEntity<BaseResponse> changeEmail(@Valid @RequestBody EmailChangeRequest emailChangeRequest) {
//        User user = userService.checkUserExistByTokenAndOnSuccess(userRepository, userFromDB -> {
//            if (emailChangeRequest.getOldCredential().equals(userFromDB.getEmail())) {
//                if (!userRepository.existsByEmail(emailChangeRequest.getNewCredential())) {
//                    userFromDB.setEmail(emailChangeRequest.getNewCredential());
//                    return userFromDB;
//                } else throw new BadRequestException("Na podany adres email jest już utworzone konto!");
//            } else throw new BadRequestException("Podany adres email jest nieprawidłowy!");
//        });
//        userRepository.save(user);
//        return ResponseEntity.ok(new BaseResponse(true, "Poprawnie zmieniono adres mailowy!"));
//    }
//
//    @PostMapping("me/username")
//    public ResponseEntity<BaseResponse> changeUsername(@Valid @RequestBody UsernameChangeRequest usernameChangeRequest) {
//        User user = userService.checkUserExistByTokenAndOnSuccess(userRepository, userFromDB -> {
//            if (usernameChangeRequest.getOldCredential().equals(userFromDB.getUsername())) {
//                if (!userRepository.existsByUsername(usernameChangeRequest.getNewCredential())) {
//                    userFromDB.setUsername(usernameChangeRequest.getNewCredential());
//                    return userFromDB;
//                } else throw new BadRequestException("Podana nazwa użytkownika jest już zajęta!");
//            } else throw new BadRequestException("Nieprawidłowa nazwa użytkownika!");
//        });
//        userRepository.save(user);
//        return ResponseEntity.ok(new BaseResponse(true, "Poprawnie zmieniono nazwę użytkownika!"));
//    }


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


//    @PutMapping("me/toggleTeams")
//    @JsonView(Views.Public.class)
//    public User addTeams(@RequestBody TeamsRequest teamsRequest) {
//        return userService.checkUserExistByTokenAndOnSuccess(userRepository, user -> {
//            for (Team teamFromRequest : teamsRequest.getTeams()) {
//                Team team = teamRepository.findById(teamFromRequest.getId()).orElseThrow(() -> new ResourceNotFoundException("Nie ma takiej drużyny"));
//                UserTeam userTeam = userTeamRepository.findByUserAndTeam(user, team)
//                        .orElse(new UserTeam());
//                userTeam.setFavourite(!userTeam.isFavourite());
//                Long amount = team.getChosenAmount();
//                team.setChosenAmount(userTeam.isFavourite() ? amount + 1 : amount - 1);
//                team.measurePopularity();
//                teamRepository.save(team);
//                userTeamRepository.save(userTeam);
//                NewsService.toggleNewsToFavourites(newsRepository, userNewsRepository,
//                        user, team, true);
//            }
//            return user;
//        });
//    }


    @PutMapping("me/toggleTeam/{id}")
    @JsonView(Views.Public.class)
    public ResponseEntity<UserTeam> addTeam(@PathVariable("id") @NotNull @Min(value = 0) Long id) {
        AtomicReference<UserTeam> userTeam = new AtomicReference<>();
        userService.checkUserExistByTokenAndOnSuccess(userRepository, (user) -> {
            Team team = teamRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Nie ma takiej drużyny"));
            userTeam.set(userTeamRepository.findByUserAndTeam(user, team)
                    .orElseThrow(() -> new BadRequestException("Podana drużyna jest już dodana!")));
            userTeam.get().setFavourite(!userTeam.get().isFavourite());
            Long amount = team.getChosenAmount();
            team.setChosenAmount(userTeam.get().isFavourite() ? amount + 1 : amount - 1);
            team.measurePopularity();
            if (userTeam.get().isFavourite()) NewsService.addNewsToFavourites(userNewsRepository, user, team);
            else NewsService.deleteNewsFromFavourites(userNewsRepository, teamRepository, user, team);
            teamRepository.save(team);
            userTeamRepository.save(userTeam.get());


            return user;
        });
        return ResponseEntity.ok(userTeam.get());
    }

    //pomyślimy, może na sam koniec będzie taka opcja

    @PutMapping("me/addSite/{id}")
    @JsonView(Views.Public.class)
    public User addSite(@PathVariable("id") @NotNull @Min(value = 0) Long id) {
        return userService.checkUserExistByTokenAndOnSuccess(userRepository, (user) -> {
            Site site = siteRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Nie ma takiej strony!"));
            if (!userSiteRepository.findByUserAndSite(user, site).isPresent()) {
                UserSite userSite = new UserSite();
                userSite.setSite(site);
                userSite.setUser(user);
                site.setChosenAmount(site.getChosenAmount() + 1);
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
                site.setChosenAmount(site.getChosenAmount() - 1);
                site.measurePopularity();
                siteRepository.save(site);
                return user;
            } catch (EmptyResultDataAccessException exception) {
                throw new ResourceNotFoundException("Podany użytkownik nie ma podanej strony!", exception);
            }
        });
    }
}
