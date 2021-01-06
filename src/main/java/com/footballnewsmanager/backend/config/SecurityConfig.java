package com.footballnewsmanager.backend.config;


import com.footballnewsmanager.backend.services.CustomUserDetailsService;
import com.footballnewsmanager.backend.auth.JwtAuthenticationEntryPoint;
import com.footballnewsmanager.backend.auth.JwtAuthenticationFilter;
import com.footballnewsmanager.backend.auth.JwtTokenProvider;
import com.footballnewsmanager.backend.repositories.BlacklistTokenRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(
        securedEnabled = true,
        jsr250Enabled = true,
        prePostEnabled = true
)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    CustomUserDetailsService customUserDetailsService;

    JwtAuthenticationEntryPoint unauthorizedHandler;

    JwtTokenProvider jwtTokenProvider;

    BlacklistTokenRepository blacklistTokenRepository;

    public SecurityConfig(JwtAuthenticationEntryPoint unauthorizedHandler, CustomUserDetailsService customUserDetailsService,
                          JwtTokenProvider jwtTokenProvider, BlacklistTokenRepository blacklistTokenRepository
    ) {
        this.unauthorizedHandler = unauthorizedHandler;
        this.customUserDetailsService = customUserDetailsService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.blacklistTokenRepository = blacklistTokenRepository;
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(){
        return new JwtAuthenticationFilter(jwtTokenProvider, customUserDetailsService, blacklistTokenRepository);
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(customUserDetailsService).passwordEncoder(passwordEncoder());
    }

    @Bean(BeanIds.AUTHENTICATION_MANAGER)
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .cors()
                .and()
                .csrf()
                .disable()
                .exceptionHandling()
                .authenticationEntryPoint(unauthorizedHandler)
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .antMatchers("/auth/**")
                .permitAll()
                .anyRequest()
                .authenticated();

        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
    }
    @Bean
    public WebMvcConfigurer corsConfigurer(){
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("*")
                        .allowedMethods("*");
            }
        };
    }
}
