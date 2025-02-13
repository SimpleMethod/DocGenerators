package pl.mlodawski.docgenerator.config.session;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import pl.mlodawski.docgenerator.config.app.AppConfig;

/*
 @Author Michał Młodawski
 */

/**
 * Configuration class for session management settings.
 *
 * This class provides configuration for session cookies used in the application.
 * The configuration defines cookie properties such as SameSite attribute, security flags,
 * and HttpOnly settings for enhanced control over session management behavior.
 */
@Configuration
public class SessionConfig {


    @Bean
    public CookieSerializer cookieSerializer() {
        DefaultCookieSerializer serializer = new DefaultCookieSerializer();
        serializer.setSameSite("Lax");
        serializer.setUseSecureCookie(false); //TODO: change to true in production
        serializer.setCookieName("SESSIONID");
        serializer.setCookiePath("/");
        return serializer;
    }

    @Bean
    public CorsFilter corsFilter(AppConfig appConfig) {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowCredentials(true);
        config.addAllowedOrigin(appConfig.getFrontendUrl());
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");

        source.registerCorsConfiguration("/api/**", config);
        return new CorsFilter(source);
    }
}
