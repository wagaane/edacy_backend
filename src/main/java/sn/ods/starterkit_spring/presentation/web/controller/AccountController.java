package sn.ods.starterkit_spring.presentation.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import sn.ods.starterkit_spring.domain.model.Utilisateur;
import sn.ods.starterkit_spring.presentation.dto.requests.utilisateur.AuthRequest;
import sn.ods.starterkit_spring.presentation.dto.responses.AuthResponse;
import sn.ods.starterkit_spring.presentation.dto.requests.utilisateur.ForgotPasswordRequest;
import sn.ods.starterkit_spring.presentation.dto.requests.utilisateur.ResetPasswordRequest;
import sn.ods.starterkit_spring.application.usecase.services.implement.UserService;
import sn.ods.starterkit_spring.infrastructure.config.security.jwt.JwtProvider;

@RestController
@RequiredArgsConstructor
@RequestMapping("/account")
public class AccountController {

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtProvider jwtProvider;
    @Autowired
    private UserService userService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        String token = jwtProvider.generateToken( request.getEmail());
        return ResponseEntity.ok(new AuthResponse(token));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String token) {
        userService.invalidateToken(token);
        return ResponseEntity.ok("Déconnexion réussie");
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        userService.sendPasswordResetEmail(request.getEmail());
        return ResponseEntity.ok("Email de réinitialisation envoyé");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) {
        userService.resetPassword(request.getToken(), request.getNewPassword());
        return ResponseEntity.ok("Mot de passe réinitialisé");
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody Utilisateur request) {
        try {
            userService.createUser(request.getEmail(), request.getPassword(), request.getNom() , request.getTelephone());
            return ResponseEntity.ok("Utilisateur créé avec succès");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

}
