package sn.wagaane.task_app.application.services.otp;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import sn.wagaane.task_app.domain.model.utilisateur.Utilisateur;
import sn.wagaane.task_app.domain.repository.IUtilisateurRepository;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OtpService {
    @Value("${otp.expiration}")
    private int EXPIRE_MINUTES;
    private final Map<String, OtpEntry> otpStorage = new HashMap<>();
    private final IUtilisateurRepository utilisateurRepository;
    private final SecureRandom random = new SecureRandom();

    public String generateOtp(String key) {
        String otp = String.format("%05d", random.nextInt(99999));
        otpStorage.put(key, new OtpEntry(otp, System.currentTimeMillis()));
        return otp;
    }

    public boolean validateOtp(String key, String otp) {
        if (!otpStorage.containsKey(key)) return false;

        OtpEntry entry = otpStorage.get(key);

        long currentTime = System.currentTimeMillis();
        if (currentTime - entry.timestamp > (long) EXPIRE_MINUTES * 60 * 1000) {
            otpStorage.remove(key);
            return false;
        }

        boolean isValid = entry.otp.equals(otp);
        if (isValid) otpStorage.remove(key); // Use-once

        if (isValid) {
            Optional<Utilisateur> utilisateur = utilisateurRepository.findByEmailAndDeletedFalse(key);
            utilisateur.ifPresent(utilisateur1 -> {
                utilisateur1.setStatus(true);
                utilisateurRepository.save(utilisateur1);
            });
        }
        return isValid;
    }

    private static class OtpEntry {
        String otp;
        long timestamp;

        OtpEntry(String otp, long timestamp) {
            this.otp = otp;
            this.timestamp = timestamp;
        }
    }
}
