package sn.ods.starterkit_spring.application.services.implement.authentication;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
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
import sn.ods.starterkit_spring.domain.repository.UtilisateurRepository;
import sn.ods.starterkit_spring.infrastructure.config.exceptions.APIException;
import sn.ods.starterkit_spring.infrastructure.config.security.jwt.JwtProvider;
import sn.ods.starterkit_spring.infrastructure.config.security.services.LoginAttemptService;
import sn.ods.starterkit_spring.infrastructure.config.utils.UtilityClass;
import sn.ods.starterkit_spring.infrastructure.config.utils.i18n.I18nTranslate;
import sn.ods.starterkit_spring.presentation.dto.requests.authencation.ForgetFormDTO;
import sn.ods.starterkit_spring.presentation.dto.requests.authencation.InitialAuthenticationDTO;
import sn.ods.starterkit_spring.presentation.dto.requests.authencation.LoginFormDTO;
import sn.ods.starterkit_spring.presentation.dto.requests.authencation.ResetOrForgetFormDTO;
import sn.ods.starterkit_spring.presentation.dto.responses.APIMessage;
import sn.ods.starterkit_spring.presentation.dto.responses.APIResponse;
import sn.ods.starterkit_spring.presentation.dto.responses.Response;
import sn.ods.starterkit_spring.presentation.dto.responses.Status;
import sn.ods.starterkit_spring.presentation.dto.responses.authentication.JwtDTO;
import sn.ods.starterkit_spring.presentation.mappers.utilisateur.UtilisateurMapper;

import java.util.Optional;

import static sn.ods.starterkit_spring.infrastructure.config.utils.i18n.I18nKeys.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationServiceImpl implements AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final UtilisateurMapper utilisateurMapper;
    private final JwtProvider jwtProvider;
    private final I18nTranslate i18nTranslat;
    private final LoginAttemptService loginAttemptService;
    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder encoder;
    private final INotificationService notificationService;

    public static final String BEARER = "Bearer";
    public static final String REFRESH_TOKEN = "Refresh token";
    private static final String RESET_PASSWORD = "RESET_PASSWORD";

    @Override
    public JwtDTO singIn(LoginFormDTO loginFormDTO) {
        Authentication authentication = authenticateUser(loginFormDTO);
        if (authentication == null) {

            throw new APIException(APIMessage.LOGIN_BAD_CREDENTIALS);

        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String jwt = jwtProvider.generateToken(authentication);
        String refreshToken = jwtProvider.generateRefreshToken(jwt);
        JwtDTO response = new JwtDTO(userDetails.getUsername(), jwt, refreshToken, BEARER);
        loginAttemptService.loginSucceeded(loginFormDTO.login());
        return response;
    }

    private Authentication authenticateUser(LoginFormDTO loginFormDTO) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginFormDTO.login(), loginFormDTO.password()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            return authentication;
        } catch (BadCredentialsException e) {
            loginAttemptService.loginFailed(loginFormDTO.login());
            return null;
        }
    }

    @Override
    public JwtDTO refreshToken(String token) {
        if (!jwtProvider.validationJwtToken(token)) {
            throw new APIException(APIMessage.CONNEXION_TOKEN_INVALIDE);
        }
        String jwtRefresh = jwtProvider.generateRefreshToken(token);
        return new  JwtDTO(jwtProvider.getUserNameFromJwtToken(token), jwtRefresh, null, BEARER);

    }

      @Override
      @Transactional
      public ResponseEntity<APIResponse> authenticateUserWithFirstUrlConnexion(InitialAuthenticationDTO formRequest) {
        if (isValidPassword(formRequest.newPassword())) {
            Optional<Utilisateur> userOptional = utilisateurRepository.findUtilisateurByEmail(formRequest.login());
            if (userOptional.isEmpty()) {

                APIResponse response = APIResponse.error(new APIException("L'Utilisateur  "+ formRequest.login()+ " n'existe pas."));
                return ResponseEntity.ok(response);

            }

            Utilisateur user = userOptional.get();
            if (Boolean.FALSE.equals(user.getFirstLog())) {
                APIResponse response = APIResponse.error(APIMessage.USER_COMPTE_ALREADY_CONNECTED);
                return ResponseEntity.ok(response);

            }

            if (Boolean.FALSE.equals(user.getStatus())) {
                APIResponse response = APIResponse.error(APIMessage.USER_COMPTE_DISABLED);
                return ResponseEntity.ok(response);
            }

            var passwordUpdateResponse = updatePassword(formRequest);
            if (passwordUpdateResponse.getStatus().equals(Status.OK)) {
                LoginFormDTO loginForm = new LoginFormDTO(formRequest.login(), formRequest.newPassword());
                APIResponse response = APIResponse.success(singIn(loginForm));
                return ResponseEntity.ok(response);
            }


            APIResponse response = APIResponse.success(passwordUpdateResponse);
            return ResponseEntity.ok(response);
        }
        throw new APIException(APIMessage.NON_STRONG_PASSWORD);
    }

    @Override
    @Transactional
    public JwtDTO authenticateUserWithForgetPasswordUrlConnexion(ForgetFormDTO formRequest) {
        if (!formRequest.newPassword().equals(formRequest.passwordConfirmed())) {
            throw new APIException(APIMessage.NON_MATCH_PASSWORD_CONFIRMED);
        }

        if (isValidPassword(formRequest.newPassword())) {

            Optional<Utilisateur> userOptional = utilisateurRepository.findUtilisateurByEmail(formRequest.login());
            if (userOptional.isEmpty()) {
               throw new APIException(APIMessage.ACCOUNT_NOT_FOUND);
            }

            var passwordUpdateResponse = updatePassword(formRequest);

            if (passwordUpdateResponse.getStatus().equals(Status.OK)) {

                LoginFormDTO loginForm = new LoginFormDTO(formRequest.login(), formRequest.newPassword());
                return singIn(loginForm);
            }
        }

        throw new APIException(APIMessage.NON_STRONG_PASSWORD);
    }

    @Override
    public  Utilisateur reinitPassword(String login) {
        Optional<Utilisateur> userOptional = utilisateurRepository.findUtilisateurByEmail(login);
        if (userOptional.isPresent()) {
            Utilisateur utilisateur = userOptional.get();
            utilisateur.setFirstLog(true);
            notificationService.sendNotificationToUserForgetPassword(
                    new LoginFormDTO(utilisateur.getEmail(), utilisateur.getPassword()), RESET_PASSWORD);

         return utilisateur;

        }
       throw new APIException(APIMessage.ACCOUNT_NOT_FOUND);
    }

    @Override
    @Transactional
    public ResponseEntity<APIResponse> updatePasswordFromInterface(ResetOrForgetFormDTO form) {
        if (isValidPassword(form.newPassword())) {
            Optional<Utilisateur> userOptional = utilisateurRepository.findUtilisateurByEmail(form.login());
            if (userOptional.isPresent()) {
                Utilisateur utilisateur = userOptional.get();
                if (!encoder.matches(form.password(), utilisateur.getPassword())) {
                    throw new APIException(APIMessage.PASSWORD_OLD_PASSWORD_ARE_NOT_IDENTIQUE);

                }

                if (encoder.matches(form.newPassword(), utilisateur.getPassword())) {
                    throw new APIException(APIMessage.PASSWORD_OLD_PASSWORD_ARE_IDENTIQUE);
                }

                utilisateur.setPassword(encoder.encode(form.newPassword()));
                Utilisateur updatedUser = utilisateurRepository.save(utilisateur);

                APIResponse response = APIResponse.success(updatedUser);
                return ResponseEntity.ok(response);
            }
            throw new APIException(APIMessage.ACCOUNT_NOT_FOUND);
        }
        throw new APIException(APIMessage.NON_STRONG_PASSWORD);
    }

    @Override
    public Utilisateur getCurrentConnectedUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return utilisateurRepository.findUtilisateurByEmail(username).orElseThrow();
    }

    private boolean isValidPassword(String password) {
        return new UtilityClass.PasswordUtility().validate(password);
    }

    private Response<Object> updatePassword(Object form) {
        Utilisateur utilisateur = getUserFromForm(form);
        if (utilisateur != null) {

            utilisateur.setPassword(encoder.encode(getNewPassword(form)));

           // utilisateur.setFirstLog(false);
            Utilisateur updatedUser = utilisateurRepository.save(utilisateur);
            return Response.ok().setMessage(i18nTranslat.toTranslate(MOT_DE_PASSE_MODIFIER_AVEC_SUCCES))
                    .setPayload(updatedUser);
        }
        return Response.notFound().setMessage(i18nTranslat.toTranslate(UTILISATEUR_ABSENT));
    }

    private Utilisateur getUserFromForm(Object form) {
        String email = null;
        if (form instanceof InitialAuthenticationDTO) {
            email = ((InitialAuthenticationDTO) form).login();
        } else if (form instanceof ForgetFormDTO) {
            email = ((ForgetFormDTO) form).login();
        }
        return utilisateurRepository.findUtilisateurByEmail(email).orElse(null);
    }

    private String getNewPassword(Object form) {
        if (form instanceof InitialAuthenticationDTO) {
            return ((InitialAuthenticationDTO) form).newPassword();
        } else if (form instanceof ForgetFormDTO) {
            return ((ForgetFormDTO) form).newPassword();
        }
        return null;
    }
}