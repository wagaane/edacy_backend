package sn.ods.starterkit_spring.application.services.implement.utilisateur;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import sn.ods.starterkit_spring.application.services.interfaces.utilisateur.ValidationUserService;
import sn.ods.starterkit_spring.application.services.shared.file.INotificationService;
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
            ValidationUser validationUser = new ValidationUser();

            validationUser.setUser(user);

            Instant creation = Instant.now();

            validationUser.setCreation(creation);

            Instant expiration = creation.plus(30, ChronoUnit.MINUTES);

            validationUser.setExpiration(expiration);

            Random random = new Random();

            int randomInt =  random.nextInt(9999999);

            String code = String.format("%06d", randomInt);


            validationUser.setCode(code);


            ValidationUser validationSaved = validationUserRepository.save(validationUser);


            notificationService.envoyer(validationSaved);


        }catch (Exception e){
          throw new RuntimeException("Exception" + e.getMessage());
        }



    }

    @Override
    public ValidationUser readCode(String code) {
      return   validationUserRepository.findByCode(code).orElseThrow(() ->
                new RuntimeException("Le code de validation est incorect: " + code));

    }
}
