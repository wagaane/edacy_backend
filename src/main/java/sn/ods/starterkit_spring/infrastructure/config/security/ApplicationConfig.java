package sn.ods.starterkit_spring.infrastructure.config.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;
import static org.springframework.http.HttpMethod.*;
import static org.springframework.web.bind.annotation.RequestMethod.PATCH;

@RequiredArgsConstructor
@Configuration
public class ApplicationConfig {

    private final UserDetailsService userDetailsService;

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12); // Force personnalisée
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();

        // Activer les credentials
        config.setAllowCredentials(true);

        // Définir les origines autorisées
        config.setAllowedOrigins(Arrays.asList(
                "http://localhost:4200",
                "http://localhost:8097/swagger-ui"
        ));

        // Définir les en-têtes autorisés
        config.setAllowedHeaders(Arrays.asList(
                "X-Requested-With",
                "Content-Type",
                "Authorization",
                "Origin",
                "Accept",
                "Access-Control-Request-Method",
                "Access-Control-Request-Headers"
        ));

        // Définir les méthodes HTTP autorisées
        config.setAllowedMethods(Arrays.asList(
                GET.name(),
                POST.name(),
                DELETE.name(),
                PUT.name(),
                PATCH.name(),
                OPTIONS.name()
        ));

        // Enregistrer la configuration pour toutes les routes
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }
}