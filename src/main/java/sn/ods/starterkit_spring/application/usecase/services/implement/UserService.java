package sn.ods.starterkit_spring.application.usecase.services.implement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import sn.ods.starterkit_spring.domain.model.Utilisateur;
import sn.ods.starterkit_spring.domain.repository.IUtilisateurRepository;
import sn.ods.starterkit_spring.application.usecase.services.interfaces.IUserService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class UserService implements IUserService {

    private Map<String, String> resetTokens = new HashMap<>();
    private Map<String, Boolean> invalidatedTokens = new HashMap<>();

    //@Value("${frontend.reset-password-url}")
    private String resetUrl;
    @Autowired
    PasswordEncoder passwordEncoder ;
    @Autowired
    private IUtilisateurRepository utilisateurRepository;
    @Autowired
    private JavaMailSender mailSender;

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

    @Override
    public void sendPasswordResetEmail(String email) {
        Utilisateur user = utilisateurRepository.findByEmail(email);

        String token = UUID.randomUUID().toString();
//        user.setResetToken(token);
//        user.setTokenExpiration(Instant.now().plus(Duration.ofHours(1)));
//        utilisateurRepository.save(user);

        String resetLink = resetUrl + "?token=" + token;
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmail());
        message.setSubject("Réinitialisation de votre mot de passe");
        message.setText("Cliquez sur le lien suivant pour réinitialiser votre mot de passe : " + resetLink);
        mailSender.send(message);
    }

    @Override
    public Utilisateur createUser(String email, String password, String firstName,  String phoneNumber) {
            if (utilisateurRepository.findByEmail(email) != null) {
                throw new RuntimeException("Cet email est déjà utilisé");
            }

            Utilisateur user = new Utilisateur();
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode(password));  // Hacher le mot de passe avant de le stocker
            user.setNom(firstName);

            return utilisateurRepository.save(user);

    }


}
