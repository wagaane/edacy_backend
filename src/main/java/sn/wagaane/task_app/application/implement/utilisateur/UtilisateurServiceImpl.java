package sn.wagaane.task_app.application.implement.utilisateur;

import com.querydsl.core.BooleanBuilder;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import sn.wagaane.task_app.application.interfaces.utilisateur.UtilisateurService;
import sn.wagaane.task_app.application.interfaces.utilisateur.ValidationUserService;
import sn.wagaane.task_app.application.implement.shared.file.INotificationService;
import sn.wagaane.task_app.domain.model.utilisateur.Profile;
import sn.wagaane.task_app.domain.model.utilisateur.QUtilisateur;
import sn.wagaane.task_app.domain.model.utilisateur.Utilisateur;
import sn.wagaane.task_app.domain.model.utilisateur.ValidationUser;
import sn.wagaane.task_app.domain.repository.utilisateur.ProfilRepository;
import sn.wagaane.task_app.domain.repository.utilisateur.UtilisateurRepository;
import sn.wagaane.task_app.infrastructure.config.exceptions.APIException;
import sn.wagaane.task_app.infrastructure.config.password.PasswordGenerator;
import sn.wagaane.task_app.infrastructure.config.utils.UtilityClass;
import sn.wagaane.task_app.presentation.dto.requests.authencation.LoginFormDTO;
import sn.wagaane.task_app.presentation.dto.requests.utilisateur.UserReqForAdminDTO;
import sn.wagaane.task_app.presentation.dto.requests.utilisateur.UserReqForUserDTO;
import sn.wagaane.task_app.presentation.dto.responses.APIMessage;
import sn.wagaane.task_app.presentation.dto.responses.Response;
import sn.wagaane.task_app.presentation.dto.responses.utilisateur.UserResForAdminDTO;
import sn.wagaane.task_app.presentation.mappers.utilisateur.UserMapperForAdminMapper;
import sn.wagaane.task_app.presentation.mappers.utilisateur.UserMapperForUserMapper;

import java.time.Instant;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class  UtilisateurServiceImpl implements UtilisateurService {

    private final UtilisateurRepository utilisateurRepository;
    private final INotificationService notificationService;
    private final UserMapperForAdminMapper userMapperForAdminMapper;
    private final UserMapperForUserMapper userMapperForUserMapper;

    private final ValidationUserService validationUserService;

    private final ProfilRepository profilRepository;

    public static final String FIRST_CONNEXION = "FIRST_CONNEXION";

    private final PasswordEncoder passwordEncoder;


    @Override
    public Utilisateur getUser(Long id) {
        return utilisateurRepository.findById(id)
                .orElseThrow(() -> new APIException(APIMessage.ACCOUNT_NOT_FOUND));
    }

    @Override
    public Response<Object> getUserPage(int page, int size, String filter) {

        Page<UserResForAdminDTO> utilisateurResDTOPage;
        BooleanBuilder builder = new BooleanBuilder();


        if (StringUtils.isNotBlank(filter)) {

            builder.andAnyOf(
                    QUtilisateur.utilisateur.email.containsIgnoreCase(filter),
                    QUtilisateur.utilisateur.prenom.containsIgnoreCase(filter),
                    QUtilisateur.utilisateur.nom.containsIgnoreCase(filter)
            );

        }

        utilisateurResDTOPage = Objects.nonNull(builder.getValue()) ? utilisateurRepository
                .findAll(builder.getValue(), PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id")))
                .map(userMapperForAdminMapper::toDto)
                : utilisateurRepository.findAll(PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id")))
                .map(userMapperForAdminMapper::toDto);

        Response.PageMetadata pageMetadata = Response.PageMetadata.builder()
                .size(utilisateurResDTOPage.getSize())
                .number(utilisateurResDTOPage.getNumber())
                .totalElements(utilisateurResDTOPage.getTotalElements())
                .totalPages(utilisateurResDTOPage.getTotalPages())
                .build();

        return Response.ok().setPayload(utilisateurResDTOPage.getContent()).setMetadata(pageMetadata)
                .setMessage("Liste des utilisateurs");

    }

    @Override
    public void activation(Map<String, String> activation) {


            ValidationUser validationUser =validationUserService.readCode(activation.get("code"));

            if(Instant.now().isAfter(validationUser.getExpiration())){

                throw new APIException(APIMessage.INVALID_CODE_EXPIRED);
            }

            Utilisateur utilisateurActivate =   utilisateurRepository.findById(validationUser.getUser().getId())
                    .orElseThrow(() -> new APIException(APIMessage.ACCOUNT_ALREADY_EXIST));

            if(Boolean.TRUE.equals(utilisateurActivate.getStatus())){

                throw new APIException(APIMessage.ACCOUNT_ALREADY_ACTIVATED);
            }
            utilisateurActivate.setStatus(true);

            String password = PasswordGenerator.generateRandomString();
            log.info("................password: = {}", password);

            utilisateurActivate.setPassword(passwordEncoder.encode(password));

            Utilisateur activatedUser = utilisateurRepository.save(utilisateurActivate);

            notificationService.sendNotificationToNewUserRegistred(
                    new LoginFormDTO(activatedUser.getEmail(), password), FIRST_CONNEXION);



    }
}
