package sn.ods.starterkit_spring.application.services.implement.utilisateur;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import sn.ods.starterkit_spring.application.services.interfaces.utilisateur.ValidationUserService;
import sn.ods.starterkit_spring.application.services.implement.shared.file.INotificationService;
import sn.ods.starterkit_spring.domain.model.utilisateur.Utilisateur;
import sn.ods.starterkit_spring.domain.model.utilisateur.ValidationUser;
import sn.ods.starterkit_spring.domain.repository.utilisateur.ValidationUserRepository;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Random;

/**
 * @author Abdou Karim CISSOKHO
 * @created 17/01/2025-11:49
 * @project starterkit-spring
 */


@Service
@Slf4j
@RequiredArgsConstructor
public class ValidationUserServiceImpl implements ValidationUserService {

    private final ValidationUserRepository validationUserRepository;

    private final INotificationService notificationService;

    @Override
    public void validateUser(Utilisateur user) {
        try {
            ValidationUser validationUser = buildValidationUser(user);
            ValidationUser validationSaved = validationUserRepository.save(validationUser);
            notificationService.envoyer(validationSaved);
        } catch (Exception e) {
            throw new RuntimeException("Exception lors de la validation de l'utilisateur : " + e.getMessage(), e);
        }
    }

    /**
     * Construit un objet ValidationUser avec les informations nécessaires.
     */
    private ValidationUser buildValidationUser(Utilisateur user) {
        Instant creation = Instant.now();
        Instant expiration = creation.plus(30, ChronoUnit.MINUTES);

        String code = generateValidationCode();

        return ValidationUser.builder()
                .user(user)
                .creation(creation)
                .expiration(expiration)
                .code(code)
                .build();
    }

    /**
     * Génère un code de validation à 6 chiffres.
     */
    private String generateValidationCode() {
        return String.format("%06d", new Random().nextInt(999999));
    }

    @Override
    public ValidationUser readCode(String code) {
      return   validationUserRepository.findByCode(code).orElseThrow(() ->
                new RuntimeException("Le code de validation est incorect: " + code));

    }
}
