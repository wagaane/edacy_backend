package sn.wagaane.task_app.infrastructure.config.security;


import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import sn.wagaane.task_app.infrastructure.config.security.jwt.JwtAuthEntryPoint;
import sn.wagaane.task_app.infrastructure.config.security.jwt.JwtAuthTokenFilter;

import java.util.List;

/**
 * @author G2k R&D
 */
@Configuration
@EnableWebSecurity
@EnableTransactionManagement
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig  {

    //private final UtilisateurDetailsSerciveImpl utilisateurDetailsSercive;
    private final JwtAuthTokenFilter jwtAuthTokenFilter;
    private final JwtAuthEntryPoint jwtAuthEntryPoint;

    private static final RequestMatcher[] AUTH_WHITELIST = {
            new AntPathRequestMatcher("/otp/**"),
            new AntPathRequestMatcher("/auth/**"),
            new AntPathRequestMatcher("/o-sign/**"),
            new AntPathRequestMatcher("/cloud-sign/**"),
            new AntPathRequestMatcher("/contact/**"),
            new AntPathRequestMatcher("/logs/**"),
            new AntPathRequestMatcher("/api/v1/landing-page/emsigner/authenticate/**"),
            new AntPathRequestMatcher("/swagger-ui.html"),
            new AntPathRequestMatcher("/swagger-ui/**"),
            new AntPathRequestMatcher("/v3/api-docs/**"),
            new AntPathRequestMatcher("/WEB-INF/classes/images/**"),
            new AntPathRequestMatcher("/static/**"),
            new AntPathRequestMatcher("/services/list-public-services-no-page"),
            new AntPathRequestMatcher("/services/get-service/public/**"),
            new AntPathRequestMatcher("/gestionDocumentation/articles/public/**"),



    };

    @Bean
    public SecurityFilterChain securiConfigFiltre(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.csrf(AbstractHttpConfigurer::disable)
                .cors(httpSecurityCorsConfigurer ->
                        httpSecurityCorsConfigurer.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(request -> request
                        .requestMatchers(AUTH_WHITELIST).permitAll()
                        .anyRequest().authenticated())
                .sessionManagement(manager -> manager
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
               // .authenticationProvider(authenticationProvider())
                .exceptionHandling(exception -> exception.authenticationEntryPoint(jwtAuthEntryPoint))
                .addFilterBefore(jwtAuthTokenFilter, UsernamePasswordAuthenticationFilter.class);
        return httpSecurity.build();
    }
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        //Make the below setting as * to allow connection from any hos
        corsConfiguration.setAllowedOrigins(List.of("http://localhost:4200","http://185.98.138.80/","http://192.168.10.23:4200/","http://180.149.197.68/"));
        corsConfiguration.setAllowedMethods(List.of("*"));
        corsConfiguration.setAllowCredentials(true);
        corsConfiguration.setAllowedHeaders(List.of("*"));
        corsConfiguration.setMaxAge(3600L);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);
        return source;
    }



}
