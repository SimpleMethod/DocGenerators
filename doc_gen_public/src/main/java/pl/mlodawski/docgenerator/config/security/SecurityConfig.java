package pl.mlodawski.docgenerator.config.security;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.SessionManagementConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.session.CompositeSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.RegisterSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionFixationProtectionStrategy;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import pl.mlodawski.docgenerator.config.app.AppConfig;

import java.util.Arrays;
import java.util.List;

import static org.springframework.security.config.Customizer.withDefaults;

/*
 @Author Michał Młodawski
 */

@Slf4j
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(
        prePostEnabled = true,
        securedEnabled = true,
        jsr250Enabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final SessionRegistry springSessionBackedSessionRegistry;
    private final AppConfig appConfig;


    /**
     * Configures the security filter chain for the application, establishing security policies such as authorization,
     * session management, CSRF handling, and exception handling.
     *
     * @param http the {@link HttpSecurity} object used to configure the security filters
     * @return the configured {@link SecurityFilterChain} object
     * @throws Exception if an error occurs during the configuration process
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(requests -> requests
                        .requestMatchers("/api/v2/**").permitAll()
                        .requestMatchers("/api/v1/**").permitAll()
                        .requestMatchers("/swagger-ui/**").permitAll()
                        .requestMatchers("/v3/api-docs/**").permitAll()

                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> {
                    session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED);
                    session.maximumSessions(1)
                            .sessionRegistry(springSessionBackedSessionRegistry)
                            .expiredUrl("/session-expired");
                    session.sessionFixation(SessionManagementConfigurer.SessionFixationConfigurer::migrateSession);
                    session.sessionAuthenticationStrategy(sessionAuthenticationStrategy());
                })
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .securityContext(context -> context
                        .securityContextRepository(new HttpSessionSecurityContextRepository())
                        .requireExplicitSave(true)
                )
                .headers(headers -> headers
                        .contentTypeOptions(withDefaults())
                        .xssProtection(withDefaults())
                        .cacheControl(withDefaults())
                        .httpStrictTransportSecurity(withDefaults())
                        .frameOptions(withDefaults())
                )
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
                        })
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Forbidden");
                        })
                );

        return http.build();
    }

    /**
     * Defines and configures the session authentication strategy bean.
     * It combines multiple session authentication strategies, including registering session authentication and
     * session fixation protection, to ensure secure and consistent session management.
     *
     * @return a configured instance of SessionAuthenticationStrategy that combines multiple strategies for session handling.
     */
    @Bean
    public SessionAuthenticationStrategy sessionAuthenticationStrategy() {
        return new CompositeSessionAuthenticationStrategy(Arrays.asList(
                new RegisterSessionAuthenticationStrategy(springSessionBackedSessionRegistry),
                new SessionFixationProtectionStrategy()
        ));
    }

    /**
     * Configures and provides a CorsConfigurationSource bean, defining CORS settings
     * to control resource sharing policies for specific origins, methods, headers,
     * and other CORS parameters.
     *
     * @return a configured CorsConfigurationSource that applies the defined CORS policies
     *         to incoming requests.
     */
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(appConfig.getAllowedOrigins());
        configuration.setAllowedMethods(Arrays.asList(
                "GET", "POST", "PATCH", "PUT", "DELETE", "OPTIONS", "HEAD"
        ));
        configuration.setAllowCredentials(true);
        configuration.setAllowedHeaders(Arrays.asList(
                "Origin", "Content-Type", "Accept", "Authorization",
                "Access-Control-Allow-Origin", "Access-Control-Allow-Headers",
                "Access-Control-Allow-Methods"
        ));
        configuration.setExposedHeaders(List.of(
                "Access-Control-Allow-Origin",
                "Access-Control-Allow-Credentials"
        ));
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }


}