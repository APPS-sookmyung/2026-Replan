package com.yuan.replan.config;

import com.yuan.replan.service.GoogleUserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.http.HttpStatus;

@Configuration
public class SecurityConfig {
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http, GoogleUserService users) throws Exception {
        RequestMatcher api = request -> {
            String path = request.getRequestURI().substring(request.getContextPath().length());
            return path.matches("/(diary|todo|plans)(/.*)?") || path.startsWith("/api/");
        };
        return http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/index.html", "/css/**", "/js/**", "/favicon.ico",
                                "/oauth2/**", "/login/**", "/error").permitAll()
                        .anyRequest().authenticated())
                .exceptionHandling(errors -> errors
                        .defaultAuthenticationEntryPointFor(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED), api)
                        .defaultAuthenticationEntryPointFor(new LoginUrlAuthenticationEntryPoint("/index.html"),
                                request -> !api.matches(request)))
                .oauth2Login(login -> login.loginPage("/index.html")
                        .userInfoEndpoint(info -> info.oidcUserService(users))
                        .defaultSuccessUrl("/home.html", true))
                .logout(logout -> logout.logoutSuccessUrl("/index.html?logout")
                        .invalidateHttpSession(true).deleteCookies("JSESSIONID"))
                .build();
    }
}
