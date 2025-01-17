package sn.ods.starterkit_spring.application.services.implement.utilisateur;

import com.querydsl.core.BooleanBuilder;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import sn.ods.starterkit_spring.application.services.interfaces.utilisateur.UtilisateurService;
import sn.ods.starterkit_spring.application.services.shared.file.INotificationService;
import sn.ods.starterkit_spring.domain.model.utilisateur.Profile;
import sn.ods.starterkit_spring.domain.model.utilisateur.QUtilisateur;
import sn.ods.starterkit_spring.domain.model.utilisateur.Utilisateur;
import sn.ods.starterkit_spring.domain.repository.ProfilRepository;
import sn.ods.starterkit_spring.domain.repository.UtilisateurRepository;
import sn.ods.starterkit_spring.infrastructure.config.exceptions.APIException;
import sn.ods.starterkit_spring.infrastructure.config.password.PasswordGenerator;
import sn.ods.starterkit_spring.infrastructure.config.utils.UtilityClass;
import sn.ods.starterkit_spring.presentation.dto.requests.authencation.LoginFormDTO;
import sn.ods.starterkit_spring.presentation.dto.requests.utilisateur.UtilisateurReqDTO;
import sn.ods.starterkit_spring.presentation.dto.responses.APIResponse;
import sn.ods.starterkit_spring.presentation.dto.responses.Response;
import sn.ods.starterkit_spring.presentation.dto.responses.utilisateur.UtilisateurResDTO;
import sn.ods.starterkit_spring.presentation.mappers.utilisateur.UtilisateurMapper;

import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class UtilisateurServiceImpl implements UtilisateurService {

    private final UtilisateurRepository utilisateurRepository;
    private final INotificationService notificationService;
    private final UtilisateurMapper  utilisateurMapper;

    private final ProfilRepository profilRepository;

    public static final String FIRST_CONNEXION = "FIRST_CONNEXION";
    private static final String BLANK = " ";

    private final PasswordEncoder passwordEncoder;


    @Override
    @Transactional
    public Utilisateur createUser(UtilisateurReqDTO dto) {

        Utilisateur utilisateur = utilisateurMapper.toEntity(dto);

        Set<Profile> profiles = new HashSet<>();

        dto.getProfiles().forEach(profile -> {
            Optional<Profile> profileDB = profilRepository.findByCode(profile.getCode());
            profileDB.ifPresent(profiles::add);
        });

        utilisateur.setProfiles(profiles);

        utilisateur.setFirstLog(true);
        utilisateur.setStatus(true);

        if (new UtilityClass.EmailUtility().validate(dto.getEmail())) {

           utilisateur.setEmail(dto.getEmail());
        } else {
           throw new APIException("Invalid email address");
        }


        String password = PasswordGenerator.generateRandomString();
        log.info("................password: = {}", password);

        utilisateur.setPassword(passwordEncoder.encode(password));

        var userSaved =  utilisateurRepository.save(utilisateur);


        if (userSaved.getEmail() != null) {
            notificationService.sendNotificationToNewUserRegistred(
                    new LoginFormDTO(userSaved.getEmail(), password), FIRST_CONNEXION);


        }


        return utilisateurRepository.save(utilisateur);
    }

    @Override
    public Utilisateur updateUser(Long id, UtilisateurReqDTO dto) {

        Utilisateur utilisateur = utilisateurRepository.findById(id)
                .orElseThrow(() -> new APIException("Invalid utilisateur"));


        utilisateur.setNom(dto.getNom());
        utilisateur.setPrenom(dto.getPrenom());
        utilisateur.setAdresse(dto.getAdresse());
        utilisateur.setTelephone(dto.getTelephone());
        utilisateur.setDateNaissance(dto.getDateNaissance());
        utilisateur.setSexe(dto.getSexe());
        utilisateur.setLieuDeNaissance(dto.getLieuDeNaissance());


        Set<Profile> profiles = new HashSet<>();
        dto.getProfiles().forEach(profile -> {
            Optional<Profile> profileDB = profilRepository.findByCode(profile.getCode());
            profileDB.ifPresent(profiles::add);
        });

        utilisateur.setProfiles(profiles);

        return utilisateurRepository.save(utilisateur);
    }

    @Override
    public Utilisateur getUser(Long id) {
        return utilisateurRepository.findById(id)
                .orElseThrow(() -> new APIException("L'id est incorrect"));
    }

    @Override
    public Response<Object> getUserPage(int page, int size, String filter) {

        Page<UtilisateurResDTO> utilisateurResDTOPage;
        BooleanBuilder builder = new BooleanBuilder();


        if (StringUtils.isNotBlank(filter)) {

            builder.andAnyOf(
                    QUtilisateur.utilisateur.email.containsIgnoreCase(filter),
                    QUtilisateur.utilisateur.prenom.containsIgnoreCase(filter),
                    QUtilisateur.utilisateur.nom.containsIgnoreCase(filter),
                    QUtilisateur.utilisateur.adresse.containsIgnoreCase(filter),
                    QUtilisateur.utilisateur.sexe.containsIgnoreCase(filter),
                    QUtilisateur.utilisateur.lieuDeNaissance.containsIgnoreCase(filter));

        }

        utilisateurResDTOPage = Objects.nonNull(builder.getValue()) ? utilisateurRepository
                .findAll(builder.getValue(), PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id")))
                .map(utilisateurMapper::toDto)
                : utilisateurRepository.findAll(PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id")))
                .map(utilisateurMapper::toDto);

        Response.PageMetadata pageMetadata = Response.PageMetadata.builder()
                .size(utilisateurResDTOPage.getSize())
                .number(utilisateurResDTOPage.getNumber())
                .totalElements(utilisateurResDTOPage.getTotalElements())
                .totalPages(utilisateurResDTOPage.getTotalPages())
                .build();

        return Response.ok().setPayload(utilisateurResDTOPage.getContent()).setMetadata(pageMetadata)
                .setMessage("Liste des utilisateurs");

    }
}
