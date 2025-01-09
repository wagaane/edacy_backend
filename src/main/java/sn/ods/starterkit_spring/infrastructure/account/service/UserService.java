package sn.ods.starterkit_spring.infrastructure.account.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import sn.ods.starterkit_spring.infrastructure.account.interfaces.IUserService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class UserService implements IUserService {

    private Map<String, String> resetTokens = new HashMap<>();
    private Map<String, Boolean> invalidatedTokens = new HashMap<>();



    public void generatePasswordResetToken(String email) {
        String token = UUID.randomUUID().toString();
        resetTokens.put(email, token);
        // Ici, tu enverrais un e-mail avec le token (simulé pour l'instant)
        System.out.println("Token de réinitialisation : " + token);
    }

    public void resetPassword(String token, String newPassword) {
        String email = resetTokens.entrySet().stream()
                .filter(entry -> entry.getValue().equals(token))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Token invalide"));

        // Simuler la mise à jour du mot de passe dans la base de données
        System.out.println("Mot de passe réinitialisé pour : " + email);
        resetTokens.remove(email);
    }

    public void invalidateToken(String token) {
        invalidatedTokens.put(token, true);
        System.out.println("Token invalidé : " + token);
    }


}
