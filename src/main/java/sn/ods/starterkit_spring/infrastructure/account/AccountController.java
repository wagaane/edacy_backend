package sn.ods.starterkit_spring.infrastructure.account;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import sn.ods.starterkit_spring.infrastructure.account.model.AuthRequest;
import sn.ods.starterkit_spring.infrastructure.account.model.AuthResponse;
import sn.ods.starterkit_spring.infrastructure.account.model.ForgotPasswordRequest;
import sn.ods.starterkit_spring.infrastructure.account.model.ResetPasswordRequest;
import sn.ods.starterkit_spring.infrastructure.account.service.UserService;
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
        String token = jwtProvider.generateToken(request.getEmail());
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

}
