package sn.ods.starterkit_spring.infrastructure.config.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import sn.ods.starterkit_spring.infrastructure.config.security.services.UtilisateurPrinciple;
import sn.ods.starterkit_spring.presentation.dto.responses.mails.MailConnexionInfosDTO;


import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;




@Component
public class JwtProvider {
    @Value("${projet.securite.jwtSecret}")
    private String jwtSecret;
    @Value("${projet.securite.jwtExpiration}")
    private int jwtExpiration;
    @Value("${projet.securite.jwtRefreshToken}")
    private int jwtRefreshToken;

    private static final  String INFOS= "infos";

    public String generateToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(Date.from(Instant.now().plus(Duration.ofMinutes(jwtExpiration))))
                .signWith(getSignatureKey(), SignatureAlgorithm.HS256)
                .compact();
    }


    public String generateJwtMailToken(MailConnexionInfosDTO infos) {
        return Jwts.builder()
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + (long) jwtExpiration * 60 * 1000))
                .claim(INFOS, infos)
                .signWith(getSignatureKey())
                .compact();
    }

    public String generateRefreshToken(String token) {
        Claims claims = Jwts.parserBuilder().setSigningKey(getSignatureKey()).build().parseClaimsJws(token).getBody();
        return Jwts.builder()
                .setSubject(claims.getSubject())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + (long) jwtRefreshToken * 60 * 1000))
                .claim(INFOS, claims.get(INFOS))
                .signWith(getSignatureKey())
                .compact();
    }

    public boolean validationJwtToken(String authtoken) {
        try {
            Jwts.parserBuilder().setSigningKey(getSignatureKey()).build().parseClaimsJws(authtoken);
            return true;
        } catch (SignatureException | MalformedJwtException | ExpiredJwtException | UnsupportedJwtException |
                 IllegalArgumentException e) {
            return false;
        }
    }

    public String getUserNameFromJwtToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignatureKey()).build()
                .parseClaimsJws(token)
                .getBody().getSubject();
    }

    public SecretKey getSignatureKey() {
        return Keys.hmacShaKeyFor(
                jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

}
