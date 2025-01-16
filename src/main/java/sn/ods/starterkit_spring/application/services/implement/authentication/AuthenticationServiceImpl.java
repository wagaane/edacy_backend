package sn.ods.starterkit_spring.application.services.implement.authentication;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import sn.ods.starterkit_spring.application.services.interfaces.authentication.AuthenticationService;
import sn.ods.starterkit_spring.application.services.shared.file.INotificationService;
import sn.ods.starterkit_spring.domain.model.utilisateur.Utilisateur;
import sn.ods.starterkit_spring.domain.repository.IUtilisateurRepository;
import sn.ods.starterkit_spring.infrastructure.config.exceptions.APIException;
import sn.ods.starterkit_spring.infrastructure.config.security.jwt.JwtProvider;
import sn.ods.starterkit_spring.infrastructure.config.security.services.LoginAttemptService;
import sn.ods.starterkit_spring.infrastructure.config.utils.UtilityClass;
import sn.ods.starterkit_spring.infrastructure.config.utils.i18n.I18nKeys;
import sn.ods.starterkit_spring.infrastructure.config.utils.i18n.I18nTranslate;
import sn.ods.starterkit_spring.presentation.dto.requests.authencation.LoginFormDTO;
import sn.ods.starterkit_spring.presentation.dto.requests.authencation.ResetOrForgetFormDTO;
import sn.ods.starterkit_spring.presentation.dto.responses.APIMessage;
import sn.ods.starterkit_spring.presentation.dto.responses.Response;
import sn.ods.starterkit_spring.presentation.dto.responses.Status;
import sn.ods.starterkit_spring.presentation.dto.responses.authentication.JwtDTO;
import sn.ods.starterkit_spring.presentation.mappers.utilisateur.UtilisateurMapper;

import java.util.Optional;

import static sn.ods.starterkit_spring.infrastructure.config.utils.i18n.I18nKeys.*;

/**
 * @author Abdou Karim CISSOKHO
 * @created 09/01/2025-16:20
 * @project starterkit-spring
 */

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationServiceImpl implements AuthenticationService {
    private final AuthenticationManager authenticationManager;
    // private final UtilisateurImpl utilisateurService;

    private final UtilisateurMapper utilisateurMapper;

    private final JwtProvider jwtProvider;
    private final I18nTranslate i18nTranslat;
    private final LoginAttemptService loginAttemptService;
    private final IUtilisateurRepository utilisateurRepository;
    private final PasswordEncoder encoder;
    private final INotificationService notificationService;

    public static final String BEARER = "Bearer";
    public static final String REFRESH_TOKEN = "Refresh token";
    private static final String RESET_PASSWORD = "RESET_PASSWORD";

  
    @Override
    public Response<Object> singIn(LoginFormDTO loginFormDTO) {
        Authentication authentication;
        try {

            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginFormDTO.login(), loginFormDTO.password()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            String jwt = jwtProvider.generateToken(authentication);
            JwtDTO response = new JwtDTO(userDetails.getUsername(), jwt, BEARER);
            loginAttemptService.loginSucceeded(loginFormDTO.login());
            return Response.ok().setPayload(response).setMessage(i18nTranslat.toTranslate(CONNEXION_LOGIN_CORRECT));
        } catch (BadCredentialsException e) {
            loginAttemptService.loginFailed(loginFormDTO.login());
            return Response.wrongCredentials().setMessage(i18nTranslat.toTranslate(CONNEXION_LOGIN_INCORRECT));
        }
    }

    @Override
    public Response<Object> refreshToken(String token) {
        if (!jwtProvider.validationJwtToken(token)) {
            throw new APIException(APIMessage.CONNEXION_TOKEN_INVALIDE);
        }
        String jwtRefresh = jwtProvider.generateRefreshToken(token);
        JwtDTO response = new JwtDTO(jwtProvider.getUserNameFromJwtToken(token), jwtRefresh, BEARER);
        return Response.ok().setPayload(response).setMessage(REFRESH_TOKEN);
    }

    @Override
    @Transactional
    public Response<Object> authenticateUserWithFirstUrlConnexion(ResetOrForgetFormDTO formRequest) {
        if (new UtilityClass.PasswordUtility().validate(formRequest.newPassword())) {

            try {

                Optional<Utilisateur> resp = utilisateurRepository.findUtilisateurByEmail(formRequest.login());
                if (resp.isEmpty())
                    return Response.notFound()
                            .setMessage(i18nTranslat.toTranslate(UTILISATEUR_ABSENT) + " : " + formRequest.login());

                Utilisateur user = resp.get();
                if (Boolean.FALSE.equals(user.getFirstLog()))
                    return Response.badRequest().setMessage(i18nTranslat.toTranslate(CONNEXION_LOGIN_RESET_FAIT));

                if (Boolean.FALSE.equals(user.getStatus()))
                    return Response.disabledAccount()
                            .setMessage(i18nTranslat.toTranslate(I18nKeys.CONNEXION_LOGIN_TENTATIVE));

                // System.out.println(formRequest);
                Response<Object> response = updatePassword(formRequest);
                // System.out.println("statu"+ response.getStatus());
                if (response.getStatus().equals(Status.OK)) {
                    // System.out.println("ici");
                    LoginFormDTO loginForm = new LoginFormDTO(formRequest.login(), formRequest.newPassword());

                    return singIn(loginForm);
                }
                return response;
            } catch(BadCredentialsException e){
                loginAttemptService.loginFailed(formRequest.login());
                return Response.wrongCredentials().setMessage(i18nTranslat.toTranslate(CONNEXION_LOGIN_INCORRECT));
            }
        }

        throw new APIException(APIMessage.NON_STRONG_PASSWORD);
    }

    @Override
    @Transactional
    public Response<Object> authenticateUserWithForgetPasswordUrlConnexion(ResetOrForgetFormDTO formRequest) {

        if(!formRequest.newPassword().equals(formRequest.passwordConfirmed())) {
            throw new APIException(APIMessage.NON_MATCH_PASSWORD_CONFIRMED);
        }
        if (new UtilityClass.PasswordUtility().validate(formRequest.newPassword())) {
            Optional<Utilisateur> resp = utilisateurRepository.findUtilisateurByEmail(formRequest.login());
            if (resp.isEmpty())
                Response.notFound().setMessage(i18nTranslat.toTranslate(UTILISATEUR_ABSENT) + " : " + formRequest.login());
            Response<Object> response = updatePassword(formRequest);

            if (response.getStatus().equals(Status.OK)) {
                LoginFormDTO loginForm = new LoginFormDTO(formRequest.login(), formRequest.newPassword());
                return singIn(loginForm);
            }

            return response;
        }
        throw new APIException(APIMessage.NON_STRONG_PASSWORD);
    }

    @Override
    public Response<Object> reinitPassword(String login) {

        Optional<Utilisateur> resp = utilisateurRepository.findUtilisateurByEmail(login);
        if (resp.isPresent()) {
            Utilisateur utilisateur = resp.get();
            notificationService.sendNotificationToUserForgetPassword(
                    new LoginFormDTO(utilisateur.getEmail(), utilisateur.getPassword()), RESET_PASSWORD);
            return Response.ok().setMessage(i18nTranslat.toTranslate(CONNEXION_LOGIN_RESET))
                    .setPayload(utilisateurMapper.toDto(utilisateur));
        }
        return Response.notFound().setMessage(i18nTranslat.toTranslate(CONNEXION_LOGIN_NOT_EXIST));
    }


    @Override
    @Transactional
    public Response<Object> updatePasswordFromInterface(ResetOrForgetFormDTO form) {
        if (new UtilityClass.PasswordUtility().validate(form.newPassword())) {
            Optional<Utilisateur> resp = utilisateurRepository.findUtilisateurByEmail(form.login());
            if (resp.isPresent() && !encoder.matches(form.password(), resp.get().getPassword()))
                return Response.badRequest().setMessage(i18nTranslat.toTranslate(PASSWORD_OLD_PASSWORD_ARE_NOT_IDENTIQUE));

            else if (resp.isPresent()) {
                Utilisateur utilisateur = resp.get();
                if (encoder.matches(form.newPassword(), utilisateur.getPassword()))
                    return Response.badRequest().setMessage(i18nTranslat.toTranslate(PASSWORD_NEW_PASSWORD_ARE_IDENTIQUE));

                utilisateur.setPassword(encoder.encode(form.newPassword()));
                Utilisateur updatedUser = utilisateurRepository.save(utilisateur);
                return Response.ok().setMessage(i18nTranslat.toTranslate(MOT_DE_PASSE_MODIFIER_AVEC_SUCCES))
                        .setPayload(utilisateurMapper.toDto(updatedUser));
            }
            return Response.notFound().setMessage(i18nTranslat.toTranslate(UTILISATEUR_ABSENT));
        }
        throw new APIException(APIMessage.NON_STRONG_PASSWORD);
    }

    @Override
    public Utilisateur getCurrentConnectedUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return utilisateurRepository.findUtilisateurByEmail(username).orElseThrow();

    }

    private Response<Object> updatePassword(ResetOrForgetFormDTO form) {
        Optional<Utilisateur> resp = utilisateurRepository.findUtilisateurByEmail(form.login());
        if (resp.isPresent()) {
            Utilisateur utilisateur = resp.get();
            utilisateur.setPassword(encoder.encode(form.newPassword()));
            utilisateur.setFirstLog(false);

            Utilisateur updatedUser = utilisateurRepository.save(utilisateur);
            return Response.ok().setMessage(i18nTranslat.toTranslate(MOT_DE_PASSE_MODIFIER_AVEC_SUCCES))
                    .setPayload(updatedUser);
        }

        return Response.notFound().setMessage(i18nTranslat.toTranslate(UTILISATEUR_ABSENT));
    }

}
